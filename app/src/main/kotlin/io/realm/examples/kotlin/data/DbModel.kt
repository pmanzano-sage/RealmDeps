package io.realm.examples.kotlin.data

import io.realm.RealmModel

/**
 * @author Pablo Manzano
 * @since 20/12/16
 */

interface DbModel : RealmModel {
    var id: String
    var sync: Int

    fun toDto(): Dto
    fun readyToSave(): Boolean
    fun getDtoClass(): Class<out Dto>
}
