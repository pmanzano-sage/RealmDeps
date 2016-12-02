package io.realm.examples.kotlin.db

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

fun DbPerson.shortName(): String {
    return "$name ($age) #dbCats=${dbCats.count()}"
}


