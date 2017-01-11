package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.CrudUtils
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.TransactionType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 *
 * This entity is basic and has no dependencies.
 */
class TransactionTypeCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    // Item used for the test
    private val enumItem = TransactionType.Companion.V3.OTHER_RECEIPT
    private val id = enumItem.name
    private val updatedName = "updated name"
    private val updatedOrdinal = 3
    private val item = TransactionType.create(enumItem)

    /**
     * Start with a fresh db.
     */
    override fun setUp() {
        super.setUp()

        // Initialize Realm & DataManager
        Realm.init(getContext())
        val realmConfig = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(realmConfig)
        dataManager = RealmDataManager(Realm.getDefaultInstance())

        // Note that we'll start each test case with an empty db
        dataManager.deleteAll()
    }

    override fun tearDown() {
        super.tearDown()
    }

    /**
     * SAVE
     */
    fun testSave() {
        dataManager.save(item)
        checkNumEntitiesIs(TransactionType::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)
        val updated = TransactionType(id, SyncStatus.SYNC_SUCCESS, updatedName, updatedOrdinal)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(TransactionType::class.java, id) as TransactionType
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(updatedName, fromDb.name)
        Assert.assertEquals(updatedOrdinal, fromDb.ordinal)

        // Also check no new entities have been created
        checkNumEntitiesIs(TransactionType::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDeleteContact() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(TransactionType::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val invalidItem = CrudUtils.createInvalidEntity(TransactionType::class.java, enumItem.name)
        try {
            dataManager.save(invalidItem)
            Assert.fail("Should have thrown a validation exception")
        } catch(e: Exception) {
        }
    }

    /**
     * DEPENDENCY LOOKUP
     */
    fun testDependencyLookup() {
        // Insert an item into the db
        dataManager.save(item)

        val invalidItem = CrudUtils.createInvalidEntity(TransactionType::class.java, enumItem.name)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(TransactionType::class.java, enumItem.name) as TransactionType
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(enumItem.displayName, fromDb.name)

    }

    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    //endregion


}