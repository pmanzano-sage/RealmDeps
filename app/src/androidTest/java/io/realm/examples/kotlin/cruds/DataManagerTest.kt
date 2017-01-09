package io.realm.examples.kotlin.cruds

import android.test.AndroidTestCase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.examples.kotlin.data.DataManager
import io.realm.examples.kotlin.data.RealmDataManager
import io.realm.examples.kotlin.dummy.model.Cat
import io.realm.examples.kotlin.dummy.model.Person
import io.realm.examples.kotlin.dummy.model.Toy
import junit.framework.Assert

/**
 * @author Pablo Manzano
 *
 * Test cases with dummy entities for DataManager.
 *
 */
class DataManagerTest : AndroidTestCase() {

    // The toys
    private val ball = Toy("ball", 1.0)
    private val yoyo = Toy("yo-yo", 2.0)
    private val teddy = Toy("teddy", 3.0)
    private val rubik = Toy("rubik", 4.0)   // Only used by wife & husband
    private val NUM_TOYS = 4

    // Jake's cats
    private val moe = Cat("Moe", 1, ball)
    private val shemp = Cat("Shemp", 2, yoyo)
    private val larry = Cat("Larry", 3, null)
    private val jakesCats = arrayListOf(moe, shemp, larry)
    private val NUM_CATS_JAKE = jakesCats.size

    // Mary's cats
    private val oliver = Cat("Oliver", 4, teddy)
    private val leo = Cat("Leo", 5, null)
    private val marysCats = arrayListOf(oliver, leo)
    private val NUM_CATS_MARY = marysCats.size
    private val NUM_CATS_JAKE_MARY = NUM_CATS_JAKE + NUM_CATS_MARY
    private val COMMON_CATS_JAKE_MARY = 0

    // Test people
    // Jake shares a toy (ball) with one of his cats.
    private val jake = Person("Jake", 34, ball, jakesCats, null)
    private val NUM_TOYS_JAKE = 2 // ball & yoyo

    // Mary shares a toy (ball) with Jake.
    private val mary = Person("Mary", 30, ball, marysCats, null)
    private val NUM_TOYS_MARY = 2 // ball & teddy
    private val NUM_TOYS_JACK_MARY = 3 // ball, yoyo & teddy
    private val COMMON_TOYS_JACK_MARY = 1 // ball

    // Wife and Husband share the cat Tom.
    private val tom = Cat("Tom", 5, teddy)
    private val wifeCats = arrayListOf(tom)
    private val NUM_CATS_WIFE = wifeCats.size
    private val wishes = arrayListOf(rubik)
    private val wife = Person("Wife", 40, null, wifeCats, wishes)

    private val husbandCats = arrayListOf(tom, leo)
    private val husband = Person("Husband", 40, null, husbandCats, null)

    private val NUM_CATS_HUSBAND = husbandCats.size
    private val COMMON_CATS_WIFE_HUSBAND = 1 // tom
    private val NUM_WISHES_WIFE = wishes.size
    private val NUM_CATS_WIFE_HUSBAND = NUM_CATS_WIFE + NUM_CATS_HUSBAND - COMMON_CATS_WIFE_HUSBAND

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
        dataManager = RealmDataManager(Realm.getDefaultInstance())

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

    /**
     * In order to understand these test cases you have to inspect the DbModel entities, and check
     * which fields are annotated with @CascadeOnDelete.
     * Depending on these annotations and the object hierarchy the result of this test may vary.
     */
    fun testRemoveMary() {
        dataManager.save(mary)
        dataManager.save(jake)
        dataManager.delete(mary)
        // Do not be tempted to check this:
        // onlyJackIsThere()
        // Since some common objects are also deleted
        checkNumPersonsIs(1)
        checkNumCatsIs(NUM_CATS_JAKE - COMMON_CATS_JAKE_MARY)
        checkNumToysIs(NUM_TOYS_JAKE - COMMON_TOYS_JACK_MARY)
    }

    /**
     * This is the same test case as before, but using the standard way provided by Realm
     * to delete objects.
     * In this way you can compare both results.
     */
    fun testRemoveMaryNonCascade() {
        dataManager.save(mary)
        dataManager.save(jake)
        dataManager.deleteNonCascade(mary)
        // The same test as before, but not paying attention to cascades.
        checkNumPersonsIs(1)
        checkNumCatsIs(NUM_CATS_JAKE_MARY)
        checkNumToysIs(NUM_TOYS_JACK_MARY)
    }

    /**
     * Check that lists without @CascadeOnDelete are not touched on deletions.
     */
    fun testRemoveWife() {
        // Wife and Husband share the cat Tom.
        dataManager.save(wife)
        dataManager.save(husband)
        dataManager.delete(wife)
        checkNumPersonsIs(1)
        checkNumCatsIs(NUM_CATS_WIFE_HUSBAND - COMMON_CATS_WIFE_HUSBAND)
        // Toys in the wish list are not deleted because they don't have a @CascadeOnDelete
        checkNumToysIs(NUM_WISHES_WIFE)
    }

    //region Auxiliary functions

    private fun onlyJackAndMary() {
        checkNumPersonsIs(2)
        checkNumCatsIs(NUM_CATS_JAKE_MARY)
        checkNumToysIs(NUM_TOYS_JACK_MARY)
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