package io.realm.examples.kotlin.model

import io.realm.examples.kotlin.db.DbToy
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class Toy(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var name: String = "",
        var price: Double = 0.0) : Dto {

    // If client code does not provide an id, a random one is generated.
    constructor(name: String, price: Double) : this(
            // generateId(),
            name,
            sync = SyncStatus.getDefault(),
            name = name,
            price = price
    )

    override fun getDbClass(): Class<out DbToy> {
        return DbToy::class.java
    }

    override fun toDb(): DbToy {
        return convertToDb(Toy::class.java, getDbClass())
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw IllegalArgumentException("Toy name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDisplayString(): String {
        return name
    }

}