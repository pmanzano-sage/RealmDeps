package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.CrudUtils
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.TaxScheme
import junit.framework.Assert

/**
 * @author Pablo Manzano
 *
 * This entity is basic and has no dependencies.
 */
class TaxSchemeCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    // Item used for the test
    private val enumItem = "Not Registered"
    private val id = enumItem
    private val updatedName = "updated name"
    private val item = TaxScheme.create(id, enumItem)
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
        checkNumEntitiesIs(TaxScheme::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)
        val updated = TaxScheme.create(id, updatedName)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(TaxScheme::class.java, id) as TaxScheme
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(id, fromDb.id)
        Assert.assertEquals(updatedName, fromDb.name)

        // Also check no new entities have been created
        checkNumEntitiesIs(TaxScheme::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDeleteContact() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(TaxScheme::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val invalidItem = CrudUtils.createInvalidEntity(TaxScheme::class.java, invalidItemName)
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
        val existingItem = TaxScheme.create(invalidItemName, invalidItemName)
        dataManager.save(existingItem)

        val invalidItem = CrudUtils.createInvalidEntity(TaxScheme::class.java, invalidItemName)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(TaxScheme::class.java, invalidItemName) as TaxScheme
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(invalidItemName, fromDb.id)
        Assert.assertEquals(invalidItemName, fromDb.name)

    }

    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    //endregion


}