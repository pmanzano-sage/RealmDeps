package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.TransactionCategory
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 *
 * This entity is basic and has no dependencies.
 */
class TransactionCategoryCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    // Item used for the test
    private val enumItem = TransactionCategory.Companion.V3.OTHER_RECEIPT
    private val id = enumItem.name
    private val updatedLabel = "updated label"
    private val updatedMoneyIn = true
    private val updatedNominalCode = 111
    private val updatedTaxCodeId = "123"
    private val item = TransactionCategory.create(enumItem)

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
        checkNumEntitiesIs(TransactionCategory::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)
        val updated = TransactionCategory(id, SyncStatus.SYNC_SUCCESS, updatedLabel, updatedMoneyIn, updatedNominalCode, updatedTaxCodeId)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(TransactionCategory::class.java, id) as TransactionCategory
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(updatedLabel, fromDb.label)
        Assert.assertEquals(updatedMoneyIn, fromDb.moneyIn)
        Assert.assertEquals(updatedNominalCode, fromDb.nominalCode)
        Assert.assertEquals(updatedTaxCodeId, fromDb.taxCodeId)

        // Also check no new entities have been created
        checkNumEntitiesIs(TransactionCategory::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDeleteContact() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(TransactionCategory::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val invalidItem = createInvalidEntity(TransactionCategory::class.java, enumItem.name)
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
        dataManager.save(item)

        val invalidItem = createInvalidEntity(TransactionCategory::class.java, id)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(TransactionCategory::class.java, id) as TransactionCategory
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(item.label, fromDb.label)
        Assert.assertEquals(item.moneyIn, fromDb.moneyIn)
        Assert.assertEquals(item.nominalCode, fromDb.nominalCode)
        Assert.assertEquals(item.taxCodeId, fromDb.taxCodeId)
    }

    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }

    private fun <T : Dto> createInvalidEntity(clazz: Class<T>, id: String): Dto {
        val ctor = clazz.constructors.first()
        val dto = ctor.newInstance()
        val field = clazz.declaredFields[0]
        field.isAccessible = true
        field.set(dto, id)
        return dto as T
    }

    //endregion


}