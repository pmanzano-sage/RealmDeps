package io.realm.examples.kotlin.model

/**
 * @author Pablo Manzano
 * @since 01/12/16
 */

fun Person.shortName(): String {
    return "$name ($age) #cats=${cats.count()}"
}


