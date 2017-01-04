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

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.RealmDbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.dummy.model.Toy

@RealmClass
open class DbToy(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var name: String = "",
        open var price: Double = 0.0
) : RealmDbModel {

    // If client code does not provide an id, a random one is generated.
    constructor(name: String, price: Double) : this(
            generateId(),
            sync = SyncStatus.getDefault().ordinal,
            name = name,
            price = price
    )

    override fun getDtoClass(): Class<out Toy> {
        return Toy::class.java
    }

    override fun checkValid(): DbModel {
        if (name.isBlank()) {
            throw IllegalArgumentException("DbToy name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toString(): String {
        return "DbToy(name=$name, price=$price)"
    }

    override fun toDto(): Toy {
        return convertToDto(DbToy::class.java, getDtoClass())
    }
}
