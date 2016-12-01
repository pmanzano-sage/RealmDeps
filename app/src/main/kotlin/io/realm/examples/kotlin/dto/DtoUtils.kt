package io.realm.examples.kotlin.dto

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

fun DtoCat.log() {
    for (prop in DtoCat::class.java.declaredFields) {
        println("${prop.name} = ${prop.get(this)}")
    }
}


