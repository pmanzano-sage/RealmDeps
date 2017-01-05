package io.realm.examples.kotlin

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.Address
import io.realm.examples.kotlin.dto.AddressType
import io.realm.examples.kotlin.dto.Business
import io.realm.examples.kotlin.dto.Country
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 */
class BusinessCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    // Item used for the test
    private val id = "ciaId"
    private val invalidId = "invalidId"
    private val address = Address.create(null, "street1", "street2", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY)
    private val updatedName = "updated name"
    private val updatedMobile = "updated mobile"
    private val item = Business.create(id, "Sage", "sage.com", "123123123", "932740909", address)

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
    //    fun testSave() {
//        dataManager.save(item)
//        checkNumEntitiesIs(Business::class.java, 1)
//    }

    /**
     * UPDATE
     */
    //    fun testUpdate() {
//        dataManager.save(item)
//
//        // These entities have the name fixed, so we can not do this:
//        // item.name = updatedName
//        // So we create a new entity with the same id and different name & symbol
//        val updated = Business(id, SyncStatus.SYNC_SUCCESS, name = updatedName, mobile = updatedMobile)
//        dataManager.update(updated)
//
//        // Now check that the item was actually modified
//        val fromDb = dataManager.find(Business::class.java, id) as Business
//        Assert.assertNotNull(fromDb)
//        Assert.assertEquals(fromDb.id, id)
//        Assert.assertEquals(fromDb.name, updatedName)
//        Assert.assertEquals(fromDb.mobile, updatedMobile)
//
//        // Also check no new entities have been created
//        checkNumEntitiesIs(Business::class.java, 1)
//    }


    /**
     * DELETE
     */
    //    fun testDeleteContact() {
//        dataManager.save(item)
//        dataManager.delete(item)
//        checkNumEntitiesIs(Business::class.java, 0)
//    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val err = "Should have thrown a InvalidDependency/InvalidField exception"
        val invalidItem = createInvalidBusiness(id)
        try {
            dataManager.save(invalidItem)
            Assert.fail(err)
        } catch (e: InvalidDependencyException) {
        } catch (e: InvalidFieldException) {
        } catch (e: Exception) {
            e.printStackTrace()
            Assert.fail("$err instead of ${e.javaClass.simpleName}")
        }
    }

    /**
     * DEPENDENCY LOOKUP
     */
    //    fun testDependencyLookup() {
//        // Insert into db the dependencies that will be searched by fillDeps
//        val invalidAddressType = AddressType(invalidId, SyncStatus.SYNC_SUCCESS, invalidId, invalidId)
//        dataManager.save(invalidAddressType)
//
//        val invalidItem = createInvalidBusiness(id)
//        try {
//            dataManager.save(invalidItem, false)
//        } catch(e: Exception) {
//            Assert.fail("Missing info should have been searched from the db.\nException: ${e.message}")
//        }
//
//        // Now check that the item was actually modified
//        val fromDb = dataManager.find(Business::class.java, id) as Business
//        Assert.assertNotNull(fromDb)
//        Assert.assertEquals(fromDb.address.addressType?.symbol, invalidId)
//    }

    /**
     * DEPENDENCY LOOKUP FAILURE (NOT FOUND)
     *
     * The same test as testDependencyLookup, but this time the missing dependency is not in the db.
     */
    //    fun testDependencyLookupFail() {
//
//        // Create an invalid entity
//        val invalidEntity = createInvalidBusiness(id)
//        try {
//            dataManager.save(invalidEntity, false)
//            Assert.fail("A NotFoundException should be triggered")
//        } catch(e: NotFoundException) {
//            Log.w("cool", "$e")
//        } catch(e: Exception) {
//            e.printStackTrace()
//            Assert.fail("Should have thrown a NotFoundException instead of ${e.javaClass.simpleName}")
//        }
//
//        // Now check that the item was actually NOT saved
//        checkNumEntitiesIs(Account::class.java, 0)
//    }


    /**
     * DEPENDENCY LOOKUP FAILURE (INVALID FIELD)
     *
     * The same test as testDependencyLookup, but this time the missing field is not a dependency.
     */
    //    fun testDependencyLookupFail2() {
//
//        // Create an invalid entity
//        val invalidEntity = createInvalidBusinessNotFixable(id)
//        try {
//            dataManager.save(invalidEntity, false)
//            Assert.fail("A InvalidFieldException should be triggered")
//        } catch(e: InvalidFieldException) {
//            Log.w("cool", "$e")
//        } catch(e: Exception) {
//            e.printStackTrace()
//            Assert.fail("Should have thrown a InvalidFieldException instead of ${e.javaClass.simpleName}")
//        }
//
//        // Now check that the item was actually NOT saved
//        checkNumEntitiesIs(Account::class.java, 0)
//    }


    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    /**
     * This Business can be fixed because it lacks some dependency details.
     */
    private fun createInvalidBusiness(id: String): Business {
        // It will be invalid cos the AddressType has no symbol.
        // It should be fixable since AddressType is annotated as SupportsIdOnly
        val invalidAddressType = AddressType(invalidId, SyncStatus.SYNC_SUCCESS, invalidId)
        val ireland = Country.create(Country.Companion.Code.IE)
        val invalidAddress = Address(id, SyncStatus.SYNC_ERROR, "street1", "street2", "town", "county", "postCode", ireland, invalidAddressType)
        return Business.create(id, name = "UglyBusiness", address = invalidAddress)
    }

    /**
     * This Business can not be fixed because the business name is missing!
     * 'name' is a mandatory field.
     */
    private fun createInvalidBusinessNotFixable(id: String): Business {
        // It will be invalid cos the Address has an invalid AddressType
        val invalidAddressType = AddressType(invalidId)
        val ireland = Country.create(Country.Companion.Code.IE)
        val invalidAddress = Address(id, SyncStatus.SYNC_ERROR, "street1", "street2", "town", "county", "postCode", ireland, invalidAddressType)
        return Business(id, SyncStatus.SYNC_ERROR, address = invalidAddress)
    }

    //endregion


}