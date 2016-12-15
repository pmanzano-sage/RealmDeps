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

package io.realm.examples.kotlin.db

import io.realm.Realm
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import io.realm.examples.kotlin.model.Cat

@RealmClass
open class DbCat(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var name: String = "",
        open var age: Int = 0,
        // When a dependency is marked as @Exclusive, its id must be generated based on the parent id.
        @Exclusive var dog: DbDog? = null
) : Db {

    // If client code does not provide an id, a random one is generated.
    constructor(name: String, age: Int, dog: DbDog?) : this(
            generateId(),
            sync = SyncStatus.getDefault().ordinal,
            name = name,
            age = age,
            dog = dog
    )

    override fun getDtoClass(): Class<out Cat> {
        return Cat::class.java
    }

    override fun readyToSave(): Boolean {
        return name.isNotEmpty() && (dog == null || dog!!.readyToSave())
    }

    override fun toString(): String {
        return "DbCat(name=$name, age=$age, dog=$dog)"
    }

    override fun toDto(): Cat {
        return convertToDto(DbCat::class.java, getDtoClass())
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(DbCat::class.java, realm)
    }

    fun log() {
        println("DbCat {")
        for (prop in DbCat::class.java.declaredFields) {
            println("\t${prop.name} = ${prop.get(this)}")
        }
        println("}")
    }

}