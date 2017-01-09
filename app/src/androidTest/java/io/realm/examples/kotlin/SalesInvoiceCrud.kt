package io.realm.examples.kotlin

import android.test.AndroidTestCase
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.*
import io.realm.examples.kotlin.dto.definition.SyncStatus
import junit.framework.Assert
import java.util.*

/**
 * @author Pablo Manzano
 */
class SalesInvoiceCrud : AndroidTestCase() {

    val CURRENCY_CODE = "GBP"

    // QTY & PRICE for each invoice line
    private val LINE1 = "LINE1"
    private val QTY1 = 5.0
    private val PRICE1 = 0.2

    private val LINE2 = "LINE2"
    private val QTY2 = 6.0
    private val PRICE2 = 0.25

    private val LINE3 = "LINE3"
    private val QTY3 = 2.0
    private val PRICE3 = 1.0

    private val LINE4 = "LINE4"
    private val QTY4 = 10.0
    private val PRICE4 = 0.1

    private val DEFAULT_TAX = 0.1

    // Some fixed data to create test payments
    private val DEFAULT_PAY_ACCOUNT = Account.create(id = null, type = AccountType.Companion.V3.CASH_IN_HAND)

    private val PAY1 = "PAY1"
    private val PAY1_AMOUNT = 1.1
    private val PAY1_DATE = "20160101"

    private val PAY2 = "PAY2"
    private val PAY2_AMOUNT = 2.2
    private val PAY2_DATE = "20160201"

    private val PAY3 = "PAY3"
    private val PAY3_AMOUNT = 3.3
    private val PAY3_DATE = "20160301"

    // Basic invoice that will be created in the db
    private val API_ID_1 = "apiId1"
    private val INVOICE_NUMBER = "123"
    private val INVOICE_REF = "REF123"
    private val INVOICE_DATE = "20160101"
    private val CONTACT_ID = "contact1"

    private val INVOICE_LINES = arrayOf(LINE1, LINE2, LINE3)
    private val INVOICE_PAYMENTS = arrayOf(PAY1, PAY3)

    private val UPDATED_INVOICE_LINES = arrayOf(LINE4, LINE2)
    private val UPDATED_INVOICE_PAYMENTS = arrayOf(PAY3, PAY2, PAY1)

    private val CONTACT_TYPES = arrayListOf(ContactType.Companion.V3.PURCHASING, ContactType.Companion.V3.ACCOUNTS)
    private val CONTACT_PERSON_TYPES = arrayListOf(ContactPersonType.Companion.V3.CONTRACTOR)

    private val testInvoice = createInvoice(API_ID_1, INVOICE_NUMBER, INVOICE_REF, INVOICE_DATE, INVOICE_LINES, INVOICE_PAYMENTS)
    private lateinit var dataManager: DataManager

    /**
     * Start with a fresh db without any invoices and create one with API_ID_1 and 3 invoice lines.
     */
    override fun setUp() {
        super.setUp()

        // Initialize Realm & DataManager
        Realm.init(getContext())
        val realmConfig = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(realmConfig)
        dataManager = RealmDataManager(Realm.getDefaultInstance())

        // Note that we are deleting all the entities before launching any test
        dataManager.deleteAll()
    }

    override fun tearDown() {
        super.tearDown()
    }

    /**
     * SAVE
     *
     * Checks that:
     * - The invoice created in [.setUp] is correctly saved into the db.
     * - It has the correct number of invoice lines (3)
     */
    fun testSaveSalesInvoice() {
        dataManager.save(testInvoice)
        checkNumEntitiesIs(SalesInvoice::class.java, 1)
        checkNumEntitiesIs(InvoiceLine::class.java, INVOICE_LINES.size.toLong())
        checkNumEntitiesIs(Payment::class.java, INVOICE_PAYMENTS.size.toLong())
    }

    /**
     * UPDATE
     *
     * Checks that:
     * - Dependent invoice lines are updated correctly.
     * - Dependent payments are updated correctly.
     * - No dangling dependencies are left.
     */
    fun testUpdateSalesInvoice1() {

        dataManager.save(testInvoice)
        // Create an invoice with everything equal to testInvoice, except for the invoice lines, totals, etc...
        val updatedInvoice = createInvoice(API_ID_1, INVOICE_NUMBER, INVOICE_REF, INVOICE_DATE, UPDATED_INVOICE_LINES,
                UPDATED_INVOICE_PAYMENTS)

        // Since both invoices have the same apiId, saving it to the db should trigger an update operation.
        dataManager.update(updatedInvoice)

        checkNumEntitiesIs(SalesInvoice::class.java, 1)
        checkNumEntitiesIs(InvoiceLine::class.java, UPDATED_INVOICE_LINES.size.toLong())
        checkNumEntitiesIs(Payment::class.java, UPDATED_INVOICE_PAYMENTS.size.toLong())

    }


    /**
     * DELETE
     *
     * Checks that:
     * - After deleting all the invoices there are no invoices left in the db.
     * - It also checks that no dependencies (in this case invoice lines and payments) are left in the db.
     */
    fun testDeleteSalesInvoice() {

        dataManager.save(testInvoice, true)
        dataManager.delete(testInvoice)

        checkNumEntitiesIs(SalesInvoice::class.java, 0)
        checkNumEntitiesIs(InvoiceLine::class.java, 0)
        checkNumEntitiesIs(Payment::class.java, 0)
    }


    //region Auxiliary methods
    /**
     * Create a test invoice using the line items received in the last param.
     */
    private fun createInvoice(
            id: String, invoiceNumber: String, reference: String, date: String,
            items: Array<String>, payItems: Array<String>): SalesInvoice {

        val lines = ArrayList<InvoiceLine>()
        val payments = ArrayList<Payment>()

        var total: Double = 0.0
        var net: Double = 0.0
        var tax: Double = 0.0

        // Invoice lines
        for (str in items) {
            var line: InvoiceLine? = null
            if (str.equals(LINE1, ignoreCase = true)) {
                line = createLine(LINE1, id, QTY1, PRICE1, reference + " line1")
                lines.add(line)
            } else if (str.equals(LINE2, ignoreCase = true)) {
                line = createLine(LINE2, id, QTY2, PRICE2, reference + " line2")
                lines.add(line)
            } else if (str.equals(LINE3, ignoreCase = true)) {
                line = createLine(LINE3, id, QTY3, PRICE3, reference + " line3")
                lines.add(line)
            } else if (str.equals(LINE4, ignoreCase = true)) {
                line = createLine(LINE4, id, QTY4, PRICE4, reference + " line4")
                lines.add(line)
            } else {
                Log.e("ugh", "invalid line type")
            }
            if (line != null) {
                total += line.totalAmount
                net += line.netAmount
                tax += line.taxAmount
            }
        }

        var outstanding = total

        // Payments
        for (str in payItems) {
            val payment = when {
                str.equals(PAY1, ignoreCase = true) -> {
                    createPayment(PAY1, id, PAY1_AMOUNT, reference + " pay1", PAY1_DATE)
                }
                str.equals(PAY2, ignoreCase = true) -> {
                    createPayment(PAY2, id, PAY2_AMOUNT, reference + " pay2", PAY2_DATE)
                }
                str.equals(PAY3, ignoreCase = true) -> {
                    createPayment(PAY3, id, PAY3_AMOUNT, reference + " pay3", PAY3_DATE)
                }
                else -> {
                    Log.e("ugh", "invalid payment")
                    createPayment(PAY3, id, PAY3_AMOUNT, reference + " pay3", PAY3_DATE)
                }
            }
            payments.add(payment)
            outstanding -= payment.amount
        }

        val contact = createContact("contact1")

        return SalesInvoice(id, SyncStatus.SYNC_SUCCESS, "SI-" + invoiceNumber, total, net, tax, outstanding, CURRENCY_CODE,
                invoiceNumber, reference, date, date, "notes", "T&C", lines, payments, contact)
    }

    /**
     * Creates an invoice line.
     */
    private fun createLine(id: String, parentId: String, qty: Double?, price: Double?, description: String): InvoiceLine {
        val total = qty!! * price!!
        val netPercentage = 1 - DEFAULT_TAX
        val net = total * netPercentage
        val tax = total * DEFAULT_TAX
        return InvoiceLine(id, SyncStatus.SYNC_SUCCESS, description, qty, price, net, tax, null, total, parentId)
    }

    /**
     * Creates a payment with some default options.
     */
    private fun createPayment(id: String, parentId: String, amount: Double, description: String, date: String): Payment {
        return Payment(id, SyncStatus.SYNC_SUCCESS, description, amount, CURRENCY_CODE, date, DEFAULT_PAY_ACCOUNT, parentId)
    }

    private fun createContact(name: String): Contact {
        return createContact(name, "ref", "email", "phone")
    }

    /**
     * Creates a dummy Contact.
     */
    private fun createContact(name: String, reference: String, email: String, mobile: String): Contact {
        val mainAddress = Address.create(null, "street1 main", "street2 main", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY)
        val deliveryAddress = Address.create(null, "street1 dely", "street2 dely", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY)
        val contactPersonTypes = ContactPersonType.createList(CONTACT_PERSON_TYPES)
        val mainContactPerson = ContactPerson.create(null, contactPersonTypes, name, "job", "telephone", mobile, email, "fax", address = mainAddress)
        val contactTypes = ContactType.createList(CONTACT_TYPES)
        return Contact.create(contactTypes, name, reference, mainAddress, deliveryAddress, mainContactPerson)
    }

    private fun <T : Dto> checkNumEntitiesIs(clazz: Class<T>, numEntities: Long) {
        Assert.assertEquals(numEntities, dataManager.count(clazz))
    }
    //endregion


}