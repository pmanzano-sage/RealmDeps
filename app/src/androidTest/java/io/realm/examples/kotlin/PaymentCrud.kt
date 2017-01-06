package io.realm.examples.kotlin

import android.test.AndroidTestCase
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.Account
import io.realm.examples.kotlin.dto.AccountType
import io.realm.examples.kotlin.dto.Amount
import io.realm.examples.kotlin.dto.Payment
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 */
class PaymentCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    // Item used for the test
    private val id = "id"
    private val parentId = "parent"
    private val account = Account.create(id, AccountType.Companion.V3.CASH_IN_HAND)
    private val updatedDate = "20160202"
    private val item = Payment.create(id, parentId, "20160101", 1.0, "GBP", "description", account)

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
        checkNumEntitiesIs(Payment::class.java, 1)
        checkNumEntitiesIs(Account::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)

        val updated = Payment.create(id, parentId, updatedDate, 1.0, "GBP", "description", account)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Payment::class.java, id) as Payment
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, id)
        Assert.assertEquals(fromDb.date, updatedDate)

        // Also check no new entities have been created
        checkNumEntitiesIs(Payment::class.java, 1)
        checkNumEntitiesIs(Account::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(Payment::class.java, 0)
        checkNumEntitiesIs(Account::class.java, 1)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val err = "Should have thrown a InvalidDependency/InvalidField exception"
        val invalidItem = createInvalidPayment(id)
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
    fun testDependencyLookup() {
        // Insert into db the dependencies that will be searched by fillDeps
        val accountType = AccountType.create(AccountType.Companion.V3.CHECKING)
        val invalidAccount = Account(id, SyncStatus.SYNC_SUCCESS, "invalidAccount", "", accountType, 0, Amount.pounds(0.0))
        dataManager.save(invalidAccount)

        val invalidItem = createInvalidPayment(id)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db.\nException: ${e.message}")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Payment::class.java, id) as Payment
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.account?.accountType?.name, AccountType.Companion.V3.CHECKING.name.toLowerCase())
    }

    /**
     * DEPENDENCY LOOKUP FAILURE (NOT FOUND)
     *
     * The same test as testDependencyLookup, but this time the missing dependency is not in the db.
     */
    fun testDependencyLookupFail() {

        // Create an invalid entity
        val invalidEntity = createInvalidPayment(id)
        try {
            dataManager.save(invalidEntity, false)
            Assert.fail("A NotFoundException should be triggered")
        } catch(e: NotFoundException) {
            Log.w("cool", "$e")
        } catch(e: Exception) {
            e.printStackTrace()
            Assert.fail("Should have thrown a NotFoundException instead of ${e.javaClass.simpleName}")
        }

        // Now check that the item was actually NOT saved
        checkNumEntitiesIs(Payment::class.java, 0)
    }


    /**
     * DEPENDENCY LOOKUP FAILURE (INVALID FIELD)
     *
     * The same test as testDependencyLookup, but this time the missing field is not a dependency.
     */
    fun testDependencyLookupFail2() {

        // Create an invalid entity
        val invalidEntity = createInvalidPaymentNotFixable(id)
        try {
            dataManager.save(invalidEntity, false)
            Assert.fail("A InvalidFieldException should be triggered")
        } catch(e: InvalidFieldException) {
            Log.w("cool", "$e")
        } catch(e: Exception) {
            e.printStackTrace()
            Assert.fail("Should have thrown a InvalidFieldException instead of ${e.javaClass.simpleName}")
        }

        // Now check that the item was actually NOT saved
        checkNumEntitiesIs(Payment::class.java, 0)
    }


    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    /**
     * This Payment can be fixed because it lacks some dependency details.
     */
    private fun createInvalidPayment(id: String): Payment {
        // It will be invalid cos the AccountType has no name.
        // It should be fixable since Account is annotated as SupportsIdOnly
        val accountType = AccountType(AccountType.Companion.V3.CHECKING.name, symbol = "symbol")
        val invalidAccount = Account(id, SyncStatus.SYNC_SUCCESS, "invalidAccount", "", accountType, 0, Amount.pounds(0.0))
        return Payment.create(id, parentId, "20160101", 1.0, "GBP", "description", invalidAccount)
    }

    /**
     * This Payment can not be fixed because the amount is zero!
     * 'amount' must be greater than zero.
     */
    private fun createInvalidPaymentNotFixable(id: String): Payment {
        // It will be invalid cos the account does not have a name
        val accountType = AccountType(AccountType.Companion.V3.CHECKING.name, symbol = "symbol")
        val invalidAccount = Account(id, SyncStatus.SYNC_SUCCESS, "invalidAccount", "", accountType, 0, Amount.pounds(0.0))
        return Payment.create(id, parentId, "20160101", 0.0, "GBP", "description", invalidAccount)
    }

    //endregion


}