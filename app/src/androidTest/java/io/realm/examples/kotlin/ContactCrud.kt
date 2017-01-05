package io.realm.examples.kotlin

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
 *
 * @since 14/12/16
 */
class ContactCrud : AndroidTestCase() {

    private lateinit var dataManager: DataManager

    private val CONTACT_TYPES = arrayListOf(ContactType.Companion.V3.PURCHASING, ContactType.Companion.V3.ACCOUNTS)
    private val CONTACT_PERSON_TYPES = arrayListOf(ContactPersonType.Companion.V3.CONTRACTOR)
    private val CONTACT_ID = "1234"
    private val UPDATED_POST_CODE = "08030"
    private var contact = createContact(CONTACT_ID, "John", "John's ref", "john@gmail.com", "123123123")
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
        checkNumEntitiesIs(Contact::class.java, 1)
        // dependencies
        checkNumEntitiesIs(Address::class.java, 2)
    }

    /**
     * UPDATE
     */
    fun testUpdate() {
        dataManager.save(contact)
        // Exactly the same contact, but we change the postal code of the main address
        val contactUpdated = createContact(CONTACT_ID, "John", "John's ref", "john@gmail.com", "123123123")
        contactUpdated.mainAddress?.postCode = UPDATED_POST_CODE
        dataManager.update(contactUpdated, false)

        checkNumEntitiesIs(Contact::class.java, 1)

        // now check the postal code that we have in the db
        val john = dataManager.find(Contact::class.java, CONTACT_ID) as Contact
        Assert.assertNotNull(john)
        Assert.assertEquals(UPDATED_POST_CODE, john.mainAddress?.postCode)
    }


    /**
     * DELETE
     */
    fun testDelete() {
        dataManager.delete(contact)
        checkNumEntitiesIs(Contact::class.java, 0)
        checkNumEntitiesIs(Address::class.java, 0)
    }

    /**
     * VALIDATION
     */
    fun testValidation() {
        val err = "Should have thrown a InvalidDependency/InvalidField exception"
        val invalidItem = createInvalidContact(CONTACT_ID)
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

        val invalidItem = createInvalidContact(CONTACT_ID)
        try {
            dataManager.save(invalidItem, false)
        } catch(e: Exception) {
            Assert.fail("Missing info should have been searched from the db.\nException: ${e.message}")
        }

        // Now check that the item was actually modified
        val fromDb = dataManager.find(Contact::class.java, CONTACT_ID) as Contact
        Assert.assertNotNull(fromDb)
        Assert.assertEquals(fromDb.mainAddress?.addressType?.symbol, invalidId)
    }

    /**
     * DEPENDENCY LOOKUP FAILURE (NOT FOUND)
     *
     * The same test as testDependencyLookup, but this time the missing dependency is not in the db.
     */
    fun testDependencyLookupFail() {

        // Create an invalid entity
        val invalidEntity = createInvalidContact(CONTACT_ID)
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


    /**
     * DEPENDENCY LOOKUP FAILURE (INVALID FIELD)
     *
     * The same test as testDependencyLookup, but this time the missing field is not a dependency.
     */
    fun testDependencyLookupFail2() {

        // Create an invalid entity
        val invalidEntity = createInvalidContactNotFixable(CONTACT_ID)
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
        checkNumEntitiesIs(Contact::class.java, 0)
    }


    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }


    /**
     * Creates a dummy Contact.
     */
    private fun createContact(id: String, name: String, reference: String, email: String, mobile: String): Contact {
        val mainAddress = Address.create(null, "street1 main", "street2 main", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY)
        val deliveryAddress = Address.create(null, "street1 dely", "street2 dely", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY)
        val contactPersonTypes = ContactPersonType.createList(CONTACT_PERSON_TYPES)
        val mainContactPerson = ContactPerson.create(contactPersonTypes, name, "job", "telephone", mobile, email, "fax", address = mainAddress)
        val contactTypes = ContactType.createList(CONTACT_TYPES)
        return Contact(id, SyncStatus.SYNC_SUCCESS, contactTypes, name, reference, mainAddress, deliveryAddress, mainContactPerson)
    }

    /**
     * This Contact can be fixed because it lacks some dependency details.
     */
    private fun createInvalidContact(id: String): Contact {
        // It will be invalid cos the AddressType has no symbol.
        // It should be fixable since AddressType is annotated as SupportsIdOnly
        val invalidAddressType = AddressType(invalidId, SyncStatus.SYNC_SUCCESS, invalidId)
        val ireland = Country.create(Country.Companion.Code.IE)
        val invalidAddress = Address(id, SyncStatus.SYNC_SUCCESS, "street1", "street2", "town", "county", "postCode", ireland, invalidAddressType)
        val deliveryAddress = Address.create(null, "street1 dely", "street2 dely", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY)
        val contactPersonTypes = ContactPersonType.createList(CONTACT_PERSON_TYPES)
        val mainContactPerson = ContactPerson.create(contactPersonTypes, name, "job", "telephone", "mobile", "email", "fax", address = invalidAddress)
        val contactTypes = ContactType.createList(CONTACT_TYPES)
        return Contact(id, SyncStatus.SYNC_SUCCESS, contactTypes, name, "reference", invalidAddress, deliveryAddress, mainContactPerson)
    }

    /**
     * This Contact can not be fixed because the name is missing!
     * 'name' is a mandatory field.
     */
    fun createInvalidContactNotFixable(id: String): Contact {
        return createContact(CONTACT_ID, "", "John's ref", "john@gmail.com", "123123123")
    }

    //endregion


}