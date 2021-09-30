import dbfile.*
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

fun addToDataBase(dbFileName: String, dataToAdd: Map<String, String>) {
    if (dataToAdd.isEmpty()) return printNoDataToWriteMsg()
    val dbFile = File(dbFileName)
    val dbParentPath = Path.of(dbFile.parent)
    createDirectories(dbParentPath)
    dbFile.createNewFile()
    if (!dbFile.canRead()) {
        return printCannotReadDataBase(dbFile.path)
    }
    val data = readDatabaseFromFile(dbFile) ?: return printInvalidDatabaseMsg(dbFile.path)
    val dataToWrite = data + dataToAdd.filterKeys { key -> !data.containsKey(key) }
    val omittedRecords = dataToAdd.count { (key, _) -> data.containsKey(key) }
    val writtenRecords = dataToAdd.size - omittedRecords
    if (!dbFile.canWrite()) {
        return printCannotWriteToDataBase(dbFile.path)
    }
    writeDatabaseToFile(dbFile, dataToWrite)
    if (writtenRecords > 0)
        println("Successfully written $writtenRecords records to database at \"${dbFile.path}\".")
    if (omittedRecords > 0)
        println("$omittedRecords records were omitted, as database already contains their keys.")
}

fun writeToDataBase(dbFileName: String, dataToWrite: Map<String, String>) {
    if (dataToWrite.isEmpty()) return printNoDataToWriteMsg()
    val dbFile = File(dbFileName)
    val dbParentPath = Path.of(dbFile.parent)
    createDirectories(dbParentPath)
    dbFile.createNewFile()
    if (!dbFile.canRead()) {
        return printCannotReadDataBase(dbFile.path)
    }
    val data = readDatabaseFromFile(dbFile) ?: return printInvalidDatabaseMsg(dbFile.path)
    val overwrittenRecords = dataToWrite.count { (key, _) -> data.containsKey(key) }
    val writtenRecords = dataToWrite.size
    if (!dbFile.canWrite()) {
        return printCannotWriteToDataBase(dbFile.path)
    }
    writeDatabaseToFile(dbFile, data + dataToWrite)
    if (writtenRecords > 0)
        println("Successfully written $writtenRecords records to database at \"${dbFile.path}\".")
    if (overwrittenRecords > 0)
        println("$overwrittenRecords of those records were overwritten")
}


data class ReadArgs(val dbFileName: String, val keys: List<String>)

fun parseReadArgs(args: List<String>): ReadArgs? {
    if (args.size < 2) return null
    val dbFileName = args[0]
    args.drop(1)
    return ReadArgs(dbFileName, args.toList())
}

data class WriteArgs(val dbFileName: String, val dataToWrite: Map<String, String>)

fun parseWriteArgs(args: List<String>): WriteArgs? {
    if (args.isEmpty()) return null
    if (args.size % 2 != 1) return null
    val dbFileName = args[0]
    val data: MutableMap<String, String> = mutableMapOf()
    val kvArgs = args.drop(1)
    for (i in kvArgs.indices step 2) {
        val key = kvArgs[i]
        val value = kvArgs[i + 1]
        data[key] = value
    }
    return WriteArgs(dbFileName, data.toMap())
}


fun read(args: List<String>) {
    val readArgs = parseReadArgs(args) ?: return printIncorrectArgsMsg()
    readKeys(readArgs.dbFileName, readArgs.keys)
}

fun create(args: List<String>) {
    val createArgs = parseWriteArgs(args) ?: return printIncorrectArgsMsg()
    createDataBase(createArgs.dbFileName, createArgs.dataToWrite)
}

fun overwrite(args: List<String>) {
    val overwriteArgs = parseWriteArgs(args) ?: return printIncorrectArgsMsg()
    createDataBase(overwriteArgs.dbFileName, overwriteArgs.dataToWrite)
}

fun add(args: List<String>) {
    val addArgs = parseWriteArgs(args) ?: return printIncorrectArgsMsg()
    addToDataBase(addArgs.dbFileName, addArgs.dataToWrite)
}

fun write(args: List<String>) {
    val writeArgs = parseWriteArgs(args) ?: return printIncorrectArgsMsg()
    writeToDataBase(writeArgs.dbFileName, writeArgs.dataToWrite)
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
