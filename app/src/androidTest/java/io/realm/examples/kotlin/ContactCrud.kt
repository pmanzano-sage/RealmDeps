package io.realm.examples.kotlin

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
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
     *
     * Checks that:
     * - The contact created in is correctly saved into the db.
     * - It has the correct number of dependencies.
     */
    fun testSaveContact() {
        dataManager.save(contact)
        checkNumEntitiesIs(Contact::class.java, 1)
        checkNumEntitiesIs(Address::class.java, 2)
    }

    /**
     * UPDATE
     *
     * Checks that:
     * - Dependencies are updated correctly.
     * - No dangling dependencies are left.
     */
    @Throws(Exception::class)
    fun testUpdateContact() {
        dataManager.save(contact)
        // Exactly the same contact, but we change the postal code of the main address
        val contactUpdated = createContact(CONTACT_ID, "John", "John's ref", "john@gmail.com", "123123123")
        contactUpdated.mainAddress?.postCode = UPDATED_POST_CODE
        dataManager.update(contactUpdated, false)

        checkNumEntitiesIs(Contact::class.java, 1)

        // now check the postal code that we have in the db
        val john = dataManager.find(Contact::class.java, CONTACT_ID) as Contact
        Assert.assertNotNull(john)
        Assert.assertEquals(UPDATED_POST_CODE, john.mainAddress?.postCode )
    }


    /**
     * DELETE
     *
     * Checks that:
     * - After deleting all the contacts there are no contacts left in the db.
     * - It also checks that no dependencies are left in the db.
     */
    @Throws(Exception::class)
    fun testDeleteContact() {
        dataManager.delete(contact)
        checkNumEntitiesIs(Contact::class.java, 0)
        checkNumEntitiesIs(Address::class.java, 0)
    }


    //region Auxiliary functions

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }


    /**
     * Creates a dummy Contact.
     */
    private fun createContact(id: String, name: String, reference: String, email: String, mobile: String): Contact {
        val mainAddress = Address.create("street1 main", "street2 main", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY)
        val deliveryAddress = Address.create("street1 dely", "street2 dely", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY)
        val contactPersonTypes = ContactPersonType.createList(CONTACT_PERSON_TYPES)
        val mainContactPerson = ContactPerson.create(contactPersonTypes, name, "job", "telephone", mobile, email, "fax", address = mainAddress)
        val contactTypes = ContactType.createList(CONTACT_TYPES)
        return Contact(id, SyncStatus.SYNC_SUCCESS, contactTypes, name, reference, mainAddress, deliveryAddress, mainContactPerson)
    }
    //endregion


}