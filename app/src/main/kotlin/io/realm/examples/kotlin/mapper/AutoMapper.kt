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

/**
 * Generic function to convert a Dto to a RealmObject.
 */
fun <F, T> F.convertToDb(fromClazz: Class<F>, toClazz: Class<T>): T {
    val TAG = "toDb"
    val instance = toClazz.newInstance()
    val fields = fromClazz.declaredFields
    for (field in fields) {
        field.isAccessible = true
        try {
            val methodName = "set${field.name.capitalize()}"
            val type = field.type

            val targetField = toClazz.getDeclaredField(field.name)
            targetField.isAccessible = true

            when {
                type == String::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, String::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
                type == Int::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Int::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
                type == Long::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Long::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
                type == Double::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Double::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
                Dto::class.java.isAssignableFrom(type) -> {
                    // Get the object from the source field, and convert it to DTO.
                    val dtoObject = field.get(this) as? Dto
                    // Set the Db object to the target field
                    dtoObject?.toDb().let { targetField.set(instance, it) }
                }
                List::class.java.isAssignableFrom(type) -> {
                    // type.name.equals("java.util.List") -> {
                    // Get the list from the source field
                    val list = field.get(this) as List<*>
                    val dbList = RealmList<RealmModel>()
                    list.map { it as Dto }.mapTo(dbList) { it.toDb() }
                    // Set the DTO list to the target field
                    targetField.set(instance, dbList)
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
 * Generic function to convert a RealmObject to a Dto.
 */
fun <F, T> F.convertToDto(fromClazz: Class<in F>, toClazz: Class<out T>): T {
    val TAG = "toDTO"
    val instance = toClazz.newInstance()
    val fields = fromClazz.declaredFields

    for (field in fields) {

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
                Db::class.java.isAssignableFrom(type) -> {
                    // Get the object from the source field, and convert it to DTO.
                    val dbObject = field.get(this) as? Db
                    // Set the DTO to the target field
                    dbObject?.toDto().let { targetField.set(instance, it) }
                }
                RealmList::class.java.isAssignableFrom(type) -> {
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
