package io.realm.examples.kotlin.dto

import android.os.Environment
import io.realm.examples.kotlin.dto.definition.Constants
import io.realm.examples.kotlin.dto.definition.StringUtils
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmAttachment
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Created by neil.wilkinson on 12/07/2016.
 *
 * <pre>
 * Meaning of the main fields:
 *
 * uri     = local path for the file as it is named right now
 * url     = relative url for the file on the server
 * tmpFile = temporary file name used while image is still not uploaded to the server
 * </pre>
 *
 */
/*
 * ONLINE MODE:
 * -------------------------------------------------------------------------------------------------
 * When an attachment is created to UPLOAD it to the server, it will look like this:
 *
 * localId = "e37742e5d9712232116824eab88992734777c1d4"
 * apiId   = ""
 * uri     = "/storage/emulated/0/SageOne/e37742e5d9712232116824eab88992734777c1d4.jpg"
 * url     = ""
 * tmpFile = "/storage/emulated/0/SageOne/e37742e5d9712232116824eab88992734777c1d4.jpg"
 *
 * -------------------------------------------------------------------------------------------------
 * When the attachment is updated with the info retrieved from the server, after a successful
 * upload, it will look like this:
 *
 * localId = "e37742e5d9712232116824eab88992734777c1d4"
 * apiId   = "61f6edf9-4b12-4b0b-9bd5-f03c576cbaf6"
 * uri     = /storage/emulated/0/SageOne/61f6edf9-4b12-4b0b-9bd5-f03c576cbaf6.jpg"
 * url     = "/accounts/v2/attachments/61f6edf9-4b12-4b0b-9bd5-f03c576cbaf6/file"
 * tmpFile = "/storage/emulated/0/SageOne/e37742e5d9712232116824eab88992734777c1d4.jpg"
 *
 * That means the tmpFile has been renamed to <apiId>.jpg
 *
 * OFFLINE MODE:
 * -------------------------------------------------------------------------------------------------
 * When an attachment is created to UPLOAD it to the server, it will look like this:
 *
 * localId = "e37742e5d9712232116824eab88992734777c1d4"
 * apiId   = "fakeid-61f6edf9-4b12-4b0b-9bd5-f03c576cbaf6"
 * uri     = "/storage/emulated/0/SageOne/e37742e5d9712232116824eab88992734777c1d4.jpg"
 * url     = ""
 * tmpFile = "/storage/emulated/0/SageOne/e37742e5d9712232116824eab88992734777c1d4.jpg"
 *
 * -------------------------------------------------------------------------------------------------
 * When the attachment is updated with the info retrieved from the server, after a successful
 * synchronisation, it will look like this:
 *
 * localId = "e37742e5d9712232116824eab88992734777c1d4"
 * apiId   = "1234567-4b12-4b0b-9bd5-f03c576cbaf8"
 * uri     = /storage/emulated/0/SageOne/1234567-4b12-4b0b-9bd5-f03c576cbaf8.jpg"
 * url     = "/accounts/v2/attachments/1234567-4b12-4b0b-9bd5-f03c576cbaf8/file"
 * tmpFile = "/storage/emulated/0/SageOne/e37742e5d9712232116824eab88992734777c1d4.jpg"
 */
data class Attachment(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),

        var contextId: String = "",
        /**
         * Possible values are:
         * "incomes"
         * "expenses"
         * "contacts"
         */
        var contextType: String = "",
        var description: String = "",

        var mimeType: String = "",
        var fileSize: Int = 0,
        var fileName: String = "",

        var uri: String = "",
        var url: String = "",
        var tmpFile: String = ""

) : Dto {

    override fun toDisplayString(): String {
        return uri
    }

    /**
     * Instantiate a new Attachment providing a temporary path in the "uri" parameter.
     * It should be used when uploading a new attachment to the server.
     */
    constructor(uri: String, mime: String?, type: String, ownerApiId: String, description: String) : this(id = "", sync = SyncStatus.getDefault(), uri = uri, description = description, contextId = ownerApiId, contextType = type, mimeType = mime ?: "") {
        fileName = fileFromUri()
        if (StringUtils.isEmpty(tmpFile)) {
            tmpFile = buildLocalPath(fileName)
        }
    }

    fun fileFromUri() = uri.takeLastWhile { c -> c != '/' }

    /**
     * Returns the directory used to store images used in attachments.
     */
    fun getImagesPath(): String {
        val attachmentsFolder = Constants.ATTACHMENTS_FOLDER
        return Environment.getExternalStorageDirectory().absolutePath + "/$attachmentsFolder"
    }

    fun buildLocalPath(fname: String): String {
        return getImagesPath() + "/$fname"
    }

    override fun getDbClass(): Class<out RealmAttachment> {
        return RealmAttachment::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmAttachment {
        return convertToDb(Attachment::class.java, getDbClass())
    }


}