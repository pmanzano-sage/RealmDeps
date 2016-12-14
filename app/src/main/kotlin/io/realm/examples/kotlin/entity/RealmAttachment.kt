package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Attachment
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

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

) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmAttachment::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Attachment> {
        return Attachment::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmAttachment::class.java, realm)
    }

}
