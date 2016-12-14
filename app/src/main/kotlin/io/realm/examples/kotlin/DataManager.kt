package io.realm.examples.kotlin

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.examples.kotlin.dto.definition.StringUtils
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.Dto

/**
 * @author Pablo Manzano
 * @since 13/12/16
 */
interface PersistenceProvider {
    fun deleteAll()
    fun save(dto: Dto): Boolean
    fun create(dto: Dto): Boolean
    fun update(dto: Dto): Boolean
    fun delete(dto: Dto): Boolean
    fun find(clazz: Class<out Dto>, id: String): Dto?
    fun getAll(clazz: Class<out Dto>): List<Dto>
}

/**
 * This is a Realm implementation of the PersistenceProvider.
 */
class DataManager(realm: Realm) : PersistenceProvider {
    private val realm: Realm

    init {
        this.realm = realm
    }

    override fun getAll(clazz: Class<out Dto>): List<Dto> {
        // TODO There should be a better way to figure out the dbClass (instead of creating an instance)
        val dto = clazz.newInstance()
        return findAllDb(dto.getDbClass()).map(Db::toDto)
    }

    override fun deleteAll() {
        realm.executeTransaction {
            realm.deleteAll()
        }
    }

    override fun find(clazz: Class<out Dto>, id: String): Dto? {
        // TODO There should be a better way to figure out the dbClass (instead of creating an instance)
        val dto = clazz.newInstance()
        val dbEntity = findDb(dto.getDbClass(), id)
        if (dbEntity != null) {
            return realm.copyFromRealm(dbEntity).toDto()
        }
        return null
    }

    override fun update(dto: Dto): Boolean {
        var success = false
        val dbEntity = findDb(dto.getDbClass(), dto.id)
        if (dbEntity != null) {
            success = save(dto)
        } else {
            throw Exception("Not found ${dto.javaClass.name} with id=${dto.id}")
        }
        return success
    }

    override fun create(dto: Dto): Boolean {
        var success = false
        val dbEntity = findDb(dto.getDbClass(), dto.id)
        if (dbEntity == null) {
            success = save(dto)
        } else {
            throw Exception("Already exists ${dto.javaClass.name} with id=${dto.id}")
        }
        return success
    }

    private fun findAllDb(clazz: Class<out Db>): List<Db> {
        return realm.where(clazz).findAll()
    }


    private fun findDb(clazz: Class<out Db>, id: String): Db? {
        if (StringUtils.isEmpty(id)) {
            return null
        }
        return realm.where(clazz).equalTo("id", id).findFirst()
    }

    override fun delete(dto: Dto): Boolean {
        var success = false;
        if (!StringUtils.isEmpty(dto.id)) {
            realm.executeTransaction {
                val dbEntity = findDb(dto.getDbClass(), dto.id)
                Log.d("delete", "dbEntity=$dbEntity")
                RealmObject.deleteFromRealm(dbEntity)
                success = true
            }
        }
        return success
    }


    override fun save(dto: Dto): Boolean {
        var success = false
        val dbEntity = dto.checkValid().toDb()
        if (dbEntity.readyToSave()) {
            try {
                realm.executeTransaction {
                    realm.copyToRealmOrUpdate(dbEntity)
                    success = true
                }
            } catch (e: Exception) {
                Log.e("save", "Exception: ${e.message}")
            }
        } else {
            Log.e("save", "Entity is not ready to be saved")
            throw Exception("Db entity can not be created")
        }
        return success
    }

}