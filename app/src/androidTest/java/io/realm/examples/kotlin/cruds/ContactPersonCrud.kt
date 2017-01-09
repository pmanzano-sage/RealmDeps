package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.*
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert

/**
 * @author Pablo Manzano
 */
class ContactPersonCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    private val CONTACT_PERSON_TYPES = arrayListOf(ContactPersonType.Companion.V3.CONTRACTOR)
    private val CONTACT_ID = "1234"
    private val UPDATED_POST_CODE = "08030"
    private var contact = createContactPerson(CONTACT_ID, "John", "john@gmail.com", "123123123")
    private val invalidId = "invalidId"

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
        dataManager.save(contact)
        // main entity
        checkNumEntitiesIs(ContactPerson::class.java, 1)
        // dependencies
        checkNumEntitiesIs(Address::class.java, 1)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(contact)
        // Exactly the same contact, but we change the postal code of the main address
        val contactUpdated = createContactPerson(CONTACT_ID, "John", "john@gmail.com", "123123123")
        contactUpdated.address?.postCode = UPDATED_POST_CODE
        dataManager.update(contactUpdated, false)

        checkNumEntitiesIs(ContactPerson::class.java, 1)

        // now check the postal code that we have in the db
        val john = dataManager.find(ContactPerson::class.java, CONTACT_ID) as ContactPerson
        Assert.assertNotNull(john)
        Assert.assertEquals(UPDATED_POST_CODE, john.address?.postCode)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.delete(contact)
        checkNumEntitiesIs(ContactPerson::class.java, 0)
        checkNumEntitiesIs(Address::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val err = "Should have thrown a InvalidDependency/InvalidField exception"
        val invalidItem = createInvalidContactPerson(CONTACT_ID)
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
        val invalidAddressType = AddressType(invalidId, SyncStatus.SYNC_SUCCESS, invalidId, invalidId)
        dataManager.save(invalidAddressType)

        val invalidItem = createInvalidContactPerson(CONTACT_ID)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db.\nException: ${e.message}")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(ContactPerson::class.java, CONTACT_ID) as ContactPerson
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.address?.addressType?.symbol, invalidId)
    }

    /**
     * DEPENDENCY LOOKUP FAILURE (NOT FOUND)
     *
     * The same test as testDependencyLookup, but this time the missing dependency is not in the db.
     */
    fun testDependencyLookupFail() {

        // Create an invalid entity
        val invalidEntity = createInvalidContactPerson(CONTACT_ID)
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
        checkNumEntitiesIs(ContactPerson::class.java, 0)
    }


    /**
     * DEPENDENCY LOOKUP FAILURE (INVALID FIELD)
     *
     * The same test as testDependencyLookup, but this time the missing field is not a dependency.
     */
    fun testDependencyLookupFail2() {

        // Create an invalid entity
        val invalidEntity = createInvalidContactPersonNotFixable(CONTACT_ID)
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
        checkNumEntitiesIs(ContactPerson::class.java, 0)
    }


    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }


    /**
     * Creates a dummy ContactPerson.
     */
    private fun createContactPerson(id: String, name: String, email: String, mobile: String): ContactPerson {
        val mainAddress = Address.create(null, "street1 main", "street2 main", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY)
        val contactPersonTypes = ContactPersonType.createList(CONTACT_PERSON_TYPES)
        return ContactPerson.create(id, contactPersonTypes, name, "job", "telephone", mobile, email, "fax", address = mainAddress)
    }

    /**
     * This ContactPerson can be fixed because it lacks some dependency details.
     */
    private fun createInvalidContactPerson(id: String): ContactPerson {
        // It will be invalid cos the AddressType has no symbol.
        // It should be fixable since AddressType is annotated as SupportsIdOnly
        val invalidAddressType = AddressType(invalidId, SyncStatus.SYNC_SUCCESS, invalidId)
        val ireland = Country.create(Country.Companion.Code.IE)
        val invalidAddress = Address(id, SyncStatus.SYNC_SUCCESS, "street1", "street2", "town", "county", "postCode", ireland, invalidAddressType)
        val contactPersonTypes = ContactPersonType.createList(CONTACT_PERSON_TYPES)
        return ContactPerson.create(id, contactPersonTypes, "name", "job", "telephone", "mobile", "email", "fax", address = invalidAddress)
    }

    /**
     * This ContactPerson can not be fixed because the name is missing!
     * 'name' is a mandatory field.
     */
    fun createInvalidContactPersonNotFixable(id: String): ContactPerson {
        return createContactPerson(id, "", "john@gmail.com", "123123123")
    }

    //endregion


}