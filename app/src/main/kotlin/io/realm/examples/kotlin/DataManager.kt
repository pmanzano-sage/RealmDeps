package io.realm.examples.kotlin

import android.util.Log
import io.realm.Realm
import io.realm.examples.kotlin.dto.definition.StringUtils
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.Dto

/**
 * @author Pablo Manzano
 * @since 13/12/16
 */
interface PersistenceProvider {
    fun deleteAll()
    fun getAll(clazz: Class<out Dto>): List<Dto>
    fun find(clazz: Class<out Dto>, id: String): Dto?
    fun save(dto: Dto): Boolean
    fun create(dto: Dto): Boolean
    fun update(dto: Dto): Boolean
    fun delete(dto: Dto): Boolean
}

/**
 * This is a Realm implementation of the PersistenceProvider.
 */
class DataManager(realm: Realm) : PersistenceProvider {
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
        return findAllDb(dto.getDbClass()).map(Db::toDto)
    }

    /**
     * Delete all the entities in the db.
     */
    override fun deleteAll() {
        realm.executeTransaction {
            realm.deleteAll()
        }
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
            Log.d(TAG, "delete: dbEntity=$dbEntity")
            dbEntity?.delete(realm)
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

    private fun findAllDb(clazz: Class<out Db>): List<Db> {
        return realm.where(clazz).findAll()
    }


    private fun findDb(clazz: Class<out Db>, id: String): Db? {
        if (StringUtils.isEmpty(id)) {
            return null
        }
        return realm.where(clazz).equalTo("id", id).findFirst()
    }

    private fun save(dto: Dto, deleteDeps: Boolean = false): Boolean {
        var success = false
        val dbEntity = dto.checkValid().toDb()
        if (dbEntity.readyToSave()) {
            try {
                realm.executeTransaction {
                    if (deleteDeps) {
                        findDb(dto.getDbClass(), dto.id)?.delete(realm)
                    }
                    realm.copyToRealmOrUpdate(dbEntity)
                    success = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "save: Exception: ${e.message}")
            }
        } else {
            Log.e(TAG, "save: Entity is not ready to be saved")
            throw Exception("Db entity can not be created")
        }
        return success
    }

    companion object {
        val TAG = DataManager::class.java.simpleName
    }

    // endregion private methods

}