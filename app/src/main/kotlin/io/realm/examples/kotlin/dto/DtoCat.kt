package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.model.Cat

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class DtoCat(val name: String = "", val age: Int = 0) {
    fun log() {
        for (prop in DtoCat::class.java.declaredFields) {
            println("${prop.name} = ${prop.get(this)}")
        }
    }

    fun toCat(): Cat {
        return convertTo(DtoCat::class.java, Cat::class.java)
    }

}