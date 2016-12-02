package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.model.Dog

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

data class DtoDog(val name: String = "", val age: Int = 0) {
    fun toDog(): Dog {
        return convertTo(DtoDog::class.java, Dog::class.java)
    }

}