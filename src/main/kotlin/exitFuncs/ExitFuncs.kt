package exitFuncs

import kotlin.system.exitProcess

/**
 * This package contains exit functions, which print a certain message,
 * and then call [exitProcess] with a corresponding exit status.
 *
 * Exit status 0 means the program finished with no errors.
 * Exit status 1 means the program finished with an error.
 */


fun exitIncorrectArgs() {
    println("Incorrect arguments. Please use -h or --help option to see help message.")
    exitProcess(1)
}

fun exitHelp() {
    TODO("write help message")
}

fun exitDatabaseNotExist(dbPath: String) {
    println("Database at \"$dbPath\" does not exist!")
    exitProcess(1)
}

fun exitCannotReadDataBase(dbPath: String) {
    println("Cannot read database at \"$dbPath\"!")
    exitProcess(1)
}

fun exitCannotWriteToDataBase(dbPath: String) {
    println("Cannot write to database at \"$dbPath\"!")
    exitProcess(1)
}

fun exitInvalidDatabase(dbPath: String) {
    println("Database at \"$dbPath\" is not valid!")
    exitProcess(1)
}

fun exitDatabaseNotContainsKey(dbPath: String, key: String) {
    println("Database at \"$dbPath\" doesn't contain the key \"$key\"!")
    exitProcess(1)
}

fun exitDataBaseAlreadyExists(dbPath: String) {
    println("Database at \"$dbPath\" already exists! Use -o or --overwrite to allow overwriting the database.")
    exitProcess(1)
}

fun exitNothingToDo() {
    println("Nothing to do.")
    exitProcess(0)
}