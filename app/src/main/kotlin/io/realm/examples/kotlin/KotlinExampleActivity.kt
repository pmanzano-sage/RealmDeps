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

package io.realm.examples.kotlin

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import io.realm.Realm
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.RealmDataManager
import kotlin.properties.Delegates


/**
 *
 * To Do
 * - Implementar la solucion para los partially filled-in entities.
 * - Cuando estamos creando una entidad local, podríamos mirar los @CascadeOnDelete y usar el id
 *   del padre para todas estas dependencias...
 * - Hacer que el DTO de factura permita agregar y quitar pagos/lineas.
 * - Pensar un poco el tema de las 2 bases de datos.
 * - Otra cosa que estaría bien tambien es recibir una lista de campos en los updates.
 * - Add Date & Boolean support.
 *
 * Done
 * - Implement a "CascadeOnDelete" annotation
 * - Add Instrumentation tests & Junit.
 * - Come up with a solution for cascade deletion.
 * - Code some convenience factories for the Dtos.
 * - Can not ignore attributes from Realm cause retention type is CLASS and not RUNTIME.
 * - You can create a model that doesn't have an id or primary key.
 * - When you delete items that belong to a list pointed by some other object, what happens?
 *   The list is directly updated.
 * - When you delete an object that has a RealmList, the list is not automatically deleted!
 * - Compare the time of executing various operations inside and outside transactions.
 *   It's slower to do it with multiple transactions... May be the memory consuption is greater.
 */

class KotlinExampleActivity : Activity() {

    companion object {
        val TAG: String = KotlinExampleActivity::class.java.simpleName
    }

    private var rootLayout: LinearLayout by Delegates.notNull()
    private var dataManager: DataManager by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realm_basic_example)
        rootLayout = findViewById(R.id.container) as LinearLayout
        rootLayout.removeAllViews()
        dataManager = RealmDataManager(Realm.getDefaultInstance())
    }

}
