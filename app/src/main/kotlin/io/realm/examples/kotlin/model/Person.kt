package io.realm.examples.kotlin.model

import io.realm.examples.kotlin.db.DbPerson
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class Person(var id: Long = 0,
                  var name: String = "",
                  var age: Int = 0,
                  var dog: Dog? = null,
                  var cats: List<Cat> = arrayListOf()) : Dto {

    override fun toDb(): DbPerson {
        return convertToDb(Person::class.java, DbPerson::class.java)
    }

    fun log() {
        println("Person {")
        for (prop in Person::class.java.declaredFields) {
            // ${prop.genericType} ${prop.type}
            println("\t${prop.name} = ${prop.get(this)}")
        }
        println("}")
    }

}