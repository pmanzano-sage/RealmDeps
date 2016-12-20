package io.realm.examples.kotlin

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.AddressType
import junit.framework.Assert

/**
 * @author Pablo Manzano
 *
 * @since 14/12/16
 */
class AddressTypeCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager
    private var addressType = AddressType.create(AddressType.Companion.V3.DELIVERY)

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
     *
     * Checks that:
     * - The addressType created in is correctly saved into the db.
     * - It has the correct number of dependencies.
     */
    fun testSaveContact() {
        dataManager.save(addressType)
        checkNumAddressTypesIs(1)
    }

    /**
     * UPDATE
     *
     * Checks that:
     * - Dependencies are updated correctly.
     * - No dangling dependencies are left.
     */
    //    @Throws(Exception::class)
//    fun testUpdateContact() {
//    }


    /**
     * DELETE
     *
     * Checks that:
     * - After deleting all the contacts there are no contacts left in the db.
     * - It also checks that no dependencies are left in the db.
     */
    //    @Throws(Exception::class)
//    fun testDeleteContact() {
//        dataManager.delete(addressType)
//    }


    //region Auxiliary functions

    private fun checkNumAddressTypesIs(numContacts: Int) {
        // Check the number of persons in the db
        val personsTypes = dataManager.getAll(AddressType::class.java)
        Assert.assertNotNull(personsTypes)
        Assert.assertEquals(numContacts, personsTypes.size)
    }

    //endregion


}