package io.realm.examples.kotlin.model

import io.realm.examples.kotlin.db.DbDog
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class Dog(var name: String = "", var age: Int = 0) : Dto {

    override fun toDb(): DbDog {
        return convertToDb(Dog::class.java, DbDog::class.java)
    }

}