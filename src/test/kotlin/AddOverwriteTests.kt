import dbfile.*
import operations.*

import java.io.File
import kotlin.test.*

internal class AddOverwriteTests {
    private val testDataDir = "testData/addOverwrite"

    // A test where no exitProcess() is called
    private fun correctAddOverwriteTestTemplate(testName: String, dataToAdd: Map<String, String>) {
        val originalDBPath = "$testDataDir/$testName-original.db"
        val newDBPath = "$testDataDir/$testName.db"
        val correctDBPath = "$testDataDir/$testName-correct.db"
        val newDBFile = File(newDBPath)
        val correctDBFile = File(correctDBPath)
        addToDataBase(originalDBPath, newDBPath, dataToAdd, true)
        assertEquals(readDatabaseFromFile(correctDBFile), readDatabaseFromFile(newDBFile))
        assert(newDBFile.delete())
    }

    // A test where exitProcess() should be called
    private fun incorrectAddOverwriteTestTemplate(testName: String, dataToAdd: Map<String, String>) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctAddOverwriteTestTemplate(testName, dataToAdd) }
    }

    @Test
    fun noIntersection() {
        val testName = "noIntersection"
        val initialData = mapOf(
            "1" to "1",
            "2" to "2",
            "6" to "1",
            "7" to "9"
        )
        val dataToAdd = mapOf(
            "3" to "1",
            "5" to "0",
            "8" to "10",
        )
        correctAddOverwriteTestTemplate(testName, dataToAdd)
    }

    @Test
    fun noNewKeysToAdd() {
        val testName = "noNewKeysToAdd"
        val initialData = mapOf(
            "1" to "1",
            "2" to "2",
            "3" to "3",
        )
        val dataToAdd = mapOf(
            "1" to "3",
            "3" to "10",
        )
        correctAddOverwriteTestTemplate(testName, dataToAdd)
    }

    @Test
    fun partiallyIntersectingData() {
        val testName = "partiallyIntersectingData"
        val initialData = mapOf(
            "1" to "2",
            "2" to "2",
            "3" to "1",
            "4" to "4",
        )
        val dataToAdd = mapOf(
            "1" to "1",
            "6" to "7",
            "4" to "4",
        )
        correctAddOverwriteTestTemplate(testName, dataToAdd)
    }

    @Test
    fun emptyDataToAdd() {
        val testName = "emptyDataToAdd"
        val initialData = mapOf(
            "first" to "second",
            "key" to "value"
        )
        val dataToAdd: Map<String, String> = mapOf()
        incorrectAddOverwriteTestTemplate(testName, dataToAdd)
        val dbFile = File("$testDataDir/$testName.db")
        dbFile.delete()
        assert(!dbFile.exists())
    }

    @Test
    fun emptyInitialFile() {
        val testName = "emptyInitialFile"
        val initialData: Map<String, String> = mapOf()
        val dataToAdd = mapOf(
            "iwant" to "add",
            "some" to "data",
        )
        correctAddOverwriteTestTemplate(testName, dataToAdd)
    }

    @Test
    fun fileHasNotBeenCreated() {
        val testName = "fileHasNotBeenCreated"
        val dataToAdd = mapOf(
            "1" to "1",
            "-1" to "test",
        )
        incorrectAddOverwriteTestTemplate(testName, dataToAdd)
    }

    @Test
    fun addOverwriteToSameFile() {
        val testName = "addOverwriteToSameFile"
        val initialData = mapOf(
            "1" to "1",
            "2" to "2",
            "4" to "5",
            "10" to "11",
        )
        val dataToAdd = mapOf(
            "1" to "2",
            "3" to "3",
            "4" to "5",
            "11" to "20",
        )
        val dbFileName = "$testDataDir/$testName.db"
        val correctDBFileName = "$testDataDir/$testName-correct.db"
        val dbFile = File(dbFileName)
        val correctDBFile = File(correctDBFileName)
        writeDatabaseToFile(dbFile, initialData)
        addToDataBase(dbFileName, dbFileName, dataToAdd, true)
        assertEquals(readDatabaseFromFile(correctDBFile), readDatabaseFromFile(dbFile))
        dbFile.delete()
        assert(!dbFile.exists())
    }
}