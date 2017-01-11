package io.realm.examples.kotlin

import io.realm.examples.kotlin.data.Dto

/**
 * @author Pablo Manzano
 * @since 10/01/17
 */
class CrudUtils {

    companion object {

        fun <T : Dto> createInvalidEntity(clazz: Class<T>, id: String): Dto {
            val ctor = clazz.constructors.first()
            val dto = ctor.newInstance()
            val field = clazz.declaredFields[0]
            field.isAccessible = true
            field.set(dto, id)
            return dto as T
        }
    }
}