package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.Address
import io.realm.examples.kotlin.dto.AddressType
import io.realm.examples.kotlin.dto.Country
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 */
class AddressCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    private val enumItem = AddressType.Companion.V3.DELIVERY
    private val id = "ADR1"
    private var item = Address.create(id, "street1", "street2", "town", "county", "postCode", enumItem)

    private val street1Updated = "street1 updated"
    private val street2Updated = "street2 updated"

    private val dep1Id = "XYZ"
    private val dep2Id = "US"

    /**
     * Start with a fresh db.
     */
    @Throws(Exception::class)
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


    @Throws(Exception::class)
    override fun tearDown() {
        super.tearDown()
    }

    /**
     * SAVE
     */
    fun testSaveContact() {
        dataManager.save(item)
        checkNumEntitiesIs(Address::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)

        val updated = Address.create(id, street1Updated, street2Updated, "town", "county", "postCode", enumItem)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Address::class.java, id) as Address
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, id)
        Assert.assertEquals(fromDb.streetOne, street1Updated)
        Assert.assertEquals(fromDb.streetTwo, street2Updated)

        // Also check no new entities have been created
        checkNumEntitiesIs(Address::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(Address::class.java, 0)
    }


    /**
     * VALIDATION
     */
    fun testValidation() {
        // Create invalid dependencies to fill parent entity
        val invalidEntity = createInvalidAddress(id)
        try {
            dataManager.save(invalidEntity)
            Assert.fail("Should have thrown a validation exception")
        } catch(e: Exception) {
        }
    }


    /**
     * DEPENDENCY LOOKUP
     */
    fun testDependencyLookup() {

        // Insert valid dependencies into the db
        val existingItem1 = AddressType(dep1Id, SyncStatus.SYNC_SUCCESS, dep1Id, dep1Id)
        dataManager.save(existingItem1)
        val existingItem2 = Country(dep2Id, SyncStatus.SYNC_SUCCESS, dep2Id, dep2Id)
        dataManager.save(existingItem2)

        // Create an invalid entity
        val invalidEntity = createInvalidAddress(id)

        try {
            dataManager.save(invalidEntity, false)
        } catch(e: Exception) {
            Log.e("ugh", "$e")
            Assert.fail("Missing info should have been searched from the db")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Address::class.java, id) as Address
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(existingItem1, fromDb.addressType)
        Assert.assertEquals(existingItem2, fromDb.country)
    }

    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    private fun createInvalidAddress(id: String): Address {
        val invalidItem1 = AddressType(dep1Id)
        val invalidItem2 = Country(dep2Id)
        return Address(id, SyncStatus.SYNC_SUCCESS, street1Updated, street2Updated, "town", "county", "postCode", invalidItem2, invalidItem1)
    }


    //endregion


}