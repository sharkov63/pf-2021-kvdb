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
    val parentPath = file.absoluteFile.parentFile.toPath()
    createDirectories(parentPath)
}

private fun ensureFile(file: File) {
    ensureAncestorDirectories(file)
    file.createNewFile()
}

private fun readDatabaseFromFileName(filename: String): Map<String, String> {
    val file = File(filename)
    if (!file.exists())
        exitDatabaseNotExist(file.path)
    if (!file.canRead())
        exitCannotReadDataBase(file.path)
    val data = readDatabaseFromFile(file)
    if (data == null)
        exitInvalidDatabase(file.path)
    else
        return data
    throw Exception("exitInvalidDatabase() seemed not to exit the program!")
}

private fun writeToDatabaseByFileName(filename: String, data: Map<String, String>) {
    val file = File(filename)
    ensureFile(file)
    if (!file.canWrite()) {
        return exitCannotWriteToDataBase(file.path)
    }
    writeDatabaseToFile(file, data)
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
    val data = readDatabaseFromFileName(dbFileName)
    keys.forEach { key ->
        if (!data.containsKey(key)) {
            return exitDatabaseNotContainsKey(dbFileName, key)
        }
        println(data[key])
    }
}


/**
 * Prints to stdout key-value pairs of those keys in [keys], which exist in [dbFileName].
 * Each key-value pair is printed in two lines:
 * first line contains the key string,
 * the second line contains value string.
 *
 * If some key is not present in the database, it is skipped.
 *
 * If the database does not exist, cannot be read, or does not match the format,
 * it calls a corresponding exit function.
 */
fun readFoundKeys(dbFileName: String, keys: List<String>) {
    val data = readDatabaseFromFileName(dbFileName)
    keys.forEach { key ->
        if (data.containsKey(key)) {
            println(key)
            println(data[key])
        }
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
    writeToDatabaseByFileName(dbFileName, data)
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

    val data = readDatabaseFromFileName(originalDBFileName)
    val dataToWrite = if (!overwrite)
        dataToAdd + data
    else
        data + dataToAdd

    writeToDatabaseByFileName(newDBFileName, dataToWrite)

    val overlappedRecords = dataToAdd.count { (key, _) -> data.containsKey(key) }
    val writtenRecords = dataToAdd.size - if (!overwrite) overlappedRecords else 0
    if (writtenRecords > 0)
        println("Successfully written $writtenRecords records to database \"${newDBFileName}\".")
    if (overlappedRecords > 0) {
        println(
            when (overwrite) {
                false -> "$overlappedRecords records were omitted, as database already contains their keys. To allow overwriting, please use -ao or -addo or -addOverwrite."
                true -> "$overlappedRecords of those records overwrote the data."
            }
        )
    }

    if (dataToAdd.isEmpty())
        println("Successfully copied \"$originalDBFileName\" to \"$newDBFileName\"")
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

    val data = readDatabaseFromFileName(originalDBFileName)
    val setOfKeys = keys.toSet()
    val dataToWrite = data.filterKeys { !setOfKeys.contains(it) }
    val deletedRecords = data.size - dataToWrite.size

    writeToDatabaseByFileName(newDBFileName, dataToWrite)

    println(
        when {
            keys.isEmpty() -> "Successfully copied \"$originalDBFileName\" to \"$newDBFileName\""
            deletedRecords > 0 -> "Successfully deleted $deletedRecords of original records."
            else -> "Database was not changed: no records were deleted."
        }
    )
}


/**
 * Copies database at [dbPath1] to the database at [dbPath2].
 * Creates [dbPath2] (and all ancestor directories), if necessary.
 *
 * If paths are equals, calls [exitNothingToDo].
 */
fun copyDataBase(dbPath1: String, dbPath2: String) {
    if (dbPath1 == dbPath2)
        return exitNothingToDo()

    val data = readDatabaseFromFileName(dbPath1)
    writeToDatabaseByFileName(dbPath2, data)

    println("Successfully copied \"$dbPath1\" to \"$dbPath2\"")
}


/**
 * Merges databases [firstDBPath] and [secondDBPath], and writes result to database [outDBPath].
 *
 * If the records in databases contradict each other, it calls an exit function if [overwrite] is false,
 * otherwise it overwrites records in [firstDBPath] with records in [secondDBPath].
 *
 * If [overwrite] is true, writes statistics of overwritten records to stdout.
 */
fun mergeDataBases(firstDBPath: String, secondDBPath: String, outDBPath: String, overwrite: Boolean = false) {
    val data1 = readDatabaseFromFileName(firstDBPath)
    val data2 = readDatabaseFromFileName(secondDBPath)

    val overwrittenRecords = data2.count { (key, value) ->
        data1.containsKey(key) && data1[key] != value
    }
    if (!overwrite && overwrittenRecords > 0) {
        exitDatabasesContradictEachOther(firstDBPath, secondDBPath)
    }

    writeToDatabaseByFileName(outDBPath, data1 + data2)

    println("Databases \"$firstDBPath\" and \"$secondDBPath\" are successfully merged into database \"$outDBPath\"")
    if (overwrittenRecords > 0)
        println("$overwrittenRecords of original records were overwritten.")
}