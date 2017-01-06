package io.realm.examples.kotlin.data

import io.realm.examples.kotlin.dto.definition.Constants
import io.realm.examples.kotlin.dto.definition.StringUtils
import io.realm.examples.kotlin.dto.definition.SyncStatus

/**
 * @author Pablo Manzano
 */
interface Dto {
    val id: String
    var sync: SyncStatus

    fun toDisplayString(): String

    fun isPersistedOnServer() = !StringUtils.isEmpty(id) && !id.startsWith(Constants.FAKE_API_ID_PREFIX)

    fun toDbModel(): RealmDbModel

    // Returns the instance so as to easily apply this method to collections.
    fun checkValid(): Dto

    fun getDbClass(): Class<out RealmDbModel>

    /**
     * Example of usage:
     * val (ident,status) = initialize(id)
     *
     * NOTE: We do not set directly id member cos we want to keep it immutable.
     */
    companion object {
        fun init(id: String?): Pair<String, SyncStatus> {
            val finalId: String
            val status: SyncStatus
            if (id.isNullOrBlank()) {
                finalId = generateId()
                status = SyncStatus.getDefaultLocal()
            } else {
                finalId = id!!
                status = SyncStatus.getDefault()
            }
            return Pair(finalId, status)
        }
    }
}
