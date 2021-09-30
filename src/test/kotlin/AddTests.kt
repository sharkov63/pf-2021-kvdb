import dbfile.*
import java.io.File
import kotlin.test.*

internal class AddTests {
    private val testDataDir = "testData/add"

    // A test where no exitProcess() is called
    private fun correctAddTestTemplate(testName: String, initialData: Map<String, String>, dataToAdd: Map<String, String>) {
        val dbPath = "$testDataDir/$testName.db"
        val correctDBPath = "$testDataDir/$testName-correct.db"
        val dbFile = File(dbPath)
        val correctDBFile = File(correctDBPath)
        writeDatabaseToFile(dbFile, initialData)
        addToDataBase(dbPath, dataToAdd)
        assertEquals(readDatabaseFromFile(correctDBFile), readDatabaseFromFile(dbFile))
        assert(dbFile.delete())
    }

    // A test where exitProcess() should be called
    private fun incorrectAddTestTemplate(testName: String, initialData: Map<String, String>, dataToAdd: Map<String, String>) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctAddTestTemplate(testName, initialData, dataToAdd) }
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
        correctAddTestTemplate(testName, initialData, dataToAdd)
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
        correctAddTestTemplate(testName, initialData, dataToAdd)
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
        correctAddTestTemplate(testName, initialData, dataToAdd)
    }

    @Test
    fun emptyDataToAdd() {
        val testName = "emptyDataToAdd"
        val initialData = mapOf(
            "first" to "second",
            "key" to "value"
        )
        val dataToAdd: Map<String, String> = mapOf()
        incorrectAddTestTemplate(testName, initialData, dataToAdd)
        val dbFile = File("$testDataDir/$testName.db")
        assert(dbFile.delete())
    }

    @Test
    fun emptyInitialFile() {
        val testName = "emptyInitialFile"
        val initialData: Map<String, String> = mapOf()
        val dataToAdd = mapOf(
            "iwant" to "add",
            "some" to "data",
        )
        correctAddTestTemplate(testName, initialData, dataToAdd)
    }

    @Test
    fun fileHasNotBeenCreated() {
        val testName = "fileHasNotBeenCreated"
        val dataToAdd = mapOf(
            "1" to "1",
            "-1" to "test",
        )

        val dbPath = "$testDataDir/$testName.db"
        val correctDBPath = "$testDataDir/$testName-correct.db"
        val dbFile = File(dbPath)
        val correctDBFile = File(correctDBPath)
        addToDataBase(dbPath, dataToAdd)
        assertEquals(readDatabaseFromFile(correctDBFile), readDatabaseFromFile(dbFile))
        assert(dbFile.delete())
    }
}