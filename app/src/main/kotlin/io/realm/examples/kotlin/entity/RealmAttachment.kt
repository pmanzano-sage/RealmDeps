package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.Attachment
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
open class RealmAttachment(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var contextId: String = "",
        open var contextType: String = "",
        open var description: String = "",

        open var mimeType: String = "",
        open var fileSize: Int = 0,
        open var fileName: String = "",

        open var uri: String = "",
        open var url: String = "",
        open var tmpFile: String = ""

) : RealmDbModel {

    override fun toDto(): Attachment {
        return convertToDto(RealmAttachment::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        // basic fields
        if (contextId.isBlank()) {
            throw InvalidFieldException("RealmAttachment contextId can not be blank!\nOffending instance:\n${this}")
        }
        if (contextType.isBlank()) {
            throw InvalidFieldException("RealmAddressType contextType can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out Attachment> {
        return Attachment::class.java
    }


}
