package io.realm.examples.kotlin.entity

import android.util.Log
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.Account
import io.realm.examples.kotlin.dto.Amount
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*

@RealmClass
open class RealmAccount(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var nominalCode: Int = 0,
        open var displayName: String = "",
        open var number: String = "",
        open var accountType: RealmAccountType? = null,
        open var balance: String? = null,
        open var currency: String = "",
        open var editable: Boolean = true
) : RealmDbModel {


    //    override fun toDto(): Dto {
    //        return convertToDto(RealmAccount::class.java, getDtoClass())
    //    }

    override fun toDto(): Account {
        var balanceNum = 0.0
        try {
            balanceNum = DECIMAL_FORMATTER.parse(balance).toDouble()
        } catch (exception: ParseException) {
            Log.d("", "convertCustomToMain() balance parsing failed for string " + balance + ": " + exception.message)
        }
        val account = Account(id, SyncStatus.values()[sync], displayName, number, accountType?.toDto(), nominalCode,
                Amount(balanceNum, currency))
        account.editable = editable
        return account
    }


    override fun checkValid(): DbModel {
        if (displayName.isBlank()) {
            throw InvalidFieldException("RealmAccount displayName can not be blank!\nOffending instance:\n${this}")
        }
        try {
            accountType!!.checkValid()
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("RealmAccount has invalid dependencies", e)
        }
        return this
    }

    override fun getDtoClass(): Class<out Account> {
        return Account::class.java
    }

    companion object {
        private val DECIMAL_FORMATTER = DecimalFormat.getCurrencyInstance()
    }

}
