package unitTests

import parseArgs.*
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


    /* Create args */

    @Test
    fun parseCorrectCreateArgsTest() {
        val createArgs = parseCreateArgs(listOf("database.db", "key1", "value1", "key2", "value1"))
        assertNotNull(createArgs)
        assertEquals("database.db", createArgs.dbFileName)
        assertEquals(mapOf("key1" to "value1", "key2" to "value1"), createArgs.dataToWrite)
    }

    @Test
    fun parseEqualKeysCreateArgsTest() {
        val createArgs = parseCreateArgs(listOf("database.db", "key1", "value1", "key1", "value2"))
        assertNull(createArgs)
    }

    @Test
    fun parseEqualKeysAndValuesCreateArgsTest() {
        val createArgs = parseCreateArgs(listOf("database.db", "key1", "value1", "key1", "value1"))
        assertNotNull(createArgs)
        assertEquals("database.db", createArgs.dbFileName)
        assertEquals(mapOf("key1" to "value1"), createArgs.dataToWrite)
    }

    @Test
    fun parseOddKeyValueArgsCreateArgsTest() {
        val createArgs = parseCreateArgs(listOf("database.db", "key1", "value1", "key2"))
        assertNull(createArgs)
    }

    @Test
    fun parseEmptyKeyValuesCreateArgsTest() {
        val createArgs = parseCreateArgs(listOf("database.db"))
        assertNotNull(createArgs)
        assertEquals("database.db", createArgs.dbFileName)
        assertEquals(mapOf(), createArgs.dataToWrite)
    }

    @Test
    fun parseEmptyCreateArgsTest() {
        val createArgs = parseCreateArgs(listOf())
        assertNull(createArgs)
    }


    /* Add args */

    @Test
    fun parseCorrectAddArgsTest() {
        val addArgs = parseAddArgs(listOf("database1.db", "database2.db", "key1", "value1", "key2", "value1"))
        assertNotNull(addArgs)
        assertEquals("database1.db", addArgs.originalDBFileName)
        assertEquals("database2.db", addArgs.newDBFileName)
        assertEquals(mapOf("key1" to "value1", "key2" to "value1"), addArgs.dataToWrite)
    }

    @Test
    fun parseExclamationMarkAddArgsTest() {
        val addArgs = parseAddArgs(listOf("database1.db", "!", "key1", "value1", "key2", "value1"))
        assertNotNull(addArgs)
        assertEquals("database1.db", addArgs.originalDBFileName)
        assertEquals("database1.db", addArgs.newDBFileName)
        assertEquals(mapOf("key1" to "value1", "key2" to "value1"), addArgs.dataToWrite)
    }

    @Test
    fun parseEqualKeysAddArgsTest() {
        val addArgs = parseAddArgs(listOf("database1.db", "database2.db", "key1", "value1", "key1", "value2"))
        assertNull(addArgs)
    }

    @Test
    fun parseEqualKeysAndValuesAddArgsTest() {
        val addArgs = parseAddArgs(listOf("database1.db", "database2.db", "key1", "value1", "key1", "value1"))
        assertNotNull(addArgs)
        assertEquals("database1.db", addArgs.originalDBFileName)
        assertEquals("database2.db", addArgs.newDBFileName)
        assertEquals(mapOf("key1" to "value1"), addArgs.dataToWrite)
    }

    @Test
    fun parseOddKeyValueArgsAddArgsTest() {
        val addArgs = parseAddArgs(listOf("database1.db", "database2.db", "key1", "value1", "key2"))
        assertNull(addArgs)
    }

    @Test
    fun parseEmptyKeyValuesAddArgsTest() {
        val addArgs = parseAddArgs(listOf("database1.db", "database2.db"))
        assertNotNull(addArgs)
        assertEquals("database1.db", addArgs.originalDBFileName)
        assertEquals("database2.db", addArgs.newDBFileName)
        assertEquals(mapOf(), addArgs.dataToWrite)
    }

    @Test
    fun parseOnlyOneFileAddArgsTest() {
        val addArgs = parseAddArgs(listOf("database1.db"))
        assertNull(addArgs)
    }

    @Test
    fun parseEmptyAddArgsTest() {
        val addArgs = parseAddArgs(listOf())
        assertNull(addArgs)
    }


    /* Delete args */

    @Test
    fun parseCorrectDeleteArgsTest() {
        val deleteArgs = parseDeleteArgs(listOf("database1.db", "database2.db", "key1", "key2", "key1"))
        assertNotNull(deleteArgs)
        assertEquals("database1.db", deleteArgs.originalDBFileName)
        assertEquals("database2.db", deleteArgs.newDBFileName)
        assertEquals(listOf("key1", "key2", "key1"), deleteArgs.keys)
    }

    @Test
    fun parseExclamationMarkDeleteArgsTest() {
        val deleteArgs = parseDeleteArgs(listOf("database1.db", "!", "key1", "key2", "key1"))
        assertNotNull(deleteArgs)
        assertEquals("database1.db", deleteArgs.originalDBFileName)
        assertEquals("database1.db", deleteArgs.newDBFileName)
        assertEquals(listOf("key1", "key2", "key1"), deleteArgs.keys)
    }

    @Test
    fun parseNoKeysDeleteArgsTest() {
        val deleteArgs = parseDeleteArgs(listOf("database1.db", "database2.db"))
        assertNotNull(deleteArgs)
        assertEquals("database1.db", deleteArgs.originalDBFileName)
        assertEquals("database2.db", deleteArgs.newDBFileName)
        assertEquals(listOf(), deleteArgs.keys)
    }

    @Test
    fun parseOnlyOneFileDeleteArgsTest() {
        val deleteArgs = parseDeleteArgs(listOf("database1.db"))
        assertNull(deleteArgs)
    }

    @Test
    fun parseEmptyDeleteArgsTest() {
        val deleteArgs = parseDeleteArgs(listOf())
        assertNull(deleteArgs)
    }


    /* Copy args */

    @Test
    fun parseCorrectCopyArgs() {
        val copyArgs = parseCopyArgs(listOf("database1.db", "database2.db"))
        assertNotNull(copyArgs)
        assertEquals("database1.db", copyArgs.fromDB)
        assertEquals("database2.db", copyArgs.toDB)
    }

    @Test
    fun parseExclamationMarkCopyArgs() {
        val copyArgs = parseCopyArgs(listOf("database1.db", "!"))
        assertNotNull(copyArgs)
        assertEquals("database1.db", copyArgs.fromDB)
        assertEquals("database1.db", copyArgs.toDB)
    }

    @Test
    fun parseOnlyOneFileCopyArgsTest() {
        val copyArgs = parseCopyArgs(listOf("database1.db"))
        assertNull(copyArgs)
    }

    @Test
    fun parseEmptyCopyArgsTest() {
        val copyArgs = parseCopyArgs(listOf())
        assertNull(copyArgs)
    }


    /* Merge args */

    @Test
    fun parseCorrectMergeArgs() {
        val mergeArgs = parseMergeArgs(listOf("database1.db", "database2.db", "database3.db"))
        assertNotNull(mergeArgs)
        assertEquals("database1.db", mergeArgs.db1)
        assertEquals("database2.db", mergeArgs.db2)
        assertEquals("database3.db", mergeArgs.dbOut)
    }

    @Test
    fun parseExclamationMarkMergeArgs() {
        val mergeArgs = parseMergeArgs(listOf("database1.db", "database2.db", "!"))
        assertNotNull(mergeArgs)
        assertEquals("database1.db", mergeArgs.db1)
        assertEquals("database2.db", mergeArgs.db2)
        assertEquals("database1.db", mergeArgs.dbOut)
    }

    @Test
    fun parseDoubleExclamationMarkMergeArgs() {
        val mergeArgs = parseMergeArgs(listOf("database1.db", "database2.db", "!!"))
        assertNotNull(mergeArgs)
        assertEquals("database1.db", mergeArgs.db1)
        assertEquals("database2.db", mergeArgs.db2)
        assertEquals("database2.db", mergeArgs.dbOut)
    }

    @Test
    fun parseOnlyTwoFilesMergeArgsTest() {
        val mergeArgs = parseMergeArgs(listOf("database1.db", "database2.db"))
        assertNull(mergeArgs)
    }

    @Test
    fun parseOnlyOneFileMergeArgsTest() {
        val mergeArgs = parseMergeArgs(listOf("database1.db"))
        assertNull(mergeArgs)
    }

    @Test
    fun parseEmptyMergeArgsTest() {
        val mergeArgs = parseMergeArgs(listOf())
        assertNull(mergeArgs)
    }
}