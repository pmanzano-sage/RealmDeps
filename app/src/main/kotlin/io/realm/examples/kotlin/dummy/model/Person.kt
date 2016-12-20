package io.realm.examples.kotlin.dummy.model

import io.realm.examples.kotlin.dummy.db.DbPerson
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId

/**
 * @author Pablo Manzano
 * @since 01/12/16
 *
 * var wishList: List<Toy> = arrayListOf()
 */

data class Person(override val id: String = generateId(),
                  override var sync: SyncStatus = SyncStatus.getDefault(),
                  var name: String = "",
                  var age: Int = 0,
                  val toy: Toy? = null,
                  var cats: List<Cat>? = null,
                  var wishList: List<Toy>? = null
) : Dto {

    constructor(name: String, age: Int, toy: Toy?, cats: List<Cat>, wishList: List<Toy>?) : this(
            // id = generateId(),
            id = name,
            sync = SyncStatus.getDefault(),
            name = name,
            age = age,
            toy = toy,
            cats = cats,
            wishList = wishList
    )

    override fun getDbClass(): Class<out DbPerson> {
        return DbPerson::class.java
    }

    override fun toDisplayString(): String {
        return name
    }

    override fun toDbModel(): DbPerson {
        return convertToDb(Person::class.java, getDbClass())
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw IllegalArgumentException("Person name can not be blank!\nOffending instance:\n${this}")
        }
        toy?.checkValid()
        cats?.map(Cat::checkValid)
        wishList?.map(Toy::checkValid)
        return this
    }

    fun log() {
        println("Person {")
        for (prop in Person::class.java.declaredFields) {
            // ${prop.genericType} ${prop.type}
            println("\t${prop.name} = ${prop.get(this)}")
        }
        println("}")
    }

}