package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.Dto.Companion.init
import io.realm.examples.kotlin.data.InvalidDependencyException
import io.realm.examples.kotlin.data.InvalidFieldException
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmAccount
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Common Bank Account model
 */
data class Account(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val displayName: String = "",
        val number: String = "",
        var accountType: AccountType? = null,
        val nominalCode: Int = 0,
        var balance: Amount = Amount.pounds(0.0)
) : Dto {

    var editable: Boolean = true
    val DECIMAL_FORMATTER: NumberFormat = DecimalFormat.getCurrencyInstance()

    override fun getDbClass(): Class<out RealmAccount> {
        return RealmAccount::class.java
    }

    override fun checkValid(): Dto {
        if (displayName.isBlank()) {
            throw InvalidFieldException("Account display name can not be blank!\nOffending instance:\n${this}")
        }
        try {
            accountType?.checkValid()
            balance.checkValid()
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("Account has invalid dependencies", e)
        }
        return this
    }

    //    override fun toDbModel(): RealmAccount {
    //        return convertToDb(Account::class.java, getDbClass())
    //    }

    // Custom mapper because of balance
    override fun toDbModel(): RealmAccount {
        return RealmAccount(
                id,
                sync.ordinal,
                displayName = displayName,
                number = number,
                balance = DECIMAL_FORMATTER.format(balance.value),
                currency = balance.currencyCode,
                nominalCode = nominalCode,
                editable = editable,
                accountType = accountType!!.toDbModel()
        )
    }

    override fun toDisplayString(): String {
        return displayName
    }

    // Convenient factory methods
    companion object {
        /**
         * Creates an empty account with nominal code in 0.
         * Example of usage:
         *
         * Account.create(AccountType.Companion.V3.CASH_IN_HAND)
         */
        fun create(id: String?, type: AccountType.Companion.V3, countryCode: Country.Companion.Code = Country.Companion.Code.UK): Account {
            val amount = 0.0
            val accountType = AccountType.create(type)
            val balance = when (countryCode) {
                Country.Companion.Code.UK -> Amount.pounds(amount)
                Country.Companion.Code.US -> Amount.dollars(amount)
                Country.Companion.Code.IE -> Amount.euros(amount)
                else -> Amount.pounds(amount)
            }
            val (finalId, status) = init(id)
            return Account(finalId, status, accountType.toDisplayString(), "", accountType, 0, balance)
        }
    }
}