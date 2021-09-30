import dbfile.*
import parseArgs.*
import java.io.File
import java.nio.file.Files.createDirectories
import java.nio.file.Path
import kotlin.system.exitProcess



fun printIncorrectArgsMsg() {
    println("Incorrect arguments. Please use -h or --help option to see help message.")
    exitProcess(1)
}

fun printHelpMsg() {
    TODO("write help message")
}

fun printDatabaseNotExistMsg(dbPath: String) {
    println("Database at \"$dbPath\" does not exist!")
    exitProcess(1)
}

fun printCannotReadDataBase(dbPath: String) {
    println("Cannot read database at \"$dbPath\"!")
    exitProcess(1)
}

fun printCannotWriteToDataBase(dbPath: String) {
    println("Cannot write to database at \"$dbPath\"!")
    exitProcess(1)
}

fun printInvalidDatabaseMsg(dbPath: String) {
    println("Database at \"$dbPath\" is not valid!")
    exitProcess(1)
}

fun printDatabaseNotContainsKey(dbPath: String, key: String) {
    println("Database at \"$dbPath\" doesn't contain the key \"$key\"!")
    exitProcess(1)
}

fun printDataBaseAlreadyExists(dbPath: String) {
    println("Database at \"$dbPath\" already exists! Use -o or --overwrite to allow overwriting the database.")
    exitProcess(1)
}

fun printNoDataToWriteMsg() {
    println("No data to write.")
    exitProcess(0)
}

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


fun read(args: List<String>) {
    val readArgs = parseReadArgs(args) ?: return printIncorrectArgsMsg()
    readKeys(readArgs.dbFileName, readArgs.keys)
}

fun create(args: List<String>) {
    val createArgs = parseModifyArgs(args) ?: return printIncorrectArgsMsg()
    createDataBase(createArgs.dbFileName, createArgs.dataToWrite)
}

fun overwrite(args: List<String>) {
    val overwriteArgs = parseModifyArgs(args) ?: return printIncorrectArgsMsg()
    createDataBase(overwriteArgs.dbFileName, overwriteArgs.dataToWrite)
}

fun add(args: List<String>) {
    val addArgs = parseWriteArgs(args) ?: return printIncorrectArgsMsg()
    addToDataBase(addArgs.originalDBFileName, addArgs.newDBFileName, addArgs.dataToWrite)
}

fun write(args: List<String>) {
    val writeArgs = parseWriteArgs(args) ?: return printIncorrectArgsMsg()
    writeToDataBase(writeArgs.originalDBFileName, writeArgs.newDBFileName, writeArgs.dataToWrite)
}

fun parseOption(option: String, args: List<String>) {
    when (option) {
        "-r", "--read" -> read(args)
        "-c", "--create" -> create(args)
        "-o", "--overwrite" -> overwrite(args)
        "-a", "--add" -> add(args)
        "-w", "--write" -> write(args)
        "-h", "--help" -> printHelpMsg()
        else -> printIncorrectArgsMsg()
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) return printIncorrectArgsMsg()

    val option = args.first()
    parseOption(option, args.drop(1).toList())
}
