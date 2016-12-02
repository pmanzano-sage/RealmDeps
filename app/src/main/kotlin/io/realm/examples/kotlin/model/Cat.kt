package io.realm.examples.kotlin.model

import io.realm.examples.kotlin.db.DbCat
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class Cat(var name: String = "", var age: Int = 0, var dog: Dog? = null) : Dto {
    fun log() {
        println("Cat {")
        for (prop in Cat::class.java.declaredFields) {
            println("\t${prop.name} = ${prop.get(this)}")
        }
        println("}")
    }

    override fun toDb(): DbCat {
        return convertToDb(Cat::class.java, DbCat::class.java)
    }

}