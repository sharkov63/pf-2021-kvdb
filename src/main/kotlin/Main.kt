import dbfile.*
import java.io.File
import kotlin.system.exitProcess

fun printIncorrectArgsMsg() {
    println("Incorrect arguments. Please use -h or --help option to see help message.")
    exitProcess(1)
}

fun printHelpMsg() {
    TODO("write help message")
    exitProcess(0)
}

fun printDatabaseNotExistMsg(dbPath: String) {
    println("Database at \"$dbPath\" does not exist!")
    exitProcess(1)
}

fun printInvalidDatabaseMsg(dbPath: String) {
    println("Database at \"$dbPath\" is not valid!")
    exitProcess(1)
}

fun printDatabaseNotContainsKey(dbPath: String, key: String) {
    println("Database at \"$dbPath\" doesn't contain the key \"$key\".")
    exitProcess(1)
}

fun readKeys(dbFileName: String, keys: List<String>) {
    val dbFile = File(dbFileName)
    if (!dbFile.exists()) {
        return printDatabaseNotExistMsg(dbFile.path)
    }
    val data = readDatabaseFromFile(dbFile) ?: return printInvalidDatabaseMsg(dbFile.path)
    keys.forEach { key ->
        if (!data.containsKey(key)) {
            return printDatabaseNotContainsKey(dbFile.path, key)
        }
        println(data[key])
    }
}

fun parseReadArgs(args: List<String>) {
    if (args.size < 2) return printIncorrectArgsMsg()
    val dbFileName = args[0]
    args.drop(1)
    readKeys(dbFileName, args.toList())
}

fun parseOption(option: String, args: List<String>) {
    when (option) {
        "-r", "--read" -> parseReadArgs(args)
        "-h", "--help" -> printHelpMsg()
        else -> printIncorrectArgsMsg()
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) return printIncorrectArgsMsg()

    val option = args.first()
    args.drop(1)
    parseOption(option, args.toList())
}
