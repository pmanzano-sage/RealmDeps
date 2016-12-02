package io.realm.examples.kotlin.dto

import android.util.Log

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

fun DtoCat.log() {
    for (prop in DtoCat::class.java.declaredFields) {
        println("${prop.name} = ${prop.get(this)}")
    }
}

fun <F, T> F.convertTo(fromClazz: Class<F>, toClazz: Class<T>): T {
    val instance = toClazz.getConstructor().newInstance()
    for (prop in fromClazz.declaredFields) {
        prop.isAccessible = true
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


