package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.Business
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

// In v2 this class had one plain address merged into this one.
// Now that we know how to handle deps better we split it.

@RealmClass
open class RealmBusiness(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var name: String = "",
        open var website: String = "",
        open var telephone: String = "",
        open var mobile: String = "",

        @CascadeOnDelete
        open var address: RealmAddress = RealmAddress()
) : RealmDbModel {

    override fun toDto(): Business {
        return convertToDto(RealmBusiness::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        // basic fields
        if (name.isBlank()) {
            throw InvalidFieldException("RealmBusiness name can not be blank!\nOffending instance:\n${this}")
        }
        // dependencies
        try {
            address.checkValid()
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("RealmBusiness has invalid dependencies", e)
        }

        return this
    }

    override fun getDtoClass(): Class<out Business> {
        return Business::class.java
    }


}
