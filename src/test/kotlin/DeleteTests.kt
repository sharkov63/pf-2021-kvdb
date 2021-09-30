import dbfile.*
import operations.*

import java.io.File
import kotlin.test.*

internal class DeleteTests {
    private val testDataDir = "testData/delete"

    // A test where no exitProcess() is called
    private fun correctDeleteTestTemplate(testName: String, keysToDelete: List<String>) {
        val originalDBPath = "$testDataDir/$testName-original.db"
        val newDBPath = "$testDataDir/$testName.db"
        val correctDBPath = "$testDataDir/$testName-correct.db"
        val newDBFile = File(newDBPath)
        val correctDBFile = File(correctDBPath)
        deleteKeysInDataBase(originalDBPath, newDBPath, keysToDelete)
        assertEquals(readDatabaseFromFile(correctDBFile), readDatabaseFromFile(newDBFile))
        newDBFile.delete()
        assert(!newDBFile.exists())
    }

    // A test where exitProcess() should be called
    private fun incorrectDeleteTestTemplate(testName: String, keysToDelete: List<String>) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctDeleteTestTemplate(testName, keysToDelete) }
    }

    @Test
    fun allKeysPresent() {
        val testName = "allKeysPresent"
        val originalData = mapOf(
            "1" to "2",
            "2" to "1",
            "3" to "3",
            "6" to "0",
            "0" to "3",
        )
        val keysToDelete = listOf("6", "1", "0")
        correctDeleteTestTemplate(testName, keysToDelete)
    }

    @Test
    fun keysPresentMultipleTimes() {
        val testName = "keysPresentMultipleTimes"
        val originalData = mapOf(
            "1" to "2",
            "2" to "1",
            "3" to "3",
            "6" to "0",
            "0" to "3",
        )
        val keysToDelete = listOf("0", "0", "1", "2", "1", "1")
        correctDeleteTestTemplate(testName, keysToDelete)
    }

    @Test
    fun someKeysNotInOriginalData() {
        val testName = "someKeysNotInOriginalData"
        val originalData = mapOf(
            "1" to "1",
            "2" to "8",
            "7" to "3",
            "3" to "2",
            "5" to "1",
        )
        val keysToDelete = listOf("1", "7", "x", "y", "", "0", "2")
        correctDeleteTestTemplate(testName, keysToDelete)
    }

    @Test
    fun allKeysNotInOriginalData() {
        val testName = "allKeysNotInOriginalData"
        val originalData = mapOf(
            "1" to "1",
            "2" to "8",
            "7" to "3",
            "3" to "2",
            "5" to "1",
        )
        val keysToDelete = listOf("q", "fff", "T", "я")
        correctDeleteTestTemplate(testName, keysToDelete)
    }

    @Test
    fun noKeysToDelete() {
        val testName = "noKeysToDelete"
        val originalData = mapOf(
            "1" to "2",
            "2" to "2",
            "5" to "10",
            "x" to "y",
        )
        val keysToDelete: List<String> = listOf()
        correctDeleteTestTemplate(testName, keysToDelete)
    }

    @Test
    fun emptyOriginalFile() {
        val testName = "emptyOriginalFile"
        val originalData: Map<String, String> = mapOf()
        val keysToDelete = listOf("key1", "key2", "key1")
        correctDeleteTestTemplate(testName, keysToDelete)
    }

    @Test
    fun fileHasNotBeenCreated() {
        val testName = "fileHasNotBeenCreated"
        val keysToDelete = listOf("key1", "test", "0")
        incorrectDeleteTestTemplate(testName, keysToDelete)
    }

    @Test
    fun deleteToSameFile() {
        val testName = "deleteToSameFile"
        val originalData = mapOf(
            "1" to "1",
            "2" to "5",
            "10" to "ten",
            "q" to "w",
            "" to "emptyString",
            "-1" to "",
        )
        val keysToDelete = listOf("1", "10", "1", "1", "", "-1")
        val dbFileName = "$testDataDir/$testName.db"
        val correctDBFileName = "$testDataDir/$testName-correct.db"
        val dbFile = File(dbFileName)
        val correctDBFile = File(correctDBFileName)
        writeDatabaseToFile(dbFile, originalData)
        deleteKeysInDataBase(dbFileName, dbFileName, keysToDelete)
        assertEquals(readDatabaseFromFile(correctDBFile), readDatabaseFromFile(dbFile))
        dbFile.delete()
        assert(!dbFile.exists())
    }
}