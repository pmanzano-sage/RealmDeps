package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDbModel
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus

/**
 * Image File model
 *
 * NOTE: localId and apiId values will be copies of the owner entity.
 * The owner will be an Attachment or Contact or whatever entity this file belongs to.
 */
data class ImageFile(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var url: String = "",
        var localPath: String = ""

) : Dto {

    override fun toDbModel(): RealmDbModel {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }

    override fun getDbClass(): Class<out RealmDbModel> {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }


    override fun checkValid(): Dto {
        return this
    }


    override fun toDisplayString(): String {
        return id
    }

    constructor(ownerId: String, url: String, localPath: String?) : this(id = ownerId, sync = SyncStatus.getDefault(), url = url, localPath = localPath ?: "")

}