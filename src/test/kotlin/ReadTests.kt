import dbfile.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.security.Permission
import kotlin.test.*

internal class ReadTests {
    private val stdout = System.`out`

    private val testDataDir = "testData/read"


    private fun writeTestDB(dbFileName: String, keys: List<String>, expectedValues: List<String>) {
        val file = File("$testDataDir/$dbFileName")
        val data = (keys zip expectedValues).toMap()
        writeDatabaseToFile(file, data)
    }

    // A test in which all values are correctly printed and no exitProcess() is called
    private fun correctTestTemplate(dbFileName: String, keys: List<String>, expectedValues: List<String>) {
        val stream = ByteArrayOutputStream()
        System.setOut(PrintStream(stream))
        readKeys("$testDataDir/$dbFileName", keys)
        System.setOut(stdout)
        val values = stream.toString().trim().lines().dropLastWhile { it.isEmpty() }
        assertEquals(expectedValues, values)
    }

    // To correctly test cases where exitProcess() is called
    // we modify SecurityManager to disallow exit at any moment
    private class NoExitSecurityManager : SecurityManager() {
        override fun checkPermission(perm: Permission?) {
            // allow anything
        }
        override fun checkPermission(perm: Permission?, context: Any?) {
            // allow anything
        }
        override fun checkExit(status: Int) {
            super.checkExit(status)
            throw Exception("Exit block")
        }
    }

    // A test where exitProcess() should be called
    private fun incorrectTestTemplate(dbFileName: String, keys: List<String>, expectedValues: List<String>) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctTestTemplate(dbFileName, keys, expectedValues) }
    }


    @Test
    fun simpleTest() {
        val dbFileName = "simpleTest.db"
        val keys = listOf("foo", "1", "zero", "foo", "1")
        val expectedValues = listOf("bar", "2", "0", "bar", "2")
        correctTestTemplate(dbFileName, keys, expectedValues)
    }

    @Test
    fun incorrectDBShortStringValue() {
        val dbFileName = "incorrectDBShortStringValue.db"
        val keys = listOf("abc")
        val expectedValues = listOf("123")
        incorrectTestTemplate(dbFileName, keys, expectedValues)
    }

    @Test
    fun incorrectDBShortStringKey() {
        val dbFileName = "incorrectDBShortStringKey.db"
        val keys = listOf("abc")
        val expectedValues = listOf("123")
        incorrectTestTemplate(dbFileName, keys, expectedValues)
    }

    @Test
    fun emptyKey() {
        val dbFileName = "emptyKey.db"
        val keys = listOf("")
        val expectedValues = listOf("non empty value")
        correctTestTemplate(dbFileName, keys, expectedValues)
    }

    @Test
    fun emptyValue() {
        val dbFileName = "emptyValue.db"
        val keys = listOf("non empty key")
        val expectedValues = listOf("")
        correctTestTemplate(dbFileName, keys, expectedValues)
    }

    @Test
    fun emptyDataBase() {
        val dbFileName = "emptyDataBase.db"
        val keys: List<String> = listOf()
        val expectedValues: List<String> = listOf()
        correctTestTemplate(dbFileName, keys, expectedValues)
    }

    @Test
    fun largeDataBase100() {
        val dbFileName = "largeDataBase100.db"
        val keys = List(100) { "key $it" }
        val expectedValues = List(100) { "value $it" }
        correctTestTemplate(dbFileName, keys, expectedValues)
    }

    @Test
    fun incorrectDBLongStringKey() {
        val dbFileName = "incorrectDBLongStringKey.db"
        val keys = listOf("foo")
        val expectedValues = listOf("bar")
        incorrectTestTemplate(dbFileName, keys, expectedValues)
    }

    @Test
    fun incorrectDBOddNumberOfStrings() {
        val dbFileName = "incorrectDBOddNumberOfStrings.db"
        val keys = listOf("key 1", "key 2")
        val expectedValues = listOf("value 1", "value 2")
        incorrectTestTemplate(dbFileName, keys, expectedValues)
    }
}