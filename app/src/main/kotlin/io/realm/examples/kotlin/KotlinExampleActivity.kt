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
import io.realm.Sort
import io.realm.examples.kotlin.dto.DtoCat
import io.realm.examples.kotlin.model.Cat
import io.realm.examples.kotlin.model.Dog
import io.realm.examples.kotlin.model.Person
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis


/**
 *
 * To Test
 *
 * - Something has to be available to iterate over the properties without being very very expensive.
 * - Test something to get an immutable version of something stored in Realm. (copyFromRealm)
 * - Check if there is an rx operator to concat the execution of various lambdas. (for transactions)
 *
 * Verified
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

    // Basic person to work with
    val myDog = Dog("Butcher")
    val myCats = RealmList<Cat>(Cat("Michifus"), Cat("Pepa"), Cat("Flora"))
    val me = Person(1, "Pablo", 25, myDog, myCats)
    val numPersons = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realm_basic_example)
        rootLayout = findViewById(R.id.container) as LinearLayout
        rootLayout.removeAllViews()


        // These operations are small enough that
        // we can generally safely run them on the UI thread.

        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()

        // Delete all persons
        // Using executeTransaction with a lambda reduces code size and makes it impossible
        // to forget to commit the transaction.
        realm.executeTransaction {
            realm.delete(Person::class.java)
            realm.delete(Dog::class.java)
            realm.delete(Cat::class.java)
        }

        // Iteration over declared fields
        val dto = DtoCat("dto", 10)
        dto.log()
        me.log()
        val cat = dto.toCat()
        Log.w(TAG, "GATO!!: $cat")


        basicCRUD(realm)
        deleteItemFromList(realm)
        deleteListOwner(realm)
        twoItemsPointingToTheSameDep(realm)

        // Transactions' speed
        testMultipleTransactions(realm)
        testSingleTransaction(realm)

        // Getting something out of Realm
        testCopyFromRealm(realm)

        basicQuery(realm)
        basicLinkQuery(realm)


        // More complex operations can be executed on another thread, for example using
        // Anko's async extension method.
        async() {
            var info: String
            info = complexReadWrite()
            info += complexQuery()

            uiThread {
                showStatus(info)
            }
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


    /**
     * - New dog "Cockie"
     * - Two persons having the SAME dog
     */
    private fun twoItemsPointingToTheSameDep(realm: Realm) {
        showStatus("twoItemsPointingToTheSameDep...")
        val dogName = "Cockie"
        realm.executeTransaction {
            val dog = Dog(dogName)
            val p1 = Person(789, "Pedro", 20, dog)
            realm.copyToRealmOrUpdate(p1)

            val cockie = realm.where(Dog::class.java).equalTo("name", dogName).findFirst()
            val p2 = Person(790, "Jose", 20, cockie)
            realm.copyToRealmOrUpdate(p2)
        }
        val numCockies = realm.where(Dog::class.java).equalTo("name", dogName).findAll().count()
        showStatus("#Cockies=$numCockies")
    }

    private fun deleteListOwner(realm: Realm) {
        showStatus("deleteListOwner...")
        realm.executeTransaction {
            val person = realm.where(Person::class.java).equalTo("id", 567).findFirst()
            person.deleteFromRealm()
        }
        // If the associated cats are deleted with the person, this count should be 2, otherwise it
        // will be 4.
        val numCats = realm.where(Cat::class.java).findAll().count()
        showStatus("#cats=$numCats")
    }


    private fun deleteItemFromList(realm: Realm) {
        showStatus("deleteItemFromList...")
        var numCats = 0
        realm.executeTransaction {
            val cats = realm.where(Cat::class.java).equalTo("name", "michifus").findAll()
            numCats = cats.count()
            cats.deleteAllFromRealm()
        }
        showStatus("$numCats cats deleted")
    }

    private fun testMultipleTransactions(realm: Realm) {
        val offset = 1000L
        val millis = measureTimeMillis {
            for (i in 1..numPersons) {
                me.id = offset + i.toLong()
                realm.executeTransaction {
                    realm.copyToRealmOrUpdate(me)
                }
            }
        }
        showStatus("MULTIPLE: $numPersons persons created in $millis milliseconds")
    }

    private fun testSingleTransaction(realm: Realm) {
        val offset = 2000L
        val millis = measureTimeMillis {
            realm.executeTransaction {
                for (i in 1..numPersons) {
                    me.id = offset + i.toLong()
                    realm.copyToRealmOrUpdate(me)
                }
            }
        }
        showStatus("SINGLE: $numPersons persons created in $millis milliseconds")
    }


    private fun testCopyFromRealm(realm: Realm) {
        realm.executeTransaction {
            val someone = realm.where(Person::class.java).equalTo("id", 1100).findFirst()
            if (someone != null) {

                // Everything that is copied from Realm out, is not managed (dependencies included)
                val myself = realm.copyFromRealm(someone)
                showStatus("testCopyFromRealm: $myself")
                // showStatus("testCopyFromRealm: ${myself.shortName()}")
                val man1 = myself.isManaged
                val man2 = myself.cats.isManaged
                val man3 = myself.cats.get(0).isManaged
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
            val dog = Dog("Butcher")
            val cats = arrayOf(Cat("michifus"), Cat("Pepa"), Cat("Flora"))
            val rcats = RealmList<Cat>()
            rcats.addAll(cats)
            val p1 = Person(1234, "Juan", 15, dog, rcats)

            // Another option is create the person with an empty list associated, and then
            // get the list, clear it, and add all the items.
            // p1.cats.clear()
            // p1.cats.addAll(cats)

            Log.d(TAG, "PERSON= ${p1.hashCode()}")

            realm.copyToRealmOrUpdate(p1)

            val p2 = Person(567, "Juan", 15, dog, rcats)
            realm.copyToRealmOrUpdate(p2)

        }

        // Find the first person (no query conditions) and read a field
        val person = realm.where(Person::class.java).findFirst()
        showStatus(person.name + ": " + person.age)

        // Update person in a transaction
        realm.executeTransaction {
            person.name = "Senior Person"
            person.age = 99
            showStatus(person.name + " got older: " + person.age)
        }
    }

    private fun basicQuery(realm: Realm) {
        showStatus("\nPerforming basic Query operation...")
        showStatus("Number of persons: ${realm.where(Person::class.java).count()}")

        val results = realm.where(Person::class.java).equalTo("age", 99).findAll()

        showStatus("Size of result set: " + results.size)
    }

    private fun basicLinkQuery(realm: Realm) {
        showStatus("\nPerforming basic Link Query operation...")
        showStatus("Number of persons: ${realm.where(Person::class.java).count()}")

        val results = realm.where(Person::class.java).equalTo("cats.name", "Tiger").findAll()

        showStatus("Size of result set: ${results.size}")
    }

    private fun complexReadWrite(): String {
        var status = "\nPerforming complex Read/Write operation..."

        // Open the default realm. All threads must use its own reference to the realm.
        // Those can not be transferred across threads.
        val realm = Realm.getDefaultInstance()

        // Add ten persons in one transaction
        realm.executeTransaction {
            val fido = realm.createObject(Dog::class.java)
            fido.name = "fido"
            for (i in 0..9) {
                val person = realm.createObject(Person::class.java)
                person.id = i.toLong()
                person.name = "Person no. $i"
                person.age = i
                person.dog = fido

                // The field tempReference is annotated with @Ignore.
                // This means setTempReference sets the Person tempReference
                // field directly. The tempReference is NOT saved as part of
                // the RealmObject:
                person.tempReference = 42

                for (j in 0..i - 1) {
                    val cat = realm.createObject(Cat::class.java)
                    cat.name = "Cat_$j"
                    person.cats.add(cat)
                }
            }
        }

        // Implicit read transactions allow you to access your objects
        status += "\nNumber of persons: ${realm.where(Person::class.java).count()}"

        // Iterate over all objects
        for (person in realm.where(Person::class.java).findAll()) {
            val dogName: String = person?.dog?.name ?: "None"

            status += "\n${person.name}: ${person.age} : $dogName : ${person.cats.size}"

            // The field tempReference is annotated with @Ignore
            // Though we initially set its value to 42, it has
            // not been saved as part of the Person RealmObject:
            check(person.tempReference == 0)
        }

        // Sorting
        val sortedPersons = realm.where(Person::class.java).findAllSorted("age", Sort.DESCENDING)
        check(realm.where(Person::class.java).findAll().last().name == sortedPersons.first().name)
        status += "\nSorting ${sortedPersons.last().name} == ${realm.where(Person::class.java).findAll().first().name}"

        realm.close()
        return status
    }

    private fun complexQuery(): String {
        var status = "\n\nPerforming complex Query operation..."

        // Realm implements the Closable interface, therefore we can make use of Kotlin's built-in
        // extension method 'use' (pun intended).
        Realm.getDefaultInstance().use {
            // 'it' is the implicit lambda parameter of type Realm
            status += "\nNumber of persons: ${it.where(Person::class.java).count()}"

            // Find all persons where age between 7 and 9 and name begins with "Person".
            val results = it
                    .where(Person::class.java)
                    .between("age", 7, 9)       // Notice implicit "and" operation
                    .beginsWith("name", "Person")
                    .findAll()

            status += "\nSize of result set: ${results.size}"

        }

        return status
    }


}
