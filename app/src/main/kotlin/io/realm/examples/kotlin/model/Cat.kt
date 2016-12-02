package io.realm.examples.kotlin.model

import io.realm.examples.kotlin.db.DbCat

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class Cat(val name: String = "", val age: Int = 0) {
    fun log() {
        for (prop in Cat::class.java.declaredFields) {
            println("${prop.name} = ${prop.get(this)}")
        }
    }

    fun toDbCat(): DbCat {
        return convertTo(Cat::class.java, DbCat::class.java)
    }

}