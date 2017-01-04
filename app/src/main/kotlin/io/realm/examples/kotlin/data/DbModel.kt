package io.realm.examples.kotlin.data

/**
 * @author Pablo Manzano
 * @since 20/12/16
 */

interface DbModel {
    var id: String
    var sync: Int

    fun toDto(): Dto
    fun checkValid(): DbModel
    fun getDtoClass(): Class<out Dto>
}
