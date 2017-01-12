package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.Country
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 *
 * This entity is basic and has no dependencies.
 */
class CountryCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    // Item used for the test
    private val enumItem = Country.Companion.Code.US
    private val id = enumItem.name
    private val updatedName = "updated name"
    private val updatedCode = "IE"
    private val item = Country.create(enumItem)

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
        checkNumEntitiesIs(Country::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)

        // These entities have the name fixed, so we can not do this:
        // item.name = updatedName
        // So we create a new entity with the same id and different name & symbol
        val updated = Country(id, SyncStatus.SYNC_SUCCESS, updatedCode, updatedName)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Country::class.java, id) as Country
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, id)
        Assert.assertEquals(fromDb.name, updatedName)
        Assert.assertEquals(fromDb.code, updatedCode)

        // Also check no new entities have been created
        checkNumEntitiesIs(Country::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(Country::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val invalidItem = createInvalidCountry(updatedCode)
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
        val existingItem = Country(updatedCode, SyncStatus.SYNC_SUCCESS, updatedCode, updatedCode)
        dataManager.save(existingItem)

        val invalidItem = createInvalidCountry(updatedCode)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Country::class.java, updatedCode) as Country
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, updatedCode)
        Assert.assertEquals(fromDb.name, updatedCode)
        Assert.assertEquals(fromDb.code, updatedCode)

    }

    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    private fun createInvalidCountry(id: String): Country {
        return Country(id)
    }

    //endregion


}