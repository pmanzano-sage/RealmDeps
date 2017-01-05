package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.InvalidFieldException
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmMe

/**
 * Common Me model
 */
data class Me(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var serviceUids: List<String>
) : Dto {

    override fun getDbClass(): Class<out RealmMe> {
        return RealmMe::class.java
    }

    override fun checkValid(): Dto {
        if (serviceUids.isEmpty()) {
            throw InvalidFieldException("Me has no services!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmMe {
        return convertToDb(Me::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return serviceUids.joinToString()
    }
}