import dbfile.*
import java.io.File
import kotlin.system.exitProcess

fun printIncorrectArgsMsg() {
    throw Exception("Incorrect arguments. Please use -h or --help option to see help message.")
}

fun printHelpMsg() {
    TODO("write help message")
}

fun printDatabaseNotExistMsg(dbPath: String) {
    throw Exception("Database at \"$dbPath\" does not exist!")
}

fun printInvalidDatabaseMsg(dbPath: String) {
    throw Exception("\"Database at \\\"$dbPath\\\" is not valid!\"")
}

fun printDatabaseNotContainsKey(dbPath: String, key: String) {
    throw Exception("Database at \"$dbPath\" doesn't contain the key \"$key\".")
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
    parseOption(option, args.drop(1).toList())
}
