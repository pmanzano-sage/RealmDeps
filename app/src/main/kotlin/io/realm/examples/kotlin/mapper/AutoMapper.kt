package io.realm.examples.kotlin.mapper

import android.util.Log
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.examples.kotlin.dto.definition.Constants
import io.realm.examples.kotlin.dto.definition.StringUtils
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

@Target(AnnotationTarget.FIELD)
annotation class Exclusive

interface Dto {
    val id: String
    var sync: SyncStatus

    fun toDisplayString(): String

    fun isPersistedOnServer() = !StringUtils.isEmpty(id) && !id.startsWith(Constants.FAKE_API_ID_PREFIX)

    fun toDb(): Db
    fun checkValid(): Dto
    fun getDbClass(): Class<out Db>
}

fun generateId(): String {
    // foo-8374-4ece-afef-fc7f7cd0e634
    return Constants.FAKE_API_ID_PREFIX + UUID.randomUUID().toString().substring(Constants.FAKE_API_ID_PREFIX.length)
}


interface Db : RealmModel {
    var id: String
    var sync: Int

    fun toDto(): Dto
    fun readyToSave(): Boolean
    fun getDtoClass(): Class<out Dto>
    fun delete(realm: Realm): Boolean
}

/**
 * This interface has to be implemented by all the entities that require a backlink to its parent.
 */
interface DbChild : RealmModel {
    var parentId: String
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
                type == SyncStatus::class.java -> {
                    val setMethod = toClazz.getDeclaredMethod(methodName, Int::class.java)
                    val value = field.get(this) as SyncStatus
                    setMethod.invoke(instance, value.ordinal)
                }
                Dto::class.java.isAssignableFrom(type) -> {
                    // We can also detect annotations in the field.
//                    if (targetField.isAnnotationPresent(Exclusive::class.java)) {
//                        Log.d(TAG, "Field '${targetField.name}' is Exclusive")
//                    }
                    Log.e(TAG, "Type '${type.name}' is primitive = ${type.isPrimitive}")
                    // Get the object from the source field, and convert it to DTO.
                    val dtoObject = field.get(this) as? Dto
                    // Set the Db object to the target field
                    val targetField = toClazz.getDeclaredField(field.name)
                    targetField.isAccessible = true
                    dtoObject?.toDb().let { targetField.set(instance, it) }
                }
                List::class.java.isAssignableFrom(type) -> {
                    // We can also detect annotations in the field.
//                    if (targetField.isAnnotationPresent(Exclusive::class.java)) {
//                        Log.d(TAG, "List field '${targetField.name}' is Exclusive")
//                    }
                    Log.e(TAG, "Type '${type.name}' is primitive = ${type.isPrimitive}")
                    // Get the list from the source field
                    val list = field.get(this) as List<*>
                    val dbList = RealmList<RealmModel>()
                    list.map { it as Dto }.mapTo(dbList, Dto::toDb)
                    // Set the DTO list to the target field
                    val targetField = toClazz.getDeclaredField(field.name)
                    targetField.isAccessible = true
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
                type == Int::class.java -> {
                    if (field.name == "sync") {
                        targetField.set(instance, SyncStatus.values()[field.get(this) as Int])
                    } else {
                        targetField.set(instance, field.get(this))
                    }
                }
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
                    val list = field.get(this) as? RealmList<*>
                    val dtoList = arrayListOf<Dto>()
                    list?.map { it as Db }?.mapTo(dtoList, Db::toDto)
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
 * Generic function to delete a RealmObject all together with its exclusive dependencies.
 */
fun <F> F.deleteCascade(fromClazz: Class<in F>, realm: Realm): Boolean {
    val success = false
    val TAG = "deleteCascade"
    val fields = fromClazz.declaredFields

    for (field in fields) {
        field.isAccessible = true
        try {
            val type = field.type
            when {
                DbChild::class.java.isAssignableFrom(type) -> {
                    // Get the object from the source field, and delete it
                    val dbObject = field.get(this) as? DbChild
                    dbObject.let { RealmObject.deleteFromRealm(it) }
                }
                RealmList::class.java.isAssignableFrom(type) -> {
                    val gentype = field.genericType
                    Log.e(TAG, "Generic type '${gentype}'")
                    // Get the list from the source field, and delete all the entities in the list.
                    val list = field.get(this) as RealmList<*>
                    list.map { it as Db }.map { RealmObject.deleteFromRealm(it) }
                }
                else -> Log.w(TAG, "'${field.name}' kept")
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }
    return success
}
