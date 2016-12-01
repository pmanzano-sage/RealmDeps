package io.realm.examples.kotlin.dto

import android.util.Log
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

    fun <F, T> convertTo(fromClazz: Class<F>, toClazz: Class<T>): T {
        val instance = toClazz.getConstructor().newInstance()
        for (prop in DtoCat::class.java.declaredFields) {
            // for (prop in fromClazz.javaClass.declaredFields) {
            try {
                val methodName = "set${prop.name.capitalize()}"
                when (prop.type) {
                    String::class.java -> {
                        val setMethod = toClazz.getDeclaredMethod(methodName, String::class.java)
                        setMethod.invoke(instance, prop.get(this))
                    }
                    Int::class.java -> {
                        val setMethod = toClazz.getDeclaredMethod(methodName, Int::class.java)
                        setMethod.invoke(instance, prop.get(this))
                    }
                    Double::class.java -> {
                        val setMethod = toClazz.getDeclaredMethod(methodName, Double::class.java)
                        setMethod.invoke(instance, prop.get(this))
                    }
                }
            } catch (e: NoSuchMethodException) {
                Log.e("convertTo", "Method ${e.message} not found in $toClazz")
            }
        }
        return instance
    }


}