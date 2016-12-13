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
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.Sort
import io.realm.examples.kotlin.db.DbCat
import io.realm.examples.kotlin.db.DbDog
import io.realm.examples.kotlin.db.DbPerson
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.model.Cat
import io.realm.examples.kotlin.model.Dog
import io.realm.examples.kotlin.model.Person
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis


/**
 *
 * To Do
 *
 *
 *
 * - Add Date & Boolean support.
 * - Use copyFromRealm to ensure that we work with unmanaged entities.
 * - Pensar bien el tema de las dependencias que se deben borrar en cascada y cuales no.
 *   Esto puede estar relacionado con el tema de buscar primero aquellos objetos que están incompletos.
 *
 * Done
 *
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
    private var realm: Realm by Delegates.notNull()

    private var dataManager: DataManager by Delegates.notNull()

    // Basic person to work with
    val doggy = DbDog("doggy", 66)
    val myDog = DbDog("Butcher", 9)
    val myCats = RealmList<DbCat>(DbCat("Michifus", 1, null), DbCat("Pepa", 2, doggy), DbCat("Flora", 3, null))
    val dbPerson = DbPerson("111", SyncStatus.SYNC_SUCCESS.ordinal, "Pablo", 35, myDog, myCats)
    val numPersons = 100

    // Basic person model
    val aDog = Dog("Spike", 5)
    val someCats = arrayListOf(Cat("Moe", 4, null), Cat("Shemp", 5, aDog), Cat("Larry", 6, null))
    val jake = Person("1", SyncStatus.SYNC_ERROR, "Jake", 34, aDog, someCats)

    // Some entities from One


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realm_basic_example)
        rootLayout = findViewById(R.id.container) as LinearLayout
        rootLayout.removeAllViews()


        // These operations are small enough that
        // we can generally safely run them on the UI thread.

        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()

        dataManager = DataManager(realm)

        // Delete all persons
        // Using executeTransaction with a lambda reduces code size and makes it impossible
        // to forget to commit the transaction.
        realm.executeTransaction {
            realm.delete(DbPerson::class.java)
            realm.delete(DbDog::class.java)
            realm.delete(DbCat::class.java)
        }

        // Automapping tests
//        Log.w(TAG, "-------------- AUTOMAPPING DTO -> DB --------------")
//        Log.w(TAG, "$jake")
//        val dbJake = jake.toDb()
//        Log.w(TAG, "$dbJake")
//        Log.w(TAG, "-------------- AUTOMAPPING DTO -> DB --------------")

        dataManagerTest()

        // Automapping tests
//        Log.w(TAG, "-------------- AUTOMAPPING DB -> DTO --------------")
//        Log.w(TAG, "$dbPerson")
//        val dtoPerson = dbPerson.toDto()
//        Log.w(TAG, "$dtoPerson")
//        Log.w(TAG, "-------------- AUTOMAPPING DB -> DTO --------------")

        // attemptToCreateInvalidPerson(realm)

        // basicCRUD(realm)
//        deleteItemFromList(realm)
//        deleteListOwner(realm)
//        twoItemsPointingToTheSameDep(realm)
//
//        // Transactions' speed
//        testMultipleTransactions(realm)
//        testSingleTransaction(realm)

        // Getting something out of Realm
//        testCopyFromRealm(realm)

//        basicQuery(realm)
//        basicLinkQuery(realm)


        // More complex operations can be executed on another thread, for example using
        // Anko's async extension method.
//        async() {
//            var info: String
//            info = complexReadWrite()
//            info += complexQuery()
//
//            uiThread {
//                showStatus(info)
//            }
//        }
    }

    private fun dataManagerTest() {
        try {
            var ok = false

//            ok = dataManager.update(jake)
//            showStatus("Jake updated: $ok")

            ok = dataManager.create(jake)
            showStatus("Jake created: $ok")

            ok = dataManager.delete(aDog)
            showStatus("Spike deleted: $ok")

            val jake2 = dataManager.find(Person::class.java, jake.id)
            showStatus("Jake2: $jake2")

        } catch (e: Exception) {
            showStatus("${e.message}")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // Remember to close Realm when done.
    }

    private fun showStatus(txt: String) {
        Log.i(TAG, txt)
        val tv = TextView(this)
        tv.text = txt
        rootLayout.addView(tv)
    }


    private fun attemptToCreateInvalidPerson(realm: Realm) {
        showStatus("Attempt to create invalid person...")
        val personName = "J"
        val dog = DbDog("")
        val cats = RealmList<DbCat>(DbCat("Michifus", 1, null), DbCat("", 2, doggy), DbCat("Flora", 3, null))
        val p1 = DbPerson("666", SyncStatus.getDefault().ordinal, personName, 20, dog, cats)

        if (p1.readyToSave()) {
            realm.executeTransaction {
                realm.copyToRealmOrUpdate(p1)
            }
        } else {
            showStatus("sorry invalid person")
        }
        val numPersons = realm.where(DbPerson::class.java).equalTo("name", personName).findAll().count()
        showStatus("#persons=$numPersons")

    }


    /**
     * - New dog "Cockie"
     * - Two persons having the SAME dog
     */
    private fun twoItemsPointingToTheSameDep(realm: Realm) {
        showStatus("twoItemsPointingToTheSameDep...")
        val dogName = "Cockie"
        realm.executeTransaction {
            val dog = DbDog(dogName)
            val p1 = DbPerson("789", SyncStatus.getDefault().ordinal, "Pedro", 20, dog)
            realm.copyToRealmOrUpdate(p1)

            val cockie = realm.where(DbDog::class.java).equalTo("name", dogName).findFirst()
            val p2 = DbPerson("790", SyncStatus.getDefault().ordinal, "Jose", 20, cockie)
            realm.copyToRealmOrUpdate(p2)
        }
        val numCockies = realm.where(DbDog::class.java).equalTo("name", dogName).findAll().count()
        showStatus("#Cockies=$numCockies")
    }

    private fun deleteListOwner(realm: Realm) {
        showStatus("deleteListOwner...")
        realm.executeTransaction {
            val person = realm.where(DbPerson::class.java).equalTo("id", 567).findFirst()
            RealmObject.deleteFromRealm(person)
        }
        // If the associated cats are deleted with the person, this count should be 2, otherwise it
        // will be 4.
        val numCats = realm.where(DbCat::class.java).findAll().count()
        showStatus("#cats=$numCats")
    }


    private fun deleteItemFromList(realm: Realm) {
        showStatus("deleteItemFromList...")
        var numCats = 0
        realm.executeTransaction {
            val cats = realm.where(DbCat::class.java).equalTo("name", "michifus").findAll()
            numCats = cats.count()
            cats.deleteAllFromRealm()
        }
        showStatus("$numCats cats deleted")
    }

    private fun testMultipleTransactions(realm: Realm) {
        val millis = measureTimeMillis {
            for (i in 1..numPersons) {
                realm.executeTransaction {
                    realm.copyToRealmOrUpdate(dbPerson)
                }
            }
        }
        showStatus("MULTIPLE: $numPersons persons created in $millis milliseconds")
    }

    private fun testSingleTransaction(realm: Realm) {
        val millis = measureTimeMillis {
            realm.executeTransaction {
                for (i in 1..numPersons) {
                    realm.copyToRealmOrUpdate(dbPerson)
                }
            }
        }
        showStatus("SINGLE: $numPersons persons created in $millis milliseconds")
    }


    private fun testCopyFromRealm(realm: Realm) {
        realm.executeTransaction {
            val someone = realm.where(DbPerson::class.java).equalTo("id", 1100).findFirst()
            if (someone != null) {

                // Everything that is copied from Realm out, is not managed (dependencies included)
                val myself = realm.copyFromRealm(someone)
                showStatus("testCopyFromRealm: $myself")
                // showStatus("testCopyFromRealm: ${myself.shortName()}")
                val man1 = RealmObject.isManaged(myself)
                val man2 = myself.cats.isManaged
                val man3 = RealmObject.isManaged(myself.cats.get(0))
                showStatus("testCopyFromRealm: $man1 $man2 $man3")
            } else {
                showStatus("testCopyFromRealm: could not find person 1100")
            }
        }
    }

    private fun basicCRUD(realm: Realm) {
        showStatus("Perform basic Create/Read/Update/Delete (CRUD) operations...")

        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.executeTransaction {
            val dog = DbDog("Butcher", 99)
            val lacie = DbDog("Lacie", 5)
            val dog33 = DbDog("33", SyncStatus.NEEDS_SYNC_CREATE.ordinal, "Dog33", 33)
            val cats = arrayOf(DbCat("michifus", 88, dog33), DbCat("Pepa", 77, lacie), DbCat("Flora", 66, dog))
            val rcats = RealmList<DbCat>()
            rcats.addAll(cats)
            val p1 = DbPerson("567", SyncStatus.SYNC_SUCCESS.ordinal, "Juan", 15, dog, rcats)

            // Another option is create the person with an empty list associated, and then
            // get the list, clear it, and add all the items.
            // p1.cats.clear()
            // p1.cats.addAll(cats)

            Log.d(TAG, "PERSON= ${p1.hashCode()}")
            realm.copyToRealmOrUpdate(p1)

            try {
                val p2 = DbPerson("567", SyncStatus.SYNC_SUCCESS.ordinal, "Pedro", 25, lacie, RealmList<DbCat>())
                realm.copyToRealm(p2)
                // realm.copyToRealmOrUpdate(p2)
            } catch (e: RealmPrimaryKeyConstraintException) {
                showStatus("${e.message}")
            }

        }

        // Find the first person (no query conditions) and read a field
        val person = realm.where(DbPerson::class.java).equalTo("age", 25).findFirst()
        if (person != null) {
            showStatus(person.name + " found.  age=" + person.age)

            // Update person in a transaction
            realm.executeTransaction {
                person.name = "Peter"
                person.age = 99
                showStatus(person.name + " got older: " + person.age)
            }

        }

        // Look for a person that has a certain dog
        realm.executeTransaction {
            val myperson = realm.where(DbPerson::class.java).equalTo("dog.name", "Lacie").findFirst()
            if (myperson != null) {
                myperson.age = 55
                showStatus(myperson.name + " is now " + myperson.age)
            } else {
                showStatus("No person found with a dog called Lacie")
            }
        }


        // Check if we can look for the bigest id in a table.
//        realm.executeTransaction {
//            val maxId: Number
//            maxId = realm.where(DbPerson::class.java).max("id")
//            val nextID: Long
//            if (maxId == null) {
//                nextID = 1
//            } else {
//                nextID = maxId.toLong() + 1
//            }
//            val another = realm.createObject(DbPerson::class.java, nextID)
//        }

    }

    private fun basicQuery(realm: Realm) {
        showStatus("\nPerforming basic Query operation...")
        showStatus("Number of persons: ${realm.where(DbPerson::class.java).count()}")

        val results = realm.where(DbPerson::class.java).equalTo("age", 99).findAll()

        showStatus("Size of result set: " + results.size)
    }

    private fun basicLinkQuery(realm: Realm) {
        showStatus("\nPerforming basic Link Query operation...")
        showStatus("Number of persons: ${realm.where(DbPerson::class.java).count()}")

        val results = realm.where(DbPerson::class.java).equalTo("cats.name", "Tiger").findAll()

        showStatus("Size of result set: ${results.size}")
    }

    private fun complexReadWrite(): String {
        var status = "\nPerforming complex Read/Write operation..."

        // Open the default realm. All threads must use its own reference to the realm.
        // Those can not be transferred across threads.
        val realm = Realm.getDefaultInstance()

        // Add ten persons in one transaction
        realm.executeTransaction {
            val fido = realm.createObject(DbDog::class.java)
            fido.name = "fido"
            for (i in 0..9) {
                val person = realm.createObject(DbPerson::class.java)
                // person.id = i.toString()
                person.name = "DbPerson no. $i"
                person.age = i
                person.dog = fido

                // The field tempReference is annotated with @Ignore.
                // This means setTempReference sets the DbPerson tempReference
                // field directly. The tempReference is NOT saved as part of
                // the RealmObject:
                person.tempReference = 42

                for (j in 0..i - 1) {
                    val cat = realm.createObject(DbCat::class.java)
                    cat.name = "Cat_$j"
                    person.cats.add(cat)
                }
            }
        }

        // Implicit read transactions allow you to access your objects
        status += "\nNumber of persons: ${realm.where(DbPerson::class.java).count()}"

        // Iterate over all objects
        for (person in realm.where(DbPerson::class.java).findAll()) {
            val dogName: String = person?.dog?.name ?: "None"

            status += "\n${person.name}: ${person.age} : $dogName : ${person.cats.size}"

            // The field tempReference is annotated with @Ignore
            // Though we initially set its value to 42, it has
            // not been saved as part of the DbPerson RealmObject:
            check(person.tempReference == 0)
        }

        // Sorting
        val sortedPersons = realm.where(DbPerson::class.java).findAllSorted("age", Sort.DESCENDING)
        check(realm.where(DbPerson::class.java).findAll().last().name == sortedPersons.first().name)
        status += "\nSorting ${sortedPersons.last().name} == ${realm.where(DbPerson::class.java).findAll().first().name}"

        realm.close()
        return status
    }

    private fun complexQuery(): String {
        var status = "\n\nPerforming complex Query operation..."

        // Realm implements the Closable interface, therefore we can make use of Kotlin's built-in
        // extension method 'use' (pun intended).
        Realm.getDefaultInstance().use {
            // 'it' is the implicit lambda parameter of type Realm
            status += "\nNumber of persons: ${it.where(DbPerson::class.java).count()}"

            // Find all persons where age between 7 and 9 and name begins with "DbPerson".
            val results = it
                    .where(DbPerson::class.java)
                    .between("age", 7, 9)       // Notice implicit "and" operation
                    .beginsWith("name", "DbPerson")
                    .findAll()

            status += "\nSize of result set: ${results.size}"

        }

        return status
    }


}
