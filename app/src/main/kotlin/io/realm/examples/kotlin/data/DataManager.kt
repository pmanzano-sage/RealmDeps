package io.realm.examples.kotlin.data

/**
 * @author Pablo Manzano
 * @since 13/12/16
 */
interface DataManager {
    fun deleteAll()
    fun getAll(clazz: Class<out Dto>): List<Dto>
    fun find(clazz: Class<out Dto>, id: String): Dto?
    fun save(dto: Dto): Boolean
    fun create(dto: Dto): Boolean
    fun update(dto: Dto): Boolean
    fun delete(dto: Dto): Boolean
    fun count(clazz: Class<out Dto>): Long
    fun deleteNonCascade(dto: Dto): Boolean
}