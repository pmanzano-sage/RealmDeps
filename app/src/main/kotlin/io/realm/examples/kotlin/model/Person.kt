package io.realm.examples.kotlin.model

import io.realm.examples.kotlin.db.DbPerson

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class Person(val name: String = "", val age: Int = 0) {
    fun toPerson(): DbPerson {
        return convertTo(Person::class.java, DbPerson::class.java)
    }

}