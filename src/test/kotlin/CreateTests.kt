import dbfile.*
import operations.*

import java.io.File
import kotlin.test.*

internal class CreateTests {
    private val testDataDir = "testData/create"

    // A test where no exitProcess() is called
    private fun correctCreateTestTemplate(testName: String, data: Map<String, String>) {
        val dbPath = "$testDataDir/$testName.db"
        val correctDBPath = "$testDataDir/$testName-correct.db"
        createDataBase(dbPath, data)
        val dbFile = File(dbPath)
        val correctDBFile = File(correctDBPath)
        assertEquals(correctDBFile.readText(), dbFile.readText())
        assert(dbFile.delete())
    }

    // A test where exitProcess() should be called
    private fun incorrectCreateTestTemplate(testName: String, data: Map<String, String>) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctCreateTestTemplate(testName, data) }
    }

    @Test
    fun createTest() {
        val testName = "createTest"
        val data = mapOf(
            "first" to "second",
            "a" to "b",
            "xxxx" to "$",
        )
        correctCreateTestTemplate(testName, data)
    }

    @Test
    fun testEmptyStrings() {
        val testName = "testEmptyStrings"
        val data = mapOf(
            "" to "",
        )
        correctCreateTestTemplate(testName, data)
    }

    @Test
    fun testEmptyData() {
        val testName = "testEmptyData"
        val data: Map<String, String> = mapOf()
        correctCreateTestTemplate(testName, data)
    }

    @Test
    fun testFileAlreadyCreated() {
        val testName = "testFileAlreadyCreated"
        val data = mapOf(
            "a" to "c",
            "s" to "t",
            "p" to "q",
            "23" to "1",
        )
        val dataInAlreadyCreated = mapOf(
            "x" to "y",
            "d" to "e",
            "key3" to "value3",
        )
        val dbFile = File("$testDataDir/$testName.db")
        writeDatabaseToFile(dbFile, dataInAlreadyCreated)
        incorrectCreateTestTemplate(testName, data)
    }

    @Test
    fun testNoParentDirectories() {
        val testName = "testNoParentDirectories"
        val testSubPath = "$testName/subdir/database.db"
        val data = mapOf(
            "p" to "q",
            "eps" to "delta",
        )
        val dbFilePath = "$testDataDir/$testSubPath"
        val correctDBFilePath = "$testDataDir/$testName-correct.db"
        val correctDBFile = File(correctDBFilePath)
        createDataBase(dbFilePath, data)
        val dbFile = File(dbFilePath)
        assertEquals(correctDBFile.readText(), dbFile.readText())
        assert(dbFile.delete())
        val testDir = File("$testDataDir/$testName")
        assert(testDir.deleteRecursively())
    }
}