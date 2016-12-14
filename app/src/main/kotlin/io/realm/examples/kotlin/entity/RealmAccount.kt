package io.realm.examples.kotlin.entity

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Account
import io.realm.examples.kotlin.dto.Amount
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.deleteCascade
import io.realm.examples.kotlin.mapper.generateId
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*

open class RealmAccount(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var nominalCode: Int = 0,
        open var displayName: String? = null,
        open var number: String? = null,
        open var accountType: RealmAccountType = RealmAccountType(),
        open var balance: String? = null,
        open var currency: String = "",
        open var editable: Boolean = true
) : RealmObject(), Db {

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmAccount::class.java, realm)
    }

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
        val account = Account(id, SyncStatus.values()[sync],
                displayName!!, number!!, accountType.toDto(), nominalCode,
                Amount(balanceNum, currency))
        account.editable = editable
        return account
    }


    override fun readyToSave(): Boolean {
        // TODO check this criteria
        return accountType.readyToSave()
    }

    override fun getDtoClass(): Class<out Account> {
        Log.d("PAAAUUUUU", "MyClass=${this.javaClass}")
        return Account::class.java
    }

    companion object {
        private val DECIMAL_FORMATTER = DecimalFormat.getCurrencyInstance()
    }

}