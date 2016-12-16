package io.realm.examples.kotlin

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.model.Cat
import io.realm.examples.kotlin.model.Person
import io.realm.examples.kotlin.model.Toy
import junit.framework.Assert

/**
 * @author Pablo Manzano
 *
 * Test cases for DataManager.
 *
 * -
 */
class DataManagerTest : AndroidTestCase() {

    // The toys
    private val ball = Toy("ball", 1.0)
    private val yoyo = Toy("yo-yo", 2.0)
    private val teddy = Toy("teddy", 3.0)
    private val rubik = Toy("rubik", 4.0)
    private val NUM_TOYS = 4

    // Jake's cats
    private val moe = Cat("Moe", 1, ball)
    private val shemp = Cat("Shemp", 2, yoyo)
    private val larry = Cat("Larry", 3, null)
    private val jakesCats = arrayListOf(moe, shemp, larry)
    private val NUM_CATS_JAKE = 3

    // Mary's cats
    private val oliver = Cat("Oliver", 4, teddy)
    private val leo = Cat("Leo", 5, null)
    private val marysCats = arrayListOf(oliver, leo)
    private val NUM_CATS_MARY = 2
    private val NUM_CATS_JAKE_MARY = NUM_CATS_JAKE + NUM_CATS_MARY

    // Test people
    // Jake shares a toy (ball) with one of his cats.
    private val jake = Person("1", SyncStatus.SYNC_ERROR, "Jake", 34, ball, jakesCats)
    private val NUM_TOYS_JAKE = 2 // ball & yoyo

    // Mary shares a toy (ball) with Jake.
    private val mary = Person("2", SyncStatus.SYNC_ERROR, "Mary", 30, ball, marysCats)
    private val NUM_TOYS_MARY = 2 // ball & teddy

    // Wife and Husband share the cat Tom.
    private val tom = Cat("Tom", 5, teddy)
    private val wifeCats = arrayListOf(tom)
    private val wife = Person("3", SyncStatus.SYNC_ERROR, "Wife", 40, null, wifeCats)

    private val husbandCats = arrayListOf(tom, leo)
    private val husband = Person("3", SyncStatus.SYNC_ERROR, "Husband", 40, null, husbandCats)

    private val NUM_PEOPLE = 4

    private lateinit var dataManager: DataManager

    /**
     * Start with a fresh db.
     */
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        // Initialize Realm & DataManager
        Realm.init(getContext())
        val realmConfig = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(realmConfig)
        dataManager = DataManager(Realm.getDefaultInstance())

        // Note that we are deleting all the entities before launching any test
        dataManager.deleteAll()
    }

    @Throws(Exception::class)
    override fun tearDown() {
        super.tearDown()
    }

    /**
     * When Jack is saved into the db assert his data is there.
     */
    @Throws(Exception::class)
    fun testSaveJake() {
        dataManager.save(jake)
        onlyJackIsThere()
    }

    /**
     * When both Jack & Mary are saved, check that both of them are correct.
     */
    fun testSaveBoth() {
        dataManager.save(jake)
        dataManager.save(mary)
        onlyJackAndMary()
    }

    fun testRemoveMary() {
        dataManager.save(mary)
        dataManager.save(jake)
        dataManager.delete(mary)
        // Do not be tempted to check this:
        // onlyJackIsThere()
        // Since toys are always shared, the number of toys remaining should be NUM_TOYS
        checkNumPersonsIs(1)
        checkNumCatsIs(NUM_CATS_JAKE)
        checkNumToysIs(NUM_TOYS)
    }

    fun testRemoveMaryNonCascade() {
        dataManager.save(mary)
        dataManager.save(jake)
        dataManager.deleteNonCascade(mary)
        // Do not be tempted to check this:
        // onlyJackIsThere()
        // Since toys are always shared, the number of toys remaining should be NUM_TOYS
        checkNumPersonsIs(1)
        checkNumCatsIs(NUM_CATS_JAKE_MARY)
        checkNumToysIs(NUM_TOYS)
    }

    //region Auxiliary functions

    private fun onlyJackAndMary() {
        checkNumPersonsIs(2)
        checkNumCatsIs(NUM_CATS_JAKE_MARY)
        checkNumToysIs(NUM_TOYS)
    }

    private fun onlyJackIsThere() {
        checkNumPersonsIs(1)
        checkNumCatsIs(NUM_CATS_JAKE)
        checkNumToysIs(NUM_TOYS_JAKE)
    }

    private fun onlyMaryIsThere() {
        checkNumPersonsIs(1)
        checkNumCatsIs(NUM_CATS_MARY)
        checkNumToysIs(NUM_TOYS_MARY)
    }

    private fun checkNumToysIs(numToys: Int) {
        // Check the number of toys in the db
        val toys = dataManager.getAll(Toy::class.java)
        Assert.assertNotNull(toys)
        Assert.assertEquals(numToys, toys.size)
    }

    private fun checkNumCatsIs(numCats: Int) {
        // Check the number of cats in the db
        val cats = dataManager.getAll(Cat::class.java)
        Assert.assertNotNull(cats)
        Assert.assertEquals(numCats, cats.size)
    }

    private fun checkNumPersonsIs(numPersons: Int) {
        // Check the number of persons in the db
        val persons = dataManager.getAll(Person::class.java)
        Assert.assertNotNull(persons)
        Assert.assertEquals(numPersons, persons.size)
    }
    //endregion

}