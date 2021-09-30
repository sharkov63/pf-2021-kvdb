package operations

import dbfile.*
import exitMsg.*

import java.io.File
import java.nio.file.Files.createDirectories
import java.nio.file.Path



fun readKeys(dbFileName: String, keys: List<String>) {
    val dbFile = File(dbFileName)
    if (!dbFile.exists()) {
        return printDatabaseNotExistMsg(dbFile.path)
    }
    if (!dbFile.canRead()) {
        return printCannotReadDataBase(dbFile.path)
    }
    val data = readDatabaseFromFile(dbFile) ?: return printInvalidDatabaseMsg(dbFile.path)
    keys.forEach { key ->
        if (!data.containsKey(key)) {
            return printDatabaseNotContainsKey(dbFile.path, key)
        }
        println(data[key])
    }
}

fun createDataBase(dbFileName: String, data: Map<String, String>) {
    val dbFile = File(dbFileName)
    val dbParentPath = Path.of(dbFile.parent)
    createDirectories(dbParentPath)
    if (!dbFile.createNewFile()) {
        return printDataBaseAlreadyExists(dbFile.path)
    }
    if (!dbFile.canWrite()) {
        return printCannotWriteToDataBase(dbFile.path)
    }
    writeDatabaseToFile(dbFile, data)
}

fun overwriteDataBase(dbFileName: String, data: Map<String, String>) {
    val dbFile = File(dbFileName)
    val dbParentPath = Path.of(dbFile.parent)
    createDirectories(dbParentPath)
    dbFile.createNewFile()
    if (!dbFile.canWrite()) {
        return printCannotWriteToDataBase(dbFile.path)
    }
    writeDatabaseToFile(dbFile, data)
}

fun addToDataBase(originalDBFileName: String, newDBFileName: String, dataToAdd: Map<String, String>) {
    if (dataToAdd.isEmpty())
        return printNoDataToWriteMsg()

    val originalDBFile = File(originalDBFileName)
    val newDBFile = File(newDBFileName)

    if (originalDBFileName != newDBFileName && !originalDBFile.exists())
        return printDatabaseNotExistMsg(originalDBFile.path)

    val newDBParentPath = Path.of(newDBFile.parent)
    createDirectories(newDBParentPath)
    newDBFile.createNewFile()

    if (!originalDBFile.canRead()) {
        return printCannotReadDataBase(originalDBFile.path)
    }

    val data = readDatabaseFromFile(originalDBFile) ?: return printInvalidDatabaseMsg(originalDBFile.path)
    val dataToWrite = data + dataToAdd.filterKeys { key -> !data.containsKey(key) }
    val omittedRecords = dataToAdd.count { (key, _) -> data.containsKey(key) }
    val writtenRecords = dataToAdd.size - omittedRecords
    if (!newDBFile.canWrite()) {
        return printCannotWriteToDataBase(newDBFile.path)
    }
    writeDatabaseToFile(newDBFile, dataToWrite)
    if (writtenRecords > 0)
        println("Successfully written $writtenRecords records to database at \"${newDBFile.path}\".")
    if (omittedRecords > 0)
        println("$omittedRecords records were omitted, as database already contains their keys.")
}

fun writeToDataBase(originalDBFileName: String, newDBFileName: String, dataToWrite: Map<String, String>) {
    if (dataToWrite.isEmpty())
        return printNoDataToWriteMsg()

    val originalDBFile = File(originalDBFileName)
    val newDBFile = File(newDBFileName)

    if (originalDBFileName != newDBFileName && !originalDBFile.exists())
        return printDatabaseNotExistMsg(originalDBFile.path)

    val newDBParentPath = Path.of(newDBFile.parent)
    createDirectories(newDBParentPath)
    newDBFile.createNewFile()

    if (!originalDBFile.canRead()) {
        return printCannotReadDataBase(originalDBFile.path)
    }

    val data = readDatabaseFromFile(originalDBFile) ?: return printInvalidDatabaseMsg(originalDBFile.path)
    val overwrittenRecords = dataToWrite.count { (key, _) -> data.containsKey(key) }
    val writtenRecords = dataToWrite.size
    if (!newDBFile.canWrite()) {
        return printCannotWriteToDataBase(newDBFile.path)
    }
    writeDatabaseToFile(newDBFile, data + dataToWrite)
    if (writtenRecords > 0)
        println("Successfully written $writtenRecords records to database at \"${newDBFile.path}\".")
    if (overwrittenRecords > 0)
        println("$overwrittenRecords of those records were overwritten")
}
