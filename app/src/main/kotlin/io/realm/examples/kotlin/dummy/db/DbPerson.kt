/*
 * Copyright 2015 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.examples.kotlin.dummy.db

import io.realm.RealmList
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.dummy.model.Person

// Your model has to extend RealmObject or be annotated with @RealmClass. Furthermore, the class and all of the
// properties must be annotated with open (Kotlin classes and methods are final by default).

@RealmClass
open class DbPerson(
        // You can put properties in the constructor as long as all of them are initialized with
        // default values. This ensures that an empty constructor is generated.
        // All properties are by default persisted.
        // Properties can be annotated with PrimaryKey or Index.
        // If you use non-nullable types, properties must be initialized with non-null values.
        @PrimaryKey @Required override var id: String = generateId(),

        override var sync: Int = SyncStatus.getDefault().ordinal,

        open var name: String = "",

        open var age: Int = 0,

        // Other objects in a one-to-one relation must also subclass RealmObject
        @CascadeOnDelete
        open var toy: DbToy? = null,

        // One-to-many relations is simply a RealmList of the objects which also subclass RealmObject
        @CascadeOnDelete
        open var cats: RealmList<DbCat> = RealmList(),

        open var wishList: RealmList<DbToy> = RealmList(),

        // You can instruct Realm to ignore a field and not persist it.
        @Ignore open var tempReference: Int = 0

) : RealmDbModel {
    // The Kotlin compiler generates standard getters and setters.
    // Realm will overload them and code inside them is ignored.
    // So if you prefer you can also just have empty abstract methods.

    constructor(name: String, age: Int, toy: DbToy?, cats: RealmList<DbCat>, wishList: RealmList<DbToy>) : this(
            id = generateId(),
            sync = SyncStatus.getDefault().ordinal,
            name = name,
            age = age,
            toy = toy,
            cats = cats,
            wishList = wishList)

    override fun getDtoClass(): Class<out Person> {
        return Person::class.java
    }

    override fun checkValid(): DbModel {
        if (name.isBlank()) {
            throw IllegalArgumentException("DbPerson name can not be blank!\nOffending instance:\n${this}")
        }
        toy!!.checkValid()
        cats.map(DbCat::checkValid)
        return this
    }

    override fun toString(): String {
        return "DbPerson( id=$id, name=$name, price=$age, toy=$toy, cats=${cats.joinToString(transform = DbCat::toString, prefix = "[", postfix = "]")}, wishList=, cats=${wishList.joinToString(transform = DbToy::toString, prefix = "[", postfix = "]")})"
    }

    override fun toDto(): Person {
        return convertToDto(DbPerson::class.java, getDtoClass())
    }

    fun log() {
        println("DbPerson {")
        for (prop in DbPerson::class.java.declaredFields) {
            println("\t${prop.name} = ${prop.get(this)} ")
        }
        println("}")
    }

}
