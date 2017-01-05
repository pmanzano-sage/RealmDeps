package io.realm.examples.kotlin

import android.test.AndroidTestCase
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.Account
import io.realm.examples.kotlin.dto.AccountType
import io.realm.examples.kotlin.dto.Amount
import io.realm.examples.kotlin.dto.Country
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 */
class AccountCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    private val enumItem = AccountType.Companion.V3.CHECKING
    private val updatedCountry = Country.Companion.Code.IE
    private val id = "myChecking1"
    private val item = Account.create(id, enumItem)
    private val dep1Id = "XYZ"

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
        checkNumEntitiesIs(Account::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)

        // Note that we are changing the country
        val updated = Account.create(id, enumItem, updatedCountry)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Account::class.java, id) as Account
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, id)
        Assert.assertEquals(fromDb.accountType, updated.accountType)
        Assert.assertEquals(fromDb.balance.currencyCode, updated.balance.currencyCode)

        // Also check no new entities have been created
        checkNumEntitiesIs(Account::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(Account::class.java, 0)
    }


    /**
     * VALIDATION
     */
    fun testValidation() {
        // Create invalid dependencies to fill parent entity
        val invalidEntity = createInvalidAccount(id)
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
        val existingItem1 = AccountType(dep1Id, SyncStatus.SYNC_SUCCESS, dep1Id, dep1Id)
        dataManager.save(existingItem1)

        // Create invalid dependencies to fill parent entity
        val invalidItem1 = AccountType(dep1Id, SyncStatus.SYNC_SUCCESS, "", "")
        val invalidEntity = Account(id, SyncStatus.SYNC_ERROR, "displayName", "", invalidItem1, 0, Amount.pounds(0.0))

        try {
            dataManager.save(invalidEntity, false)
        } catch(e: Exception) {
            Log.e("ugh", "$e")
            Assert.fail("Missing info should have been searched from the db")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Account::class.java, id) as Account
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(existingItem1, fromDb.accountType)
    }


    /**
     * DEPENDENCY LOOKUP FAILURE
     *
     * The same test as testDependencyLookup, but this time the missing dependency is not in the db.
     */
    fun testDependencyLookupFail() {

        // Create invalid dependencies to fill parent entity
        val invalidItem1 = AccountType(dep1Id, SyncStatus.SYNC_SUCCESS, "", "")
        val invalidEntity = Account(id, SyncStatus.SYNC_ERROR, "displayName", "", invalidItem1, 0, Amount.pounds(0.0))

        try {
            dataManager.save(invalidEntity, false)
            Assert.fail("A NotFoundException should be triggered")
        } catch(e: Exception) {
            Log.w("cool", "$e")
        }

        // Now check that the item was actually NOT saved
        checkNumEntitiesIs(Account::class.java, 0)
    }

    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    private fun createInvalidAccount(id: String): Account {
        // It will be invalid cos the accountType does not have a name
        val accountType = AccountType(symbol = "symbol")
        return Account(id, SyncStatus.SYNC_ERROR, "displayName", "", accountType, 0, Amount.pounds(0.0))
    }

    //endregion


}