package io.realm.examples.kotlin.mapper

import android.util.Log
import io.realm.RealmList
import io.realm.RealmModel

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

interface Dto {
    fun toDb(): Db
}

interface Db : RealmModel {
    fun toDto(): Dto
}

fun <F, T> F.convertToDb(fromClazz: Class<F>, toClazz: Class<T>): T {
    val TAG = "toDb"
    val instance = toClazz.newInstance()
    val fields = fromClazz.declaredFields
    for (field in fields) {
        field.isAccessible = true
        try {
            val methodName = "set${field.name.capitalize()}"
            val type = field.type
            when {
                type == String::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, String::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
                type == Int::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Int::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
                type == Double::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Double::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
                isDbObject(type) -> {
                    // TODO
                    Log.w(TAG, "Pending mapping for ${type.name} in $toClazz")
                }
                isDbList(type) -> {
                    // TODO
                    Log.w(TAG, "Pending mapping for ${type.name} in $toClazz")
                }
                else -> Log.e(TAG, "Type '${type.name}' not mapped in '$toClazz'")
            }
        } catch (e: NoSuchMethodException) {
            Log.e(TAG, "Method ${e.message} not found in $toClazz")
        }
    }
    return instance
}

/**
 *
 */
fun <F, T> F.convertToDto(fromClazz: Class<in F>, toClazz: Class<out T>): T {
    val TAG = "toDTO"
    val instance = toClazz.newInstance()
    val fields = fromClazz.declaredFields

    for (field in fields) {
        // println("\t${field.name} : ${field.genericType} ${field.type}")

        field.isAccessible = true
        try {
            val type = field.type
            val targetField = toClazz.getDeclaredField(field.name)
            targetField.isAccessible = true

            when {
                type == String::class.java -> targetField.set(instance, field.get(this))
                type == Int::class.java -> targetField.set(instance, field.get(this))
                type == Long::class.java -> targetField.set(instance, field.get(this))
                type == Double::class.java -> targetField.set(instance, field.get(this))
                isDbObject(type) -> {
                    // Get the object from the source field, and convert it to DTO.
                    val dbObject = field.get(this) as? Db
                    // Set the DTO to the target field
                    dbObject?.toDto().let { targetField.set(instance, it) }
                }
                isDbList(type) -> {
                    // Get the list from the source field
                    val list = field.get(this) as RealmList<*>
                    val dtoList = list.map { it as Db }.map(Db::toDto)
                    // Set the DTO list to the target field
                    targetField.set(instance, dtoList)
                }
                else -> Log.e(TAG, "Type '${type.name}' not mapped in '$toClazz'")
            }
        } catch (e: NoSuchFieldException) {
            Log.e(TAG, "Field ${e.message} not found in $toClazz")
        }
    }

    return instance
}

/**
 * Given something like: 'class io.realm.examples.kotlin.db.DbDog'
 * returns true if the class starts with Db. (Hacky for the moment)
 */
fun isDbObject(clazz: Class<*>): Boolean {
    return clazz.name.split('.').last().startsWith("Db")
}

/**
 * Given something like: 'class io.realm.RealmList'
 * returns true if the class starts with Db. (Hacky for the moment)
 */
fun isDbList(clazz: Class<*>): Boolean {
    return clazz.name.split('.').last().contentEquals("RealmList")
}