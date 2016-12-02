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

import io.realm.RealmObject
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.convertToDto
import io.realm.examples.kotlin.model.Dog

open class DbDog(open var name: String = "", open var age: Int = 0) : RealmObject(), Db {
    override fun toString(): String {
        return "DbDog(name=$name, age=$age)"
    }

    override fun toDto(): Dog {
        return convertToDto(DbDog::class.java, Dog::class.java)
    }

}
