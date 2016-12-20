package io.realm.examples.kotlin

import android.test.AndroidTestCase
import android.util.Log
import io.realm.*
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.dummy.db.DbCat
import io.realm.examples.kotlin.dummy.db.DbPerson
import io.realm.examples.kotlin.dummy.db.DbToy
import io.realm.examples.kotlin.dummy.model.Cat
import io.realm.examples.kotlin.dummy.model.Person
import io.realm.examples.kotlin.dummy.model.Toy
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

/**
 * @author Pablo Manzano
 * *
 * @since 20/12/16
 */
class RealmDataManagerTest : AndroidTestCase() {

    private lateinit var dataManager: DataManager
    private var realm: Realm by Delegates.notNull()

    // Basic person to work with
    val ball = DbToy("ball", 10)
    val teddy = DbToy("teddy", 2)
    val myCats = RealmList<DbCat>(DbCat("Michifus", 1, null), DbCat("Pepa", 2, ball), DbCat("Flora", 3, null))
    val dbPerson = DbPerson("111", SyncStatus.SYNC_SUCCESS.ordinal, "Pablo", 35, teddy, myCats)
    val numPersons = 100

    // Basic person model
    val yoyo = Toy("yo-yo", 5.0)
    val someCats = arrayListOf(Cat("Moe", 4, null), Cat("Shemp", 5, yoyo), Cat("Larry", 6, null))
    val jake = Person("1", SyncStatus.SYNC_ERROR, "Jake", 34, yoyo, someCats)

    override fun setUp() {

        // Initialize Realm & DataManager
        Realm.init(getContext())
        val realmConfig = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(realmConfig)
        dataManager = RealmDataManager(Realm.getDefaultInstance())
        realm = Realm.getDefaultInstance()

        // Note that we are deleting all the entities before launching any test
        dataManager.deleteAll()
    }

    override fun tearDown() {
        super.tearDown()
    }

    fun getAll() {

    }

    fun deleteAll() {

    }

    fun count() {

    }

    fun find() {

    }

    fun update() {

    }

    fun create() {

    }

    fun delete() {

    }

    fun deleteNonCascade() {

    }

    fun save() {

    }

    // Some other tests
    fun createInvalidPerson() {
        val personName = "J"
        val dog = DbToy("")
        val cats = RealmList<DbCat>(DbCat("Michifus", 1, null), DbCat("", 2, ball), DbCat("Flora", 3, null))
        val p1 = DbPerson("666", SyncStatus.getDefault().ordinal, personName, 20, dog, cats)
        if (p1.readyToSave()) {
            realm.executeTransaction {
                realm.copyToRealmOrUpdate(p1)
            }
        } else {
            showStatus("sorry invalid person")
        }
        val numPersons = realm.where(DbPerson::class.java).equalTo("name", personName).findAll().count()
    }

    /**
     * - New toy "Cockie"
     * - Two persons having the SAME toy
     */
    fun twoItemsPointingToTheSameDep() {
        showStatus("twoItemsPointingToTheSameDep...")
        val dogName = "Cockie"
        realm.executeTransaction {
            val dog = DbToy(dogName)
            val p1 = DbPerson("789", SyncStatus.getDefault().ordinal, "Pedro", 20, dog)
            realm.copyToRealmOrUpdate(p1)

            val cockie = realm.where(DbToy::class.java).equalTo("name", dogName).findFirst()
            val p2 = DbPerson("790", SyncStatus.getDefault().ordinal, "Jose", 20, cockie)
            realm.copyToRealmOrUpdate(p2)
        }
        val numCockies = realm.where(DbToy::class.java).equalTo("name", dogName).findAll().count()
        showStatus("#Cockies=$numCockies")
    }

    fun deleteListOwner() {
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


    fun deleteItemFromList() {
        showStatus("deleteItemFromList...")
        var numCats = 0
        realm.executeTransaction {
            val cats = realm.where(DbCat::class.java).equalTo("name", "michifus").findAll()
            numCats = cats.count()
            cats.deleteAllFromRealm()
        }
        showStatus("$numCats cats deleted")
    }

    fun testMultipleTransactions(realm: Realm) {
        val millis = measureTimeMillis {
            for (i in 1..numPersons) {
                realm.executeTransaction {
                    realm.copyToRealmOrUpdate(dbPerson)
                }
            }
        }
        showStatus("MULTIPLE: $numPersons persons created in $millis milliseconds")
    }

    fun testSingleTransaction(realm: Realm) {
        val millis = measureTimeMillis {
            realm.executeTransaction {
                for (i in 1..numPersons) {
                    realm.copyToRealmOrUpdate(dbPerson)
                }
            }
        }
        showStatus("SINGLE: $numPersons persons created in $millis milliseconds")
    }


    fun testCopyFromRealm() {
        realm.executeTransaction {
            val someone = realm.where(DbPerson::class.java).equalTo("id", "1100").findFirst()
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

    fun basicCRUD() {
        showStatus("Perform basic Create/Read/Update/Delete (CRUD) operations...")

        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.executeTransaction {
            val toy1 = DbToy("toy1", 1.0)
            val toy2 = DbToy("toy2", 2.0)
            val toy3 = DbToy("toy3", SyncStatus.NEEDS_SYNC_CREATE.ordinal, "toy3", 3.0)
            val cats = arrayOf(DbCat("michifus", 88, toy3), DbCat("Pepa", 77, toy2), DbCat("Flora", 66, toy1))
            val rcats = RealmList<DbCat>()
            rcats.addAll(cats)
            val p1 = DbPerson("567", SyncStatus.SYNC_SUCCESS.ordinal, "Juan", 15, toy1, rcats)

            // Another option is create the person with an empty list associated, and then
            // get the list, clear it, and add all the items.
            // p1.cats.clear()
            // p1.cats.addAll(cats)

            Log.d(KotlinExampleActivity.TAG, "PERSON= ${p1.hashCode()}")
            realm.copyToRealmOrUpdate(p1)

            try {
                val p2 = DbPerson("567", SyncStatus.SYNC_SUCCESS.ordinal, "Pedro", 25, toy2, RealmList<DbCat>())
                realm.copyToRealm(p2)
                // realm.copyToRealmOrUpdate(p2)
            } catch (e: RealmPrimaryKeyConstraintException) {
                showStatus("${e.message}")
            }

        }

        // Find the first person (no query conditions) and read a field
        val person = realm.where(DbPerson::class.java).equalTo("price", 25).findFirst()
        if (person != null) {
            showStatus(person.name + " found.  price=" + person.age)

            // Update person in a transaction
            realm.executeTransaction {
                person.name = "Peter"
                person.age = 99
                showStatus(person.name + " got older: " + person.age)
            }

        }

        // Look for a person that has a certain toy
        realm.executeTransaction {
            val myperson = realm.where(DbPerson::class.java).equalTo("toy.name", "Lacie").findFirst()
            if (myperson != null) {
                myperson.age = 55
                showStatus(myperson.name + " is now " + myperson.age)
            } else {
                showStatus("No person found with a toy called Lacie")
            }
        }

    }

    private fun basicQuery() {
        showStatus("\nPerforming basic Query operation...")
        showStatus("Number of persons: ${realm.where(DbPerson::class.java).count()}")

        val results = realm.where(DbPerson::class.java).equalTo("price", 99).findAll()

        showStatus("Size of result set: " + results.size)
    }

    private fun basicLinkQuery() {
        showStatus("\nPerforming basic Link Query operation...")
        showStatus("Number of persons: ${realm.where(DbPerson::class.java).count()}")

        val results = realm.where(DbPerson::class.java).equalTo("cats.name", "Tiger").findAll()

        showStatus("Size of result set: ${results.size}")
    }

    private fun complexReadWrite(): String {
        var status = "\nPerforming complex Read/Write operation..."

        // Add ten persons in one transaction
        realm.executeTransaction {
            val fido = realm.createObject(DbToy::class.java)
            fido.name = "fido"
            for (i in 0..9) {
                val person = realm.createObject(DbPerson::class.java)
                // person.id = i.toString()
                person.name = "DbPerson no. $i"
                person.age = i
                person.toy = fido

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
            val dogName: String = person?.toy?.name ?: "None"

            status += "\n${person.name}: ${person.age} : $dogName : ${person.cats.size}"

            // The field tempReference is annotated with @Ignore
            // Though we initially set its value to 42, it has
            // not been saved as part of the DbPerson RealmObject:
            check(person.tempReference == 0)
        }

        // Sorting
        val sortedPersons = realm.where(DbPerson::class.java).findAllSorted("price", Sort.DESCENDING)
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

            // Find all persons where price between 7 and 9 and name begins with "DbPerson".
            val results = it
                    .where(DbPerson::class.java)
                    .between("price", 7, 9)       // Notice implicit "and" operation
                    .beginsWith("name", "DbPerson")
                    .findAll()

            status += "\nSize of result set: ${results.size}"

        }

        return status
    }


    private fun showStatus(txt: String) {
        Log.d("Test", txt)
    }

}