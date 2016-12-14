package io.realm.examples.kotlin.model

import io.realm.examples.kotlin.db.DbCat
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * @author Pablo Manzano
 * @since 01/12/16
 *
 * In order to create a new DTO class follow these steps:
 * - implement Dto interface
 */

data class Cat(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var name: String = "",
        var age: Int = 0,
        var dog: Dog? = null) : Dto {

    // If client code does not provide an id, a random one is generated marked also as local.
    constructor(name: String, age: Int, dog: Dog?) : this(
            generateId(),
            sync = SyncStatus.getDefault(),
            name = name,
            age = age,
            dog = dog
    )

    override fun getDbClass(): Class<out DbCat> {
        return DbCat::class.java
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw IllegalArgumentException("Cat name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDb(): DbCat {
        return convertToDb(Cat::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }

    fun log() {
        println("Cat {")
        for (prop in Cat::class.java.declaredFields) {
            println("\t${prop.name} = ${prop.get(this)}")
        }
        println("}")
    }
}