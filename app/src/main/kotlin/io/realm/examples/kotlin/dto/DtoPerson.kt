package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.model.Person

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class DtoPerson(val name: String = "", val age: Int = 0) {
    fun toPerson(): Person {
        return convertTo(DtoPerson::class.java, Person::class.java)
    }

}