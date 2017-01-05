package io.realm.examples.kotlin

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.AddressType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 *
 * This entity is basic and has no dependencies.
 */
class AddressTypeCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    // Item used for the test
    private val enumItem = AddressType.Companion.V3.DELIVERY
    private val id = enumItem.name
    private val updatedName = "updated name"
    private val updatedSymbol = "updated symbol"
    private val item = AddressType.create(enumItem)
    private val invalidItemName = "XYZ"

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
        checkNumEntitiesIs(AddressType::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(item)

        // These entities have the name fixed, so we can not do this:
        // item.name = updatedName
        // So we create a new entity with the same id and different name & symbol
        val updated = AddressType(id, SyncStatus.SYNC_SUCCESS, updatedName, updatedSymbol)
        dataManager.update(updated)

        // Now check that the item was actually modified
        val fromDb = dataManager.find(AddressType::class.java, id) as AddressType
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, id)
        Assert.assertEquals(fromDb.name, updatedName)
        Assert.assertEquals(fromDb.symbol, updatedSymbol)

        // Also check no new entities have been created
        checkNumEntitiesIs(AddressType::class.java, 1)
    }


    /**
     * DELETE
     */
    fun testDeleteContact() {
        dataManager.save(item)
        dataManager.delete(item)
        checkNumEntitiesIs(AddressType::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val invalidItem = createInvalidEntity(AddressType::class.java, invalidItemName)
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
        val existingItem = AddressType(invalidItemName, SyncStatus.SYNC_SUCCESS, invalidItemName, invalidItemName)
        dataManager.save(existingItem)

        val invalidItem = createInvalidEntity(AddressType::class.java, invalidItemName)
        try {
            // Note the 'false' param. That means:
            // - validation is deactivated, and
            // - classes tagged as @SupportsIdOnly are looked up in the db using only the id
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(AddressType::class.java, invalidItemName) as AddressType
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.id, invalidItemName)
        Assert.assertEquals(fromDb.name, invalidItemName)
        Assert.assertEquals(fromDb.symbol, invalidItemName)

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