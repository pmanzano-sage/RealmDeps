package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Amount model
 */
data class Amount(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val value: Double,
        val currencyCode: String
) : Dto {
    override fun toDb(): Db {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }

    override fun getDbClass(): Class<out Db> {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDisplayString(): String {
        return "$value"
    }

    constructor(value: Double, currencyCode: String) : this("", SyncStatus.getDefault(), value, currencyCode)
}