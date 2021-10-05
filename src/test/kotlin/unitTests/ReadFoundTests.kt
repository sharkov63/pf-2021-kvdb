package unitTests

import dbfile.*
import operations.*

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.*

internal class ReadFoundTests {
    private val stdout = System.`out`

    private val testDataDir = "testData/read"


    private fun writeTestDB(dbFileName: String, expectedKVPairs: List<Pair<String, String>>) {
        val file = File("$testDataDir/$dbFileName")
        val data = expectedKVPairs.toMap()
        writeDatabaseToFile(file, data)
    }

    // A test where no exitProcess() is called
    private fun correctTestTemplate(dbFileName: String, keys: List<String>, expectedKVPairs: List<Pair<String, String>>) {
        val stream = ByteArrayOutputStream()
        System.setOut(PrintStream(stream))
        readFoundKeys("$testDataDir/$dbFileName", keys)
        System.setOut(stdout)

        val kvLines = stream.toString().lines().dropLast(1)
        assert(kvLines.size % 2 == 0)
        val kvPairs: MutableList<Pair<String, String>> = mutableListOf()
        for (i in kvLines.indices step 2) {
            val key = kvLines[i]
            val value = kvLines[i + 1]
            kvPairs.add(key to value)
        }
        assertEquals(expectedKVPairs, kvPairs)
    }

    // A test where exitProcess() should be called
    private fun incorrectTestTemplate(dbFileName: String, keys: List<String>, expectedKVPairs: List<Pair<String, String>>) {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        assertFails { correctTestTemplate(dbFileName, keys, expectedKVPairs) }
    }


    @Test
    fun simpleTest() {
        val dbFileName = "simpleTest.db"
        val keys = listOf("foo", "1", "zero", "foo", "1")
        val expectedKVPairs = listOf("foo" to "bar", "1" to "2", "zero" to "0", "foo" to "bar", "1" to "2")
        correctTestTemplate(dbFileName, keys, expectedKVPairs)
    }

    @Test
    fun incorrectDBShortStringValue() {
        val dbFileName = "incorrectDBShortStringValue.db"
        val keys = listOf("abc")
        val expectedKVPairs = listOf("abc" to "123")
        incorrectTestTemplate(dbFileName, keys, expectedKVPairs)
    }

    @Test
    fun incorrectDBShortStringKey() {
        val dbFileName = "incorrectDBShortStringKey.db"
        val keys = listOf("abc")
        val expectedKVPairs = listOf("abc" to "123")
        incorrectTestTemplate(dbFileName, keys, expectedKVPairs)
    }

    @Test
    fun emptyKey() {
        val dbFileName = "emptyKey.db"
        val keys = listOf("")
        val expectedKVPairs = listOf("" to "non empty value")
        correctTestTemplate(dbFileName, keys, expectedKVPairs)
    }

    @Test
    fun emptyValue() {
        val dbFileName = "emptyValue.db"
        val keys = listOf("non empty key")
        val expectedKVPairs = listOf("non empty key" to "")
        correctTestTemplate(dbFileName, keys, expectedKVPairs)
    }

    @Test
    fun emptyDataBase() {
        val dbFileName = "emptyDataBase.db"
        val keys: List<String> = listOf()
        val expectedKVPairs: List<Pair<String, String>> = listOf()
        correctTestTemplate(dbFileName, keys, expectedKVPairs)
    }

    @Test
    fun largeDataBase100() {
        val dbFileName = "largeDataBase100.db"
        val keys = List(100) { "key $it" }
        val expectedKVPairs = List(100) { "key $it" to "value $it" }
        correctTestTemplate(dbFileName, keys, expectedKVPairs)
    }

    @Test
    fun incorrectDBLongStringKey() {
        val dbFileName = "incorrectDBLongStringKey.db"
        val keys = listOf("foo")
        val expectedKVPairs = listOf("foo" to "bar")
        incorrectTestTemplate(dbFileName, keys, expectedKVPairs)
    }

    @Test
    fun incorrectDBOddNumberOfStrings() {
        val dbFileName = "incorrectDBOddNumberOfStrings.db"
        val keys = listOf("key 1", "key 2")
        val expectedKVPairs = listOf("key 1" to "value 1", "key 2" to "value 2")
        incorrectTestTemplate(dbFileName, keys, expectedKVPairs)
    }

    @Test
    fun someKeysNotPresent() {
        val dbFileName = "someKeysNotPresent.db"
        val keys = listOf("1", "2", "1", "3", "4", "5")
        val expectedKVPairs = listOf("2" to "two", "4" to "four", "5" to "five")
        correctTestTemplate(dbFileName, keys, expectedKVPairs)
    }
}