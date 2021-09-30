import dbfile.*

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.measureTimeMillis
import kotlin.test.*

private fun ensureAncestorDirectories(file: File) {
    val parentPath = Path.of(file.parent)
    Files.createDirectories(parentPath)
}

internal class StressTests {
    private val testPath = "testData/stressTests"

    private val testRecordCounts = listOf(
        100,
        500,
        1000,
        5000,
        10000,
        25000,
        50000,
        75000,
        100000,
        250000,
        500000,
        750000,
        1000000,
        2000000,
    )
    private val testNames = testRecordCounts.map { "stress$it" }
    private val testFileNames = testNames.map { "$it.db" }

    private val minStrLen = 5
    private val maxStrLen = 20
    private val symbols = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    private fun genRandomString(): String {
        val len = kotlin.random.Random.nextInt(minStrLen, maxStrLen + 1)
        return (1..len)
            .map { symbols.random() }
            .joinToString("")
    }

    private fun genRandomDataBase(records: Int): Map<String, String> {
        val data: MutableMap<String, String> = mutableMapOf()
        repeat(records) {
            val key = genRandomString()
            val value = genRandomString()
            data[key] = value
        }
        return data.toMap()
    }

    // @Test is under comment so that by default running "all tests" gradle task would not run stress tests.

    //@Test
    fun dbWrite() {
        for (testIndex in testNames.indices) {
            val testName = testNames[testIndex]
            val fileName = testFileNames[testIndex]
            val recordCount = testRecordCounts[testIndex]
            val filePath = "$testPath/$fileName"
            val file = File(filePath)
            ensureAncestorDirectories(file)

            val data = genRandomDataBase(recordCount)
            val time = measureTimeMillis {
                writeDatabaseToFile(file, data)
            }

            println("Write to $testName: $time ms")
        }
    }

    //@Test
    fun dbRead() {
        for (testIndex in testNames.indices) {
            val testName = testNames[testIndex]
            val fileName = testFileNames[testIndex]
            val filePath = "$testPath/$fileName"
            val file = File(filePath)
            val time = measureTimeMillis {
                readDatabaseFromFile(file)
            }
            println("Read from $testName: $time ms")
        }
    }

    //@Test
    fun clearTestFolder() {
        for (testIndex in testNames.indices) {
            val fileName = testFileNames[testIndex]
            val filePath = "$testPath/$fileName"
            val file = File(filePath)
            file.delete()
        }
    }
}