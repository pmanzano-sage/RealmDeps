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
annotation class CascadeOnDelete

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
    // fun del(localStore: LocalStore): Boolean
    fun delete(realm: Realm): Boolean
}

// Just a tagging interface that we should use in place of Realm.
// Understand delegate syntax in Kotlin for this..
interface LocalStore

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
                    // Get the object from the source field, and convert it to DTO.
                    val dtoObject = field.get(this) as? Dto
                    // Set the Db object to the target field
                    val targetField = toClazz.getDeclaredField(field.name)
                    targetField.isAccessible = true
                    dtoObject?.toDb().let { targetField.set(instance, it) }
                }
                List::class.java.isAssignableFrom(type) -> {
                    // Get the list from the source field
                    val list = field.get(this) as? List<*>
                    val dbList = RealmList<RealmModel>()
                    list?.map { it as Dto }?.mapTo(dbList, Dto::toDb)
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
                type.name.contains("Companion") -> {
                    // Log.w(TAG, "'${type.name}' not mapped in '$toClazz'")
                }
                else -> Log.w(TAG, "Type '${type.name}' not mapped in '$toClazz'")
            }
        } catch (e: NoSuchFieldException) {
            Log.e(TAG, "${e.message}")
        }
    }

    return instance
}


/**
 * Generic function to cascade a RealmObject deletion.
 * All Db objects that also implement BackLink interface will be deleted.
 */
fun Db.deleteCascade(clazz: Class<out Db>, realm: Realm, level: Int = 0): Boolean {
    var success = true
    val TAG = "deleteCascade"
    val TAB = "    "
    val fields = clazz.declaredFields

    // Take the object out of Realm, otherwise realm proxy object will show us an empty instance.
    val myself = if (level == 0 && RealmObject.isManaged(this)) realm.copyFromRealm(this) else this

    Log.i(TAG, "${TAB.repeat(level)} ${myself.javaClass.simpleName} ${myself.id}")

    // Recursively delete dependencies that implement BackLink interface
    for (field in fields) {
        field.isAccessible = true
        try {
            val type = field.type
            val cascade = field.isAnnotationPresent(CascadeOnDelete::class.java)
            when {
                Db::class.java.isAssignableFrom(type) && (BackLink::class.java.isAssignableFrom(type) || cascade) -> {
                    // Get the object from the source field, and delete it
                    val dbObject = field.get(myself) as? Db
                    Log.w(TAG, "${TAB.repeat(level)} Object '${field.name}':")
                    // First of all, delete that object dependencies
                    dbObject?.deleteCascade(dbObject.javaClass, realm, level.inc())
                }
                RealmList::class.java.isAssignableFrom(type) && cascade -> {
                    val list = field.get(myself) as RealmList<*>
                    Log.w(TAG, "${TAB.repeat(level)} List '${field.name}': ${list.size} items")
                    // Get the list from the source field, and deleteCascade all the entities in the list.
                    list.map { it as Db? }.map { it?.deleteCascade(it.javaClass, realm, level.inc()) }
                }
                else -> {
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            success = false
        }
    }

    // Finally delete this object
    val obj = realm.where(myself.javaClass).equalTo("id", id).findFirst()
    RealmObject.deleteFromRealm(obj)

    if (level == 0) {
        Log.w(TAG, "success=$success")
    }

    return success
}
