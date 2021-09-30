import parseArgs.*
import exitMsg.*
import operations.*



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
