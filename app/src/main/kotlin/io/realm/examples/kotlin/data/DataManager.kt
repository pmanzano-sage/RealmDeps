package io.realm.examples.kotlin.data

/**
 * Persistence Layer Interface
 *
 * @author Pablo Manzano
 */
interface DataManager {
    fun deleteAll()
    fun getAll(clazz: Class<out Dto>): List<Dto>
    fun find(clazz: Class<out Dto>, id: String): Dto?

    /**
     * Saves a dto.
     *
     * If validate = true, the entity will be validated before trying to persist it.
     * If validate = false, the entity will NOT be validated and every invalid dependency
     * annotated with @SupportsIdOnly will be replaced with one taken from the db using its id.
     */
    fun save(dto: Dto, validate: Boolean = true): Boolean

    fun create(dto: Dto, validate: Boolean = true): Boolean
    fun update(dto: Dto, validate: Boolean = true): Boolean
    fun delete(dto: Dto): Boolean
    fun count(clazz: Class<out Dto>): Long
    fun deleteNonCascade(dto: Dto): Boolean
}

/**
 * Exception to throw when something can't be found in the database
 * and the entity is annotated with @SupportsIdOnly
 */
class NotFoundException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)


/**
 * Exception to throw when an entity has an invalid dependency.
 */
class InvalidDependencyException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)


/**
 * Exception to throw when an entity has an invalid field that can not be fixed
 * and the entity is NOT annotated with @SupportsIdOnly
 */
class InvalidFieldException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)

