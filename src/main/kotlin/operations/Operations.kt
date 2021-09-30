package operations

import dbfile.*
import exitFuncs.*

import java.io.File
import java.nio.file.Files.createDirectories
import java.nio.file.Path

/**
 * This package contains possible operations with databases.
 */


/* Aux functions */

private fun ensureAncestorDirectories(file: File) {
    val parentPath = Path.of(file.parent)
    createDirectories(parentPath)
}

private fun ensureFile(file: File) {
    ensureAncestorDirectories(file)
    file.createNewFile()
}


/**
 * Prints the values of [keys] in a database at [dbFileName]
 * to stdout, each value on a separate line.
 *
 * If the database does not exist, cannot be read, or does not match the format,
 * it calls a corresponding exit function.
 *
 * On the first key, which is not present in the database,
 * calls [exitDatabaseNotContainsKey] exit function.
 */
fun readKeys(dbFileName: String, keys: List<String>) {
    val dbFile = File(dbFileName)
    if (!dbFile.exists()) {
        return exitDatabaseNotExist(dbFile.path)
    }
    if (!dbFile.canRead()) {
        return exitCannotReadDataBase(dbFile.path)
    }
    val data = readDatabaseFromFile(dbFile) ?: return exitInvalidDatabase(dbFile.path)
    keys.forEach { key ->
        if (!data.containsKey(key)) {
            return exitDatabaseNotContainsKey(dbFile.path, key)
        }
        println(data[key])
    }
}


/**
 * Creates a new database at [dbFileName] (with all the directories) and writes [data] in it.
 * When the file already exists, it is overwritten if [overwrite] is true, otherwise an exit function is called.
 *
 * If it cannot write to a newly created file, it calls a corresponding exit function.
 */
fun createDataBase(dbFileName: String, data: Map<String, String>, overwrite: Boolean = false) {
    val dbFile = File(dbFileName)
    ensureAncestorDirectories(dbFile)
    if (!dbFile.createNewFile() && !overwrite) {
        return exitDataBaseAlreadyExists(dbFile.path)
    }
    if (!dbFile.canWrite()) {
        return exitCannotWriteToDataBase(dbFile.path)
    }
    writeDatabaseToFile(dbFile, data)
}


/**
 * Adds [dataToAdd] to the database at [originalDBFileName]
 * and saves the result to the database at [newDBFileName] (which may be equal to [originalDBFileName])
 *
 * Records, in which keys are already present in the original database,
 * are omitted if [overwrite] is false, and are written otherwise.
 *
 * Writes statistics of records to stdout.
 *
 * If the original database cannot be reached or read, or is invalid,
 * or it cannot write to the new database, calls a corresponding exit function.
 *
 * If [dataToAdd] is empty, works as a copy operation.
 */
fun addToDataBase(originalDBFileName: String, newDBFileName: String, dataToAdd: Map<String, String>, overwrite: Boolean = false) {
    if (dataToAdd.isEmpty() && originalDBFileName == newDBFileName)
        return exitNothingToDo()

    val originalDBFile = File(originalDBFileName)
    val newDBFile = File(newDBFileName)

    if (originalDBFileName != newDBFileName && !originalDBFile.exists())
        return exitDatabaseNotExist(originalDBFile.path)

    if (!originalDBFile.canRead())
        return exitCannotReadDataBase(originalDBFile.path)

    val data = readDatabaseFromFile(originalDBFile) ?: return exitInvalidDatabase(originalDBFile.path)

    if (!overwrite) {
        val dataToWrite = data + dataToAdd.filterKeys { key -> !data.containsKey(key) }
        val omittedRecords = dataToAdd.count { (key, _) -> data.containsKey(key) }
        val writtenRecords = dataToAdd.size - omittedRecords

        ensureFile(newDBFile)
        if (!newDBFile.canWrite()) {
            return exitCannotWriteToDataBase(newDBFile.path)
        }
        writeDatabaseToFile(newDBFile, dataToWrite)

        if (writtenRecords > 0)
            println("Successfully written $writtenRecords records to database at \"${newDBFile.path}\".")
        if (omittedRecords > 0)
            println("$omittedRecords records were omitted, as database already contains their keys.")
    } else {
        val overwrittenRecords = dataToAdd.count { (key, _) -> data.containsKey(key) }
        val writtenRecords = dataToAdd.size

        ensureFile(newDBFile)
        if (!newDBFile.canWrite()) {
            return exitCannotWriteToDataBase(newDBFile.path)
        }
        writeDatabaseToFile(newDBFile, data + dataToAdd)

        if (writtenRecords > 0)
            println("Successfully written $writtenRecords records to database at \"${newDBFile.path}\".")
        if (overwrittenRecords > 0)
            println("$overwrittenRecords of those records overwrote the data.")
    }

    if (dataToAdd.isEmpty())
        println("Successfully copied \"$originalDBFile\" to \"$newDBFile\"")
}


/**
 * Deletes [keys] in database at [originalDBFileName]
 * and saves the result to the database at [newDBFileName] (which may be equal to [originalDBFileName])
 *
 * If a key appears second time in [keys], it will be deleted as if it appeared once.
 *
 * Writes statistics of deleted records to stdout.
 *
 * If the original database cannot be reached or read, or is invalid,
 * or it cannot write to the new database, calls a corresponding exit function.
 *
 * If [keys] is empty, works as a copy operation.
 */
fun deleteKeysInDataBase(originalDBFileName: String, newDBFileName: String, keys: List<String>) {
    if (keys.isEmpty() && originalDBFileName == newDBFileName)
        return exitNothingToDo()

    val originalDBFile = File(originalDBFileName)
    val newDBFile = File(newDBFileName)

    if (originalDBFileName != newDBFileName && !originalDBFile.exists())
        return exitDatabaseNotExist(originalDBFile.path)

    if (!originalDBFile.canRead())
        return exitCannotReadDataBase(originalDBFile.path)


    val data = readDatabaseFromFile(originalDBFile) ?: return exitInvalidDatabase(originalDBFile.path)
    val setKeys = keys.toSet()
    val dataToWrite = data.filterKeys { !setKeys.contains(it) }
    val deletedRecords = data.size - dataToWrite.size

    ensureFile(newDBFile)
    if (!newDBFile.canWrite()) {
        return exitCannotWriteToDataBase(newDBFile.path)
    }
    writeDatabaseToFile(newDBFile, dataToWrite)

    println(when {
        keys.isEmpty() -> "Successfully copied \"$originalDBFile\" to \"$newDBFile\""
        deletedRecords > 0 -> "Successfully deleted $deletedRecords records."
        else -> "Database was not changed: no records were deleted."
    })
}