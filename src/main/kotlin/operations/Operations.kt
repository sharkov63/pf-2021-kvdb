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
 *
 * If the file already exists, or it cannot write to a newly created file,
 * it calls a corresponding exit function.
 */
fun createDataBase(dbFileName: String, data: Map<String, String>) {
    val dbFile = File(dbFileName)
    ensureAncestorDirectories(dbFile)
    if (!dbFile.createNewFile()) {
        return exitDataBaseAlreadyExists(dbFile.path)
    }
    if (!dbFile.canWrite()) {
        return exitCannotWriteToDataBase(dbFile.path)
    }
    writeDatabaseToFile(dbFile, data)
}

/**
 * Writes [data] to the database at [dbFileName].
 * Overwrites if the database already exists.
 * Creates the database (and all required directories) otherwise.
 *
 * If it cannot write to the file, calls an exit function.
 */
fun overwriteDataBase(dbFileName: String, data: Map<String, String>) {
    val dbFile = File(dbFileName)
    ensureFile(dbFile)
    if (!dbFile.canWrite()) {
        return exitCannotWriteToDataBase(dbFile.path)
    }
    writeDatabaseToFile(dbFile, data)
}


/**
 * Adds [dataToAdd] to the database at [originalDBFileName]
 * and saves the result to the database at [newDBFileName] (which may be equal to [originalDBFileName])
 * Records, in which keys are already present in the original database, are omitted.
 * Writes the number of written and omitted records to stdout.
 *
 * If the original database cannot be reached or read, or is invalid,
 * or it cannot write to the new database, calls a corresponding exit function.
 *
 * If there is no data to add, calls [exitNoDataToWrite].
 */
fun addToDataBase(originalDBFileName: String, newDBFileName: String, dataToAdd: Map<String, String>) {
    if (dataToAdd.isEmpty())
        return exitNoDataToWrite()

    val originalDBFile = File(originalDBFileName)
    val newDBFile = File(newDBFileName)

    if (originalDBFileName != newDBFileName && !originalDBFile.exists())
        return exitDatabaseNotExist(originalDBFile.path)

    if (!originalDBFile.canRead())
        return exitCannotReadDataBase(originalDBFile.path)

    val data = readDatabaseFromFile(originalDBFile) ?: return exitInvalidDatabase(originalDBFile.path)
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
}


/**
 * Writes [dataToWrite] to the database at [originalDBFileName]
 * and saves the result to the database at [newDBFileName] (which may be equal to [originalDBFileName])
 * Records, in which keys are already present in the original database, overwrite the database.
 * Writes the number of written and overwriting records to stdout.
 *
 * If the original database cannot be reached or read, or is invalid,
 * or it cannot write to the new database, calls a corresponding exit function.
 *
 * If there is no data to add, calls [exitNoDataToWrite].
 */
fun writeToDataBase(originalDBFileName: String, newDBFileName: String, dataToWrite: Map<String, String>) {
    if (dataToWrite.isEmpty())
        return exitNoDataToWrite()

    val originalDBFile = File(originalDBFileName)
    val newDBFile = File(newDBFileName)

    if (originalDBFileName != newDBFileName && !originalDBFile.exists())
        return exitDatabaseNotExist(originalDBFile.path)

    if (!originalDBFile.canRead())
        return exitCannotReadDataBase(originalDBFile.path)

    val data = readDatabaseFromFile(originalDBFile) ?: return exitInvalidDatabase(originalDBFile.path)
    val overwrittenRecords = dataToWrite.count { (key, _) -> data.containsKey(key) }
    val writtenRecords = dataToWrite.size

    ensureFile(newDBFile)
    if (!newDBFile.canWrite()) {
        return exitCannotWriteToDataBase(newDBFile.path)
    }
    writeDatabaseToFile(newDBFile, data + dataToWrite)

    if (writtenRecords > 0)
        println("Successfully written $writtenRecords records to database at \"${newDBFile.path}\".")
    if (overwrittenRecords > 0)
        println("$overwrittenRecords of those records overwrote the data.")
}
