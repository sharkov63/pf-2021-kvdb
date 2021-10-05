package unitTests

import dbfile.*
import operations.*

import java.io.File
import kotlin.test.*

internal class CopyTests {
    private val testDataDir = "testData/copy"

    // A test where no exitProcess() is called
    private fun correctCopyTestTemplate(testName: String) {
        val fromDBPath = "$testDataDir/$testName-from.db"
        val toDBPath = "$testDataDir/$testName-to.db"
        val fromDBFile = File(fromDBPath)
        val toDBFile = File(toDBPath)
        copyDataBase(fromDBPath, toDBPath)
        assertEquals(readDatabaseFromFile(fromDBFile), readDatabaseFromFile(toDBFile))
        toDBFile.delete()
        assert(!toDBFile.exists())
    }

    // A test where exitProcess() should be called
    private fun incorrectDeleteTestTemplate(testName: String) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctCopyTestTemplate(testName) }
    }

    @Test
    fun copyTest() {
        val testName = "copyTest"
        val data = mapOf(
            "1" to "one",
            "2" to "two",
            "3" to "three",
        )
        correctCopyTestTemplate(testName)
    }

    @Test
    fun noOriginalFile() {
        val testName = "noOriginalFile"
        incorrectDeleteTestTemplate(testName)
    }

    @Test
    fun noParentDirectories() {
        val testName = "noParentDirectories"
        val data = mapOf(
            "some" to "data",
            "not" to "important",
            "foo" to "bar",
        )
        val fromDBPath = "$testDataDir/$testName-from.db"
        val toDBPath = "$testDataDir/$testName-folder/subdir/subsubdir/to.db"
        val fromDBFile = File(fromDBPath)
        val toDBFile = File(toDBPath)
        copyDataBase(fromDBPath, toDBPath)
        assertEquals(readDatabaseFromFile(fromDBFile), readDatabaseFromFile(toDBFile))
        val testDir = File("$testDataDir/$testName-folder")
        assert(testDir.deleteRecursively())
    }
}