package io.realm.examples.kotlin.data

import android.util.Log
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.examples.kotlin.dto.definition.Constants
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

/**
 * @author Pablo Manzano
 */

@Target(AnnotationTarget.FIELD)
annotation class CascadeOnDelete

@Target(AnnotationTarget.CLASS)
annotation class SupportsIdOnly


fun generateId(): String {
    // foo-8374-4ece-afef-fc7f7cd0e634
    return Constants.FAKE_API_ID_PREFIX + UUID.randomUUID().toString().substring(Constants.FAKE_API_ID_PREFIX.length)
}


/**
 * This interface has to be implemented by all the entities that require a back link to its parent.
 * As a side effect, these entities will be cascade deleted when its parent is deleted.
 */
interface BackLink {
    var parentId: String
}

/**
 * Generic function to convert a Dto to a RealmObject.
 */
fun <F, T> F.convertToDb(fromClazz: Class<F>, toClazz: Class<T>): T {
    val TAG = "toDbModel"
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
                type == Long::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Long::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
                type == Double::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Double::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
            // Kotlin is doing something strange when dealing with booleans.
            // Sometimes the type is not unboxed so we have to compare with "java.lang.Boolean"
                type == Boolean::class.java || type.name == "java.lang.Boolean" -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Boolean::class.java)
                    setMethod.invoke(instance, field.get(this))
                }
                type == SyncStatus::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Int::class.java)
                    val value = field.get(this) as SyncStatus
                    setMethod.invoke(instance, value.ordinal)
                }
                Dto::class.java.isAssignableFrom(type) -> {
                    // Get the object from the source field, and convert it to DTO.
                    val dtoObject = field.get(this) as? Dto
                    // Set the DbModel object to the target field
                    val targetField = toClazz.getDeclaredField(field.name)
                    targetField.isAccessible = true
                    dtoObject?.toDbModel().let { targetField.set(instance, it) }
                }
                List::class.java.isAssignableFrom(type) -> {
                    // Get the list from the source field
                    val list = field.get(this) as? List<*>
                    val dbList = RealmList<RealmModel>()
                    list?.map { it as Dto }?.mapTo(dbList, Dto::toDbModel)
                    // Set the DTO list to the target field
                    val targetField = toClazz.getDeclaredField(field.name)
                    targetField.isAccessible = true
                    targetField.set(instance, dbList)
                }
                type.name.contains("$") -> {
                    // Log.w(TAG, "Inner '${type.name}' not mapped in '$toClazz'")
                }
                else -> Log.w(TAG, "'${type.name}' not mapped in '$toClazz'")
            }
        } catch (e: NoSuchMethodException) {
            Log.e(TAG, "Method ${e.message} not found in $toClazz")
        } catch (e: TypeCastException) {
            e.printStackTrace()
            throw e
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

            // Todo Move this after checking the type
            val targetField = toClazz.getDeclaredField(field.name)
            targetField.isAccessible = true

            when {
                type == String::class.java -> targetField.set(instance, field.get(this))
                type == Int::class.java -> {
                    if (field.name == "sync") {
                        targetField.set(instance, SyncStatus.values()[field.get(this) as Int])
                    } else {
                        targetField.set(instance, field.get(this))
                    }
                }
                type == Long::class.java -> targetField.set(instance, field.get(this))
                type == Double::class.java -> targetField.set(instance, field.get(this))
                type == Boolean::class.java -> targetField.set(instance, field.get(this))
                DbModel::class.java.isAssignableFrom(type) -> {
                    // Get the object from the source field, and convert it to DTO.
                    val dbObject = field.get(this) as? DbModel
                    // Set the DTO to the target field
                    dbObject?.toDto().let { targetField.set(instance, it) }
                }
                RealmList::class.java.isAssignableFrom(type) -> {
                    // Get the list from the source field
                    val list = field.get(this) as? RealmList<*>
                    val dtoList = arrayListOf<Dto>()
                    list?.map { it as DbModel }?.mapTo(dtoList, DbModel::toDto)
                    // Set the DTO list to the target field
                    targetField.set(instance, dtoList)
                }
                type.name.contains("Companion") -> {
                    // Log.w(TAG, "'${type.name}' not mapped in '$toClazz'")
                }
                else -> Log.w(TAG, "Type '${type.name}' not mapped in '$toClazz'")
            }
        } catch (e: NoSuchFieldException) {
            Log.e(TAG, "${e.message}")
        } catch (e: TypeCastException) {
            e.printStackTrace()
            throw e
        }
    }

    return instance
}
