import dbfile.*
import java.io.File
import kotlin.test.*

internal class WriteTests {
    private val testDataDir = "testData/write"

    // A test where no exitProcess() is called
    private fun correctWriteTestTemplate(testName: String, dataToWrite: Map<String, String>) {
        val originalDBPath = "$testDataDir/$testName-original.db"
        val newDBPath = "$testDataDir/$testName.db"
        val correctDBPath = "$testDataDir/$testName-correct.db"
        val newDBFile = File(newDBPath)
        val correctDBFile = File(correctDBPath)
        writeToDataBase(originalDBPath, newDBPath, dataToWrite)
        assertEquals(readDatabaseFromFile(correctDBFile), readDatabaseFromFile(newDBFile))
        assert(newDBFile.delete())
    }

    // A test where exitProcess() should be called
    private fun incorrectWriteTestTemplate(testName: String, dataToWrite: Map<String, String>) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctWriteTestTemplate(testName, dataToWrite) }
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
        val dataToWrite = mapOf(
            "3" to "1",
            "5" to "0",
            "8" to "10",
        )
        correctWriteTestTemplate(testName, dataToWrite)
    }

    @Test
    fun noNewKeysToWrite() {
        val testName = "noNewKeysToWrite"
        val initialData = mapOf(
            "1" to "1",
            "2" to "2",
            "3" to "3",
        )
        val dataToWrite = mapOf(
            "1" to "3",
            "3" to "10",
        )
        correctWriteTestTemplate(testName, dataToWrite)
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
        val dataToWrite = mapOf(
            "1" to "1",
            "6" to "7",
            "4" to "4",
        )
        correctWriteTestTemplate(testName, dataToWrite)
    }

    @Test
    fun emptyDataToWrite() {
        val testName = "emptyDataToWrite"
        val initialData = mapOf(
            "first" to "second",
            "key" to "value"
        )
        val dataToWrite: Map<String, String> = mapOf()

        incorrectWriteTestTemplate(testName, dataToWrite)
        val dbFile = File("$testDataDir/$testName.db")
        dbFile.delete()
        assert(!dbFile.exists())
    }

    @Test
    fun emptyInitialFile() {
        val testName = "emptyInitialFile"
        val initialData: Map<String, String> = mapOf()
        val dataToWrite = mapOf(
            "iwant" to "add",
            "some" to "data",
        )
        correctWriteTestTemplate(testName, dataToWrite)
    }

    @Test
    fun fileHasNotBeenCreated() {
        val testName = "fileHasNotBeenCreated"
        val dataToWrite = mapOf(
            "1" to "1",
            "-1" to "test",
        )
        incorrectWriteTestTemplate(testName, dataToWrite)
    }

    @Test
    fun writeToSameFile() {
        val testName = "writeToSameFile"
        val initialData = mapOf(
            "1" to "1",
            "2" to "2",
            "4" to "5",
            "10" to "11",
        )
        val dataToWrite = mapOf(
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
        writeToDataBase(dbFileName, dbFileName, dataToWrite)
        assertEquals(readDatabaseFromFile(correctDBFile), readDatabaseFromFile(dbFile))
        dbFile.delete()
        assert(!dbFile.exists())
    }
}