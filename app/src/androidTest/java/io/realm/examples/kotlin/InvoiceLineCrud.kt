package io.realm.examples.kotlin

import android.test.AndroidTestCase
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.InvoiceLine
import io.realm.examples.kotlin.dto.TaxRate
import junit.framework.Assert

/**
 * @author Pablo Manzano
 */
class InvoiceLineCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    // Item used for the test
    private val id = "id"
    private val parentId = "parent"
    private val validName = "NoTax"
    private val taxRate = TaxRate.create(id, validName, "0.0", true, null)
    private val doubleVal = 1.0
    private val item = InvoiceLine.create(id, parentId, 2.5, 3.9, "support hours ", taxRate)

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
        checkNumEntitiesIs(InvoiceLine::class.java, 1)
        checkNumEntitiesIs(TaxRate::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)

        val updated = InvoiceLine.create(id, parentId, doubleVal, doubleVal, "support hours ", taxRate)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(InvoiceLine::class.java, id) as InvoiceLine
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, id)
        Assert.assertEquals(fromDb.quantity, doubleVal)
        Assert.assertEquals(fromDb.unitPrice, doubleVal)

        // Also check no new entities have been created
        checkNumEntitiesIs(InvoiceLine::class.java, 1)
        checkNumEntitiesIs(TaxRate::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(InvoiceLine::class.java, 0)
        checkNumEntitiesIs(TaxRate::class.java, 1)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val err = "Should have thrown a InvalidDependency/InvalidField exception"
        val invalidItem = createInvalidInvoiceLine(id)
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
        val invalidTaxRate = TaxRate.create(id, validName, "0.0", true, null)
        dataManager.save(invalidTaxRate)

        val invalidItem = createInvalidInvoiceLine(id)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db.\nException: ${e.message}")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(InvoiceLine::class.java, id) as InvoiceLine
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.taxRate?.name, validName)
    }

    /**
     * DEPENDENCY LOOKUP FAILURE (NOT FOUND)
     *
     * The same test as testDependencyLookup, but this time the missing dependency is not in the db.
     */
    fun testDependencyLookupFail() {

        // Create an invalid entity
        val invalidEntity = createInvalidInvoiceLine(id)
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
        checkNumEntitiesIs(InvoiceLine::class.java, 0)
    }


    /**
     * DEPENDENCY LOOKUP FAILURE (INVALID FIELD)
     *
     * The same test as testDependencyLookup, but this time the missing field is not a dependency.
     */
    fun testDependencyLookupFail2() {

        // Create an invalid entity
        val invalidEntity = createInvalidInvoiceLineNotFixable(id)
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
        checkNumEntitiesIs(InvoiceLine::class.java, 0)
    }


    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    /**
     * This InvoiceLine can be fixed because it lacks some dependency details.
     */
    private fun createInvalidInvoiceLine(id: String): InvoiceLine {
        // It will be invalid cos the TaxRate has no name.
        // It should be fixable since TaxRate is annotated as SupportsIdOnly
        val invalidTaxRate = TaxRate.create(id, "", "0.0", true, null)
        return InvoiceLine.create(id, parentId, 2.5, 3.9, "support hours", invalidTaxRate)
    }

    /**
     * This InvoiceLine can not be fixed because the business name is missing!
     * 'name' is a mandatory field.
     */
    private fun createInvalidInvoiceLineNotFixable(id: String): InvoiceLine {
        // It will be invalid cos the quantity is zero (and can not be zero)
        val taxRate = TaxRate.create(id, "NoTax", "0.0", true, null)
        return InvoiceLine.create(id, parentId, 0.0, 4.5, "support hours", taxRate)
    }

    //endregion


}