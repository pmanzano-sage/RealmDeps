package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.CrudUtils
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.ContactPersonType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 *
 * This entity is basic and has no dependencies.
 */
class ContactPersonTypeCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    // Item used for the test
    private val enumItem = ContactPersonType.Companion.V3.CONTRACTOR
    private val id = enumItem.name
    private val updatedName = "updated name"
    private val updatedSymbol = "updated symbol"
    private val item = ContactPersonType.create(enumItem)
    private val invalidItemName = "XYZ"

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
        checkNumEntitiesIs(ContactPersonType::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)

        // These entities have the name fixed, so we can not do this:
        // item.name = updatedName
        // So we create a new entity with the same id and different name & symbol
        val updated = ContactPersonType(id, SyncStatus.SYNC_SUCCESS, updatedName, updatedSymbol)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(ContactPersonType::class.java, id) as ContactPersonType
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, id)
        Assert.assertEquals(fromDb.name, updatedName)
        Assert.assertEquals(fromDb.symbol, updatedSymbol)

        // Also check no new entities have been created
        checkNumEntitiesIs(ContactPersonType::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(ContactPersonType::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val invalidItem = CrudUtils.createInvalidEntity(ContactPersonType::class.java, invalidItemName)
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
        val existingItem = ContactPersonType(invalidItemName, SyncStatus.SYNC_SUCCESS, invalidItemName, invalidItemName)
        dataManager.save(existingItem)

        val invalidItem = CrudUtils.createInvalidEntity(ContactPersonType::class.java, invalidItemName)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(ContactPersonType::class.java, invalidItemName) as ContactPersonType
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, invalidItemName)
        Assert.assertEquals(fromDb.name, invalidItemName)
        Assert.assertEquals(fromDb.symbol, invalidItemName)

    }

    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    //endregion


}