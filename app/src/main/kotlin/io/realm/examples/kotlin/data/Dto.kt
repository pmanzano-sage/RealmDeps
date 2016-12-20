package io.realm.examples.kotlin.data

import io.realm.examples.kotlin.dto.definition.Constants
import io.realm.examples.kotlin.dto.definition.StringUtils
import io.realm.examples.kotlin.dto.definition.SyncStatus

/**
 * @author Pablo Manzano
 * @since 20/12/16
 */
interface Dto {
    val id: String
    var sync: SyncStatus

    fun toDisplayString(): String

    fun isPersistedOnServer() = !StringUtils.isEmpty(id) && !id.startsWith(Constants.FAKE_API_ID_PREFIX)

    fun toDbModel(): DbModel
    fun checkValid(): Dto
    fun getDbClass(): Class<out DbModel>
}
