import dbfile.*
import java.io.File
import kotlin.test.*

internal class ParseArgsTests {


    /* Read args */

    @Test
    fun parseCorrectReadArgsTest() {
        val dbFileName = "database.db"
        val keys = listOf("key1", "key2", "key1")
        val readArgs = parseReadArgs(listOf(dbFileName) + keys)
        assertNotNull(readArgs)
        assertEquals(dbFileName, readArgs.dbFileName)
        assertEquals(keys, readArgs.keys)
    }

    @Test
    fun parseEmptyReadArgsTest() {
        val readArgs = parseReadArgs(listOf())
        assertNull(readArgs)
    }

    @Test
    fun parseNoKeysReadArgsTest() {
        val readArgs = parseReadArgs(listOf("database.db"))
        assertNotNull(readArgs)
        assertEquals("database.db", readArgs.dbFileName)
        assertEquals(listOf(), readArgs.keys)
    }


    /* Modify args */

    @Test
    fun parseCorrectModifyArgsTest() {
        val modifyArgs = parseModifyArgs(listOf("database.db", "key1", "value1", "key2", "value1"))
        assertNotNull(modifyArgs)
        assertEquals("database.md", modifyArgs.dbFileName)
        assertEquals(mapOf("key1" to "value1", "key2" to "value1"), modifyArgs.dataToWrite)
    }

    @Test
    fun parseEqualKeysModifyArgsTest() {
        val modifyArgs = parseModifyArgs(listOf("database.db", "key1", "value1", "key1", "value2"))
        assertNull(modifyArgs)
    }

    @Test
    fun parseOddKeyValueArgsModifyArgsTest() {
        val modifyArgs = parseModifyArgs(listOf("database.db", "key1", "value1", "key2"))
        assertNull(modifyArgs)
    }

    @Test
    fun parseEmptyKeyValuesModifyArgsTest() {
        val modifyArgs = parseModifyArgs(listOf("database.db"))
        assertNotNull(modifyArgs)
        assertEquals("database.db", modifyArgs.dbFileName)
        assertEquals(mapOf(), modifyArgs.dataToWrite)
    }

    @Test
    fun parseEmptyModifyArgsTest() {
        val modifyArgs = parseModifyArgs(listOf())
        assertNull(modifyArgs)
    }


    /* Write args */

    @Test
    fun parseCorrectWriteArgsTest() {
        val writeArgs = parseWriteArgs(listOf("database1.db", "database2.db", "key1", "value1", "key2", "value1"))
        assertNotNull(writeArgs)
        assertEquals("database1.db", writeArgs.originalDBFileName)
        assertEquals("database2.db", writeArgs.newDBFileName)
        assertEquals(mapOf("key1" to "value1", "key2" to "value1"), writeArgs.dataToWrite)
    }

    @Test
    fun parseExclamationMarkWriteArgsTest() {
        val writeArgs = parseWriteArgs(listOf("database1.db", "!", "key1", "value1", "key2", "value1"))
        assertNotNull(writeArgs)
        assertEquals("database1.db", writeArgs.originalDBFileName)
        assertEquals("database1.db", writeArgs.newDBFileName)
        assertEquals(mapOf("key1" to "value1", "key2" to "value1"), writeArgs.dataToWrite)
    }

    @Test
    fun parseEqualKeysWriteArgsTest() {
        val writeArgs = parseWriteArgs(listOf("database1.db", "database2.db", "key1", "value1", "key1", "value2"))
        assertNull(writeArgs)
    }

    @Test
    fun parseOddKeyValueArgsWriteArgsTest() {
        val writeArgs = parseWriteArgs(listOf("database1.db", "database2.db", "key1", "value1", "key2"))
        assertNull(writeArgs)
    }

    @Test
    fun parseEmptyKeyValuesWriteArgsTest() {
        val writeArgs = parseWriteArgs(listOf("database1.db", "database2.db"))
        assertNotNull(writeArgs)
        assertEquals("database1.db", writeArgs.originalDBFileName)
        assertEquals("database2.db", writeArgs.newDBFileName)
        assertEquals(mapOf(), writeArgs.dataToWrite)
    }

    @Test
    fun parseOnlyOneFileWriteArgsTest() {
        val writeArgs = parseWriteArgs(listOf("database1.db"))
        assertNull(writeArgs)
    }

    @Test
    fun parseEmptyWriteArgsTest() {
        val writeArgs = parseWriteArgs(listOf())
        assertNull(writeArgs)
    }
}