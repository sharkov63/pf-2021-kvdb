import dbfile.*
import operations.*

import java.io.File
import kotlin.test.*

internal class CreateOverwriteTests {
    private val testDataDir = "testData/createOverwrite"

    // A test where no exitProcess() is called
    private fun correctCreateOverwriteTestTemplate(testName: String, data: Map<String, String>) {
        val dbPath = "$testDataDir/$testName.db"
        val correctDBPath = "$testDataDir/$testName-correct.db"
        createDataBase(dbPath, data, true)
        val dbFile = File(dbPath)
        val correctDBFile = File(correctDBPath)
        assertEquals(correctDBFile.readText(), dbFile.readText())
        assert(dbFile.delete())
    }

    // A test where exitProcess() should be called
    private fun incorrectCreateOverwriteTestTemplate(testName: String, data: Map<String, String>) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctCreateOverwriteTestTemplate(testName, data) }
    }

    @Test
    fun createOverwriteTest() {
        val testName = "createOverwriteTest"
        val data = mapOf(
            "first" to "second",
            "a" to "b",
            "xxxx" to "$",
        )
        correctCreateOverwriteTestTemplate(testName, data)
    }

    @Test
    fun testEmptyStrings() {
        val testName = "testEmptyStrings"
        val data = mapOf(
            "" to "",
        )
        correctCreateOverwriteTestTemplate(testName, data)
    }

    @Test
    fun testEmptyData() {
        val testName = "testEmptyData"
        val data: Map<String, String> = mapOf()
        correctCreateOverwriteTestTemplate(testName, data)
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
        correctCreateOverwriteTestTemplate(testName, data)
        dbFile.delete()
        assert(!dbFile.exists())
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
        createDataBase(dbFilePath, data, true)
        val dbFile = File(dbFilePath)
        assertEquals(correctDBFile.readText(), dbFile.readText())
        assert(dbFile.delete())
        val testDir = File("$testDataDir/$testName")
        assert(testDir.deleteRecursively())
    }


}