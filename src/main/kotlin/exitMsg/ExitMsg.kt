package exitMsg

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