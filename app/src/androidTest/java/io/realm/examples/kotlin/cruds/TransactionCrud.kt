package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.*
import junit.framework.Assert

/**
 * @author Pablo Manzano
 */
class TransactionCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    private val TRAN_ID = "1234"
    private var tran = createValidTransaction(TRAN_ID, "example tran")
    private val ORIGINAL_TAX = "9.9"

    /**
     * Creates a dummy Transaction.
     */
    fun createTransaction(id: String, title: String, taxRate: TaxRate): Transaction {
        val accountType = AccountType.Companion.V3.CHECKING
        val sourceAccountId = "sourceAccount"
        val account = Account.create(sourceAccountId, accountType)
        val contact = ContactCrud.createContact("contact1", "email", "mobile", "name", "ref")

        return Transaction.createMoneyIn(id, title, title, Amount.pounds(1.0), "20160101", account, contact, taxRate)
    }

    fun createValidTransaction(id: String, title: String): Transaction {
        val taxRate = TaxRateCrud.createTaxRate("tax-$id", "0.0")
        return createTransaction(id, title, taxRate)
    }

    /**
     * This Transaction can be fixed because it lacks some dependency details.
     *
     * NOTE: It will only have one subtax and the percentage of that subtax will match
     * the one received for the tax.
     */
    fun createInvalidTransaction(id: String, title: String): Transaction {
        // It will be invalid cos the TaxRate is invalid.
        // It should be fixable since TaxRate is annotated as SupportsIdOnly
        val taxRate = TaxRateCrud.createInvalidTaxRate("tax-$id", ORIGINAL_TAX)
        return createTransaction(id, title, taxRate)
    }

    /**
     * This Transaction can not be fixed because the name is missing!
     * 'name' is a mandatory field.
     */
    fun createInvalidTransactionNotFixable(id: String): Transaction {
        // It will be invalid cos the TaxRate is invalid.
        // It can not be fix since TaxRate is not fixable
        val taxRate = TaxRateCrud.createInvalidTaxRateNotFixable("tax-$id")
        return createTransaction(id, "title", taxRate)

    }


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
        dataManager.save(tran)
        // main entity
        checkNumEntitiesIs(Transaction::class.java, 1)
        // dependencies
        checkNumEntitiesIs(Account::class.java, tran.numAccounts().toLong())
        checkNumEntitiesIs(TransactionCategory::class.java, 1)
        checkNumEntitiesIs(Contact::class.java, 1)
        checkNumEntitiesIs(TaxRate::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        val value = 2.0
        dataManager.save(tran)
        // Exactly the same tran, but we change the postal code of the main address
        val transactionUpdated = createValidTransaction(TRAN_ID, "example tran")
        transactionUpdated.amount = Amount.euros(value)
        dataManager.update(transactionUpdated)

        checkNumEntitiesIs(Transaction::class.java, 1)

        // check that the amount was updated
        val tran = dataManager.find(Transaction::class.java, TRAN_ID) as Transaction
        Assert.assertNotNull(tran)
        Assert.assertEquals(value, tran.amount.value)
        Assert.assertEquals("EUR", tran.amount.currencyCode)

        // assert the number of dependencies didn't change
        checkNumEntitiesIs(Account::class.java, tran.numAccounts().toLong())
        checkNumEntitiesIs(TransactionCategory::class.java, 1)
        checkNumEntitiesIs(Contact::class.java, 1)
        checkNumEntitiesIs(TaxRate::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.save(tran)
        dataManager.delete(tran)

        checkNumEntitiesIs(Transaction::class.java, 0)
        // dependencies (Note that none of them cascade on delete)
        checkNumEntitiesIs(Account::class.java, tran.numAccounts().toLong())
        checkNumEntitiesIs(TransactionCategory::class.java, 1)
        checkNumEntitiesIs(Contact::class.java, 1)
        checkNumEntitiesIs(TaxRate::class.java, 1)

    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val err = "Should have thrown a InvalidDependency/InvalidField exception"
        val invalidItem = createInvalidTransaction(TRAN_ID, "invalid tran")
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
        val taxPercentage = "3.0"
        val taxRate = TaxRateCrud.createTaxRate("tax-$TRAN_ID", taxPercentage)
        dataManager.save(taxRate)

        val invalidItem = createInvalidTransaction(TRAN_ID, "invalid tran")
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db.\nException: ${e.message}")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Transaction::class.java, TRAN_ID) as Transaction
        Assert.assertNotNull(fromDb)

        // The number of subtaxes should not change
        val subTaxes = fromDb.taxRate?.subTaxRates
        Assert.assertEquals(1, subTaxes?.size)

        // Only the subtax should change, but not the tax
        val subTax = subTaxes?.get(0)
        Assert.assertEquals("0.1", subTax?.percentage)
        Assert.assertEquals(ORIGINAL_TAX, fromDb.taxRate?.percentage)
    }

    /**
     * DEPENDENCY LOOKUP FAILURE (NOT FOUND)
     *
     * The same test as testDependencyLookup, but this time the missing dependency is not in the db.
     */
    fun testDependencyLookupFail() {

        // Create an invalid entity
        val invalidEntity = createInvalidTransaction(TRAN_ID, "title")
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
        checkNumEntitiesIs(Contact::class.java, 0)
    }


    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    //endregion


}