package unitTests

import dbfile.*
import operations.*

import java.io.File
import kotlin.test.*

internal class MergeTests {
    private val testDataDir = "testData/merge"

    private fun writeTestDB(testName: String, dataInFirst: Map<String, String>, dataInSecond: Map<String, String>) {
        val dbFile1 = File("$testDataDir/$testName-1.db")
        val dbFile2 = File("$testDataDir/$testName-2.db")
        val dbFileCorrect = File("$testDataDir/$testName-correct.db")
        writeDatabaseToFile(dbFile1, dataInFirst)
        writeDatabaseToFile(dbFile2, dataInSecond)
        writeDatabaseToFile(dbFileCorrect, dataInFirst + dataInSecond)
    }

    // A test where no exitProcess() is called
    private fun correctMergeTestTemplate(testName: String) {
        val dbPath1 = "$testDataDir/$testName-1.db"
        val dbPath2 = "$testDataDir/$testName-2.db"
        val dbPathOut = "$testDataDir/$testName-out.db"
        val correctDBPath = "$testDataDir/$testName-correct.db"
        val outDBFile = File(dbPathOut)
        val correctDBFile = File(correctDBPath)
        mergeDataBases(dbPath1, dbPath2, dbPathOut, false)
        assertEquals(readDatabaseFromFile(correctDBFile), readDatabaseFromFile(outDBFile))
        assert(outDBFile.delete())
    }

    // A test where exitProcess() should be called
    private fun incorrectMergeTestTemplate(testName: String) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctMergeTestTemplate(testName) }
    }

    @Test
    fun noIntersection() {
        val testName = "noIntersection"
        val dataInFirst = mapOf(
            "1" to "1",
            "2" to "2",
            "6" to "1",
            "7" to "9"
        )
        val dataInSecond = mapOf(
            "3" to "1",
            "5" to "0",
            "8" to "10",
        )
        correctMergeTestTemplate(testName)
    }

    @Test
    fun noNewKeys() {
        val testName = "noNewKeys"
        val dataInFirst = mapOf(
            "1" to "1",
            "2" to "2",
            "3" to "3",
        )
        val dataInSecond = mapOf(
            "1" to "1",
            "3" to "3",
        )
        correctMergeTestTemplate(testName)
    }

    @Test
    fun partialIntersection() {
        val testName = "partialIntersection"
        val dataInFirst = mapOf(
            "1" to "1",
            "2" to "2",
            "6" to "1",
            "7" to "9"
        )
        val dataInSecond = mapOf(
            "1" to "1",
            "5" to "0",
            "8" to "10",
            "6" to "1",
        )
        correctMergeTestTemplate(testName)
    }

    @Test
    fun noNewKeysContradiction() {
        val testName = "noNewKeysContradiction"
        val dataInFirst = mapOf(
            "1" to "1",
            "2" to "2",
            "3" to "3",
        )
        val dataInSecond = mapOf(
            "1" to "1",
            "3" to "10",
        )
        incorrectMergeTestTemplate(testName)
    }

    @Test
    fun partiallyIntersectingDataContradiction() {
        val testName = "partiallyIntersectingDataContradiction"
        val dataInFirst = mapOf(
            "1" to "2",
            "2" to "2",
            "3" to "1",
            "4" to "4",
        )
        val dataInSecond = mapOf(
            "1" to "1",
            "6" to "7",
            "4" to "4",
        )
        incorrectMergeTestTemplate(testName)
    }

    @Test
    fun emptySecondFile() {
        val testName = "emptySecondFile"
        val dataInFirst = mapOf(
            "first" to "second",
            "key" to "value"
        )
        val dataInSecond: Map<String, String> = mapOf()
        correctMergeTestTemplate(testName)
    }

    @Test
    fun emptyFirstFile() {
        val testName = "emptyFirstFile"
        val dataInFirst: Map<String, String> = mapOf()
        val dataInSecond = mapOf(
            "iwant" to "add",
            "some" to "data",
        )
        correctMergeTestTemplate(testName)
    }

    @Test
    fun firstFileHasNotBeenCreated() {
        val testName = "firstFileHasNotBeenCreated"
        val dataInSecond = mapOf(
            "1" to "1",
            "-1" to "test",
        )
        incorrectMergeTestTemplate(testName)
    }

    @Test
    fun mergeToFirstFile() {
        val testName = "mergeToFirstFile"
        val dataInFirst = mapOf(
            "1" to "1",
            "2" to "2",
            "4" to "5",
            "10" to "11",
        )
        val dataInSecond = mapOf(
            "1" to "1",
            "3" to "3",
            "4" to "5",
            "11" to "20",
        )
        val dbPath1 = "$testDataDir/$testName-1.db"
        val dbPath2 = "$testDataDir/$testName-2.db"
        val dbPathCorrect = "$testDataDir/$testName-correct.db"
        val dbFile1 = File(dbPath1)
        val dbFileCorrect = File(dbPathCorrect)

        writeDatabaseToFile(dbFile1, dataInFirst)
        mergeDataBases(dbPath1, dbPath2, dbPath1, false)
        assertEquals(readDatabaseFromFile(dbFileCorrect), readDatabaseFromFile(dbFile1))
        dbFile1.delete()
        assert(!dbFile1.exists())
    }
}