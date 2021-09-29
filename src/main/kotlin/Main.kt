import dbfile.*
import java.io.File
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
    dbFile.createNewFile()
    if (!dbFile.canWrite()) {
        return printCannotWriteToDataBase(dbFile.path)
    }
    writeDatabaseToFile(dbFile, data)
}

fun parseReadArgs(args: List<String>) {
    if (args.size < 2) return printIncorrectArgsMsg()
    val dbFileName = args[0]
    args.drop(1)
    readKeys(dbFileName, args.toList())
}

fun parseCreateArgs(args: List<String>) {
    if (args.isEmpty()) return printIncorrectArgsMsg()
    if (args.size % 2 != 1) return printIncorrectArgsMsg()
    val dbFileName = args[0]
    val data: MutableMap<String, String> = mutableMapOf()
    val kvArgs = args.drop(1)
    for (i in kvArgs.indices step 2) {
        val key = kvArgs[i]
        val value = kvArgs[i + 1]
        data[key] = value
    }
    createDataBase(dbFileName, data.toMap())
}

fun parseOverwriteArgs(args: List<String>) {
    if (args.isEmpty()) return printIncorrectArgsMsg()
    if (args.size % 2 != 1) return printIncorrectArgsMsg()
    val dbFileName = args[0]
    val data: MutableMap<String, String> = mutableMapOf()
    val kvArgs = args.drop(1)
    for (i in kvArgs.indices step 2) {
        val key = kvArgs[i]
        val value = kvArgs[i + 1]
        data[key] = value
    }
    overwriteDataBase(dbFileName, data.toMap())
}

fun parseOption(option: String, args: List<String>) {
    when (option) {
        "-r", "--read" -> parseReadArgs(args)
        "-c", "--create" -> parseCreateArgs(args)
        "-o", "--overwrite" -> parseOverwriteArgs(args)
        "-h", "--help" -> printHelpMsg()
        else -> printIncorrectArgsMsg()
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) return printIncorrectArgsMsg()

    val option = args.first()
    parseOption(option, args.drop(1).toList())
}
