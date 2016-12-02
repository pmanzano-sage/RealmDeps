package io.realm.examples.kotlin.model

import io.realm.examples.kotlin.db.DbDog

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class Dog(val name: String = "", val age: Int = 0) {
    fun toDog(): DbDog {
        return convertTo(Dog::class.java, DbDog::class.java)
    }

}