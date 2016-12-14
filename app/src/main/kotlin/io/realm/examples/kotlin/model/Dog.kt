package io.realm.examples.kotlin.model

import io.realm.examples.kotlin.db.DbDog
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class Dog(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var name: String = "",
        var age: Int = 0) : Dto {

    // If client code does not provide an id, a random one is generated.
    constructor(name: String, age: Int) : this(
            generateId(),
            sync = SyncStatus.getDefault(),
            name = name,
            age = age
    )

    override fun getDbClass(): Class<out DbDog> {
        return DbDog::class.java
    }

    override fun toDb(): DbDog {
        return convertToDb(Dog::class.java, getDbClass())
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw IllegalArgumentException("Dog name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDisplayString(): String {
        return name
    }

}