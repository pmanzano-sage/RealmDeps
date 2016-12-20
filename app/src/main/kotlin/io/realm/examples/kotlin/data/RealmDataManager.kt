package io.realm.examples.kotlin.data

import android.util.Log
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.examples.kotlin.dto.definition.StringUtils

/**
 * @author Pablo Manzano
 * @since 13/12/16
 */

/**
 * This is a Realm implementation of the DataManager.
 */
class RealmDataManager(realm: Realm) : DataManager {
    private val realm: Realm

    init {
        this.realm = realm
    }

    /**
     * Get all the entities of the received class.
     */
    override fun getAll(clazz: Class<out Dto>): List<Dto> {
        // TODO There should be a better way to figure out the dbClass (instead of creating an instance)
        val dto = clazz.newInstance()
        return findAllDb(dto.getDbClass()).map(DbModel::toDto)
    }

    /**
     * Delete all the entities in the db.
     */
    override fun deleteAll() {
        realm.executeTransaction {
            realm.deleteAll()
        }
    }

    override fun count(clazz: Class<out Dto>): Long {
        val dto = clazz.newInstance()
        return countInDb(dto.getDbClass())
    }

    /**
     * Find an entity with the received id.
     */
    override fun find(clazz: Class<out Dto>, id: String): Dto? {
        // TODO There should be a better way to figure out the dbClass (instead of creating an instance)
        val dto = clazz.newInstance()
        val dbEntity = findDb(dto.getDbClass(), id)
        if (dbEntity != null) {
            return realm.copyFromRealm(dbEntity).toDto()
        }
        return null
    }

    /**
     * Update an entity deleting old dependencies.
     * Throws an exception if the entity does not exist.
     */
    override fun update(dto: Dto): Boolean {
        val success: Boolean
        val dbEntity = findDb(dto.getDbClass(), dto.id)
        if (dbEntity != null) {
            success = save(dto, true)
        } else {
            throw Exception("Not found ${dto.javaClass.name} with id=${dto.id}")
        }
        return success
    }

    /**
     * Create a new entity. Throws an exception if the entity already exists.
     */
    override fun create(dto: Dto): Boolean {
        val success: Boolean
        val dbEntity = findDb(dto.getDbClass(), dto.id)
        if (dbEntity == null) {
            success = save(dto)
        } else {
            throw Exception("Already exists ${dto.javaClass.name} with id=${dto.id}")
        }
        return success
    }

    /**
     * Delete an entity.
     * Cascades the deletion to the exclusive dependencies.
     */
    override fun delete(dto: Dto): Boolean {
        var success = false
        realm.executeTransaction {
            val dbEntity = findDb(dto.getDbClass(), dto.id)
            if (dbEntity != null) {
                Log.d(TAG, "delete: dbEntity=$dbEntity")
                // dbEntity?.delete(realm)
                deleteCascade(dbEntity, realm)
                success = true
            } else {
                Log.e(TAG, "delete: ${dto.getDbClass()} with id=${dto.id} NOT FOUND")
            }
        }
        return success
    }

    /**
     * Delete an entity without cascading the deletion to its dependencies.
     */
    override fun deleteNonCascade(dto: Dto): Boolean {
        var success = false
        realm.executeTransaction {
            val dbEntity = findDb(dto.getDbClass(), dto.id)
            Log.d(TAG, "deleteNonCascade: dbEntity=$dbEntity")
            RealmObject.deleteFromRealm(dbEntity)
            success = true
        }
        return success
    }


    /**
     * Create or update an entity.
     */
    override fun save(dto: Dto): Boolean {
        return save(dto, true)
    }

    // region private methods

    private fun findAllDb(clazz: Class<out DbModel>): List<DbModel> {
        return realm.where(clazz).findAll()
    }

    private fun countInDb(clazz: Class<out DbModel>): Long {
        return realm.where(clazz).count()
    }

    private fun findDb(clazz: Class<out DbModel>, id: String): DbModel? {
        if (StringUtils.isEmpty(id)) {
            return null
        }
        return realm.where(clazz).equalTo("id", id).findFirst()
    }

    private fun save(dto: Dto, deleteDeps: Boolean = false): Boolean {
        var success = false
        val dbEntity = dto.checkValid().toDbModel()
        if (dbEntity.readyToSave()) {
            try {
                realm.executeTransaction {
                    if (deleteDeps) {
                        val clazz = dto.getDbClass()
                        val dbObj = findDb(clazz, dto.id)
                        dbObj?.let { deleteCascade(it, realm) }
                        Log.w(TAG, "save: Previous entity deleted")
                    }
                    realm.copyToRealmOrUpdate(dbEntity)
                    success = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "save: Exception: ${e.message}")
            }
        } else {
            Log.e(TAG, "save: Entity is not ready to be saved")
            throw Exception("DbModel entity can not be created")
        }
        return success
    }

    /**
     * Generic function to cascade a RealmObject deletion.
     * All DbModel objects that also implement BackLink interface will be deleted.
     */
    private fun deleteCascade(me: DbModel, realm: Realm, level: Int = 0) {
        val TAG = "deleteCascade"
        val TAB = "    "

        // Take the object out of Realm, otherwise realm proxy object will show us an empty instance.
        val myself = if (level == 0 && RealmObject.isManaged(me)) realm.copyFromRealm(me) else me
        val fields = myself.javaClass.declaredFields
        Log.i(TAG, "${TAB.repeat(level)} ${myself.javaClass.simpleName} ${myself.id} #fields=${fields.size}")

        // Recursively delete dependencies that implement BackLink interface
        for (field in fields) {
            field.isAccessible = true
            try {
                val type = field.type
                val cascade = field.isAnnotationPresent(CascadeOnDelete::class.java)
                when {
                    DbModel::class.java.isAssignableFrom(type) && (BackLink::class.java.isAssignableFrom(type) || cascade) -> {
                        // Get the object from the source field, and delete it
                        val dbObject = field.get(myself) as? DbModel
                        Log.w(TAG, "${TAB.repeat(level)} Object '${field.name}':")
                        // First of all, delete that object dependencies
                        dbObject?.let { deleteCascade(it, realm, level.inc()) }
                    }
                    RealmList::class.java.isAssignableFrom(type) && cascade -> {
                        val list = field.get(myself) as RealmList<*>
                        Log.w(TAG, "${TAB.repeat(level)} List '${field.name}': ${list.size} items")
                        // Get the list from the source field, and deleteCascade all the entities in the list.
                        list.map { it as DbModel? }.filterNotNull().map { deleteCascade(it, realm, level.inc()) }
                    }
                    else -> {
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
            }
        }

        // Finally delete this object
        try {
            val obj = realm.where(myself.javaClass).equalTo("id", me.id).findFirst()
            RealmObject.deleteFromRealm(obj)
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }


    companion object {
        val TAG = DataManager::class.java.simpleName

    }

    // endregion private methods

}