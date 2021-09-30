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
    println("kvdb.jar is a simple console key-value database interface.                     ")
    println("                                                                               ")
    println("Usage:                                                                         ")
    println("java -jar kvdb.jar [option] [args...]                                          ")
    println("                                                                               ")
    println("Possible options:                                                              ")
    println("-r database [key1] [key2] ...                      get values of specified keys")
    println("-c database [key1] [value1] ...                           create a new database")
    println("                                                            with specified data")
    println("-co database [key1] [value1] ...              same as -c, but overwrite already")
    println("                                                    existing file, if necessary")
    println("-a database1 database2 [key1] [value1] ...   add key-value records to database1")
    println("                                               and save the result to database2")
    println("                                              records with keys already present")
    println("                                                       in database1 are omitted")
    println("-ao database1 database2 [key1] [value1] ...           same as -a, but keys that")
    println("                                                   already present in database1")
    println("                                                     overwrite existing records")
    println("-d database1 database2 [key1] [key2] ...             delete keys from database1")
    println("                                               and save the result to database2")
    println("-cp database1 database2                             copy database1 to database2")
    println("-h, --help                                                   print this message")
    println("                                                                               ")
    println("Key-value pairs in -c, -co, -a, -ao options must not contradict each other.    ")
    println("                                                                               ")
    println("In options -a, -ao, -d and -cp [database2] can be replaced with !.             ")
    println("In this case the result is written back to [database1]. For example:           ")
    println("-a database1 ! [key1] [value1] ...                                             ")
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
    println("Database at \"$dbPath\" already exists! Use -co or --createo or --createOverwrite options to allow overwriting the database.")
    exitProcess(1)
}

fun exitNothingToDo() {
    println("Nothing to do.")
    exitProcess(0)
}