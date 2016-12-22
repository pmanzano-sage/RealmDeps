package io.realm.examples.kotlin.entity

import android.util.Log
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.SupportsIdOnly
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.ContactPersonType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
@SupportsIdOnly
open class RealmContactPersonType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var symbol: String = "",
        open var name: String = ""
) : DbModel {

    override fun toDto(): ContactPersonType {
        return convertToDto(RealmContactPersonType::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        val ready = !symbol.isBlank() && !name.isBlank()
        Log.d("RealmContactPersonType", "ready=$ready")
        return ready
    }

    override fun getDtoClass(): Class<out ContactPersonType> {
        return ContactPersonType::class.java
    }

}
