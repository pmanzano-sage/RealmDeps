package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.SubTaxRate
import io.realm.examples.kotlin.dto.TaxRate
import junit.framework.Assert

/**
 * @author Pablo Manzano
 */
class TaxRateCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    companion object {

        private val id = "id"
        private val updatedName = "FiftyPercent"
        private val updatedPercentage = "0.25"
        private var taxRate = createTaxRate(id, "0.0")
        private val subId = "-1"

        /**
         * Creates a dummy TaxRate.
         *
         * Example of usage:
         * val taxRate = createTaxRate(id, "0.0")
         */
        fun createTaxRate(id: String, percentage: String): TaxRate {
            val subTaxRate1 = SubTaxRate.create("$id-1", id, "SubTax1", "0.1", true)
            val subTaxRate2 = SubTaxRate.create("$id-2", id, "SubTax2", "0.2", true)
            val subTaxRate3 = SubTaxRate.create("$id-3", id, "SubTax3", "0.3", true)
            val subTaxRates = arrayListOf(subTaxRate1, subTaxRate2, subTaxRate3)
            return TaxRate.create(id, "NoTax", percentage, true, subTaxRates)
        }

        /**
         * This TaxRate can be fixed because it lacks some dependency details.
         * NOTE: It will only have one subtax and the percentage of that subtax will match
         * the one received.
         */
        fun createInvalidTaxRate(id: String, percentage: String): TaxRate {
            // It will be invalid since the name is a mandatory field of a subTaxRate
            val subTaxRate = SubTaxRate.create("$id$subId", id, "", percentage, true)
            val subTaxes = arrayListOf(subTaxRate)
            return TaxRate.create(id, "Exempt", percentage, true, subTaxes)
        }

        /**
         * This TaxRate can not be fixed because the name is missing!
         * 'name' is a mandatory field.
         */
        fun createInvalidTaxRateNotFixable(id: String): TaxRate {
            val subTaxRate1 = SubTaxRate.create("$id-1", id, "SubTax1", "0.1", true)
            val subTaxRate2 = SubTaxRate.create("$id-2", id, "SubTax2", "0.2", true)
            val subTaxRate3 = SubTaxRate.create("$id-3", id, "SubTax3", "0.3", true)
            val subTaxRates = arrayListOf(subTaxRate1, subTaxRate2, subTaxRate3)
            return TaxRate.create(id, "", "0.1", true, subTaxRates)
        }
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
        dataManager.save(taxRate)
        // main entity
        checkNumEntitiesIs(TaxRate::class.java, 1)
        // dependencies
        checkNumEntitiesIs(SubTaxRate::class.java, taxRate.subTaxRates?.size?.toLong() ?: 0)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(taxRate)

        // Exactly the same taxRate, but we change the postal code of the main address
        val taxRateUpdated = createTaxRate(id, updatedPercentage)
        dataManager.update(taxRateUpdated, false)

        checkNumEntitiesIs(TaxRate::class.java, 1)

        // now check the postal code that we have in the db
        val item = dataManager.find(TaxRate::class.java, id) as TaxRate
        Assert.assertNotNull(item)
        Assert.assertEquals(updatedPercentage, item.percentage)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.delete(taxRate)
        checkNumEntitiesIs(TaxRate::class.java, 0)
        checkNumEntitiesIs(SubTaxRate::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val err = "Should have thrown a InvalidDependency/InvalidField exception"
        val invalidItem = createInvalidTaxRate(id, "0.0")
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
        val goodPercentage = "0.3"
        val subTaxRate = SubTaxRate.create("$id$subId", id, updatedName, goodPercentage, true)
        dataManager.save(subTaxRate)

        val invalidItem = createInvalidTaxRate(id, "0.0")
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db.\nException: ${e.message}")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(TaxRate::class.java, id) as TaxRate
        Assert.assertNotNull(fromDb)
        val subTax = fromDb.subTaxRates?.findByName(updatedName)
        Assert.assertNotNull(subTax)
        Assert.assertEquals(goodPercentage, subTax?.percentage)
    }

    fun List<SubTaxRate>.findByName(name: String): SubTaxRate? {
        return find { it.name == name }
    }

    /**
     * DEPENDENCY LOOKUP FAILURE (NOT FOUND)
     *
     * The same test as testDependencyLookup, but this time the missing dependency is not in the db.
     */
    fun testDependencyLookupFail() {

        // Create an invalid entity
        val invalidEntity = createInvalidTaxRate(id, "0.0")
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
        checkNumEntitiesIs(TaxRate::class.java, 0)
    }


    /**
     * DEPENDENCY LOOKUP FAILURE (INVALID FIELD)
     *
     * The same test as testDependencyLookup, but this time the missing field is not a dependency.
     * Since TaxRate is annotated as SupportsIdOnly it will try to find it in the db, and throw
     * a NotFoundException.
     */
    fun testDependencyLookupFail2() {

        // Create an invalid entity
        val invalidEntity = createInvalidTaxRateNotFixable(id)
        try {
            dataManager.save(invalidEntity, false)
            Assert.fail("A InvalidFieldException should be triggered")
        } catch(e: NotFoundException) {
            Log.w("cool", "$e")
        } catch(e: Exception) {
            e.printStackTrace()
            Assert.fail("Should have thrown a InvalidFieldException instead of ${e.javaClass.simpleName}")
        }

        // Now check that the item was actually NOT saved
        checkNumEntitiesIs(TaxRate::class.java, 0)
    }


    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    //endregion


}