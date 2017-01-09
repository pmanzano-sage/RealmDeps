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
class SubTaxRateCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    private val id = "id"
    private val parentId = "parentId"
    private val updatedName = "FiftyPercent"
    private val updatedPercentage = "0.25"
    private var subTaxRate = SubTaxRate.create(id, parentId, "SubTax1", "0.1", true)

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
        dataManager.save(subTaxRate)
        // main entity
        checkNumEntitiesIs(SubTaxRate::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(subTaxRate)

        // Exactly the same subTaxRate, but we change the postal code of the main address
        val taxRateUpdated = SubTaxRate.create(id, parentId, updatedName, updatedPercentage, true)
        dataManager.update(taxRateUpdated, false)

        checkNumEntitiesIs(SubTaxRate::class.java, 1)

        // now check the percentage that we have in the db
        val item = dataManager.find(SubTaxRate::class.java, id) as SubTaxRate
        Assert.assertNotNull(item)
        Assert.assertEquals(updatedName, item.name)
        Assert.assertEquals(updatedPercentage, item.percentage)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.delete(subTaxRate)
        checkNumEntitiesIs(SubTaxRate::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val err = "Should have thrown a InvalidDependency/InvalidField exception"
        val invalidItem = createInvalidSubTaxRate(id)
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
        val subTaxRate = SubTaxRate.create(id, parentId, updatedName, "0.5", true)
        dataManager.save(subTaxRate)

        val invalidItem = createInvalidSubTaxRate(id)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db.\nException: ${e.message}")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(SubTaxRate::class.java, id) as SubTaxRate
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.name, updatedName)
    }

    /**
     * DEPENDENCY LOOKUP FAILURE (NOT FOUND)
     *
     * The same test as testDependencyLookup, but this time the missing dependency is not in the db.
     */
    fun testDependencyLookupFail() {

        // Create an invalid entity
        val invalidEntity = createInvalidSubTaxRate(id)
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


    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    /**
     * This TaxRate can be fixed because it lacks some dependency details.
     */
    private fun createInvalidSubTaxRate(id: String): SubTaxRate {
        // It will be invalid since the name is a mandatory field of a subTaxRate
        return SubTaxRate.create(id, parentId, "", "0.5", true)
    }


    //endregion


}