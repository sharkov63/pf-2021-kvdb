import parseArgs.*
import exitFuncs.*
import operations.*



fun read(args: List<String>) {
    val readArgs = parseReadArgs(args) ?: return exitIncorrectArgs()
    readKeys(readArgs.dbFileName, readArgs.keys)
}

fun create(args: List<String>, overwrite: Boolean = false) {
    val createArgs = parseCreateArgs(args) ?: return exitIncorrectArgs()
    createDataBase(createArgs.dbFileName, createArgs.dataToWrite, overwrite)
}

fun add(args: List<String>, overwrite: Boolean = false) {
    val addArgs = parseAddArgs(args) ?: return exitIncorrectArgs()
    addToDataBase(addArgs.originalDBFileName, addArgs.newDBFileName, addArgs.dataToWrite, overwrite)
}

fun delete(args: List<String>) {
    val deleteArgs = parseDeleteArgs(args) ?: return exitIncorrectArgs()
    deleteKeysInDataBase(deleteArgs.originalDBFileName, deleteArgs.newDBFileName, deleteArgs.keys)
}

fun copy(args: List<String>) {
    val copyArgs = parseCopyArgs(args) ?: return exitIncorrectArgs()
    copyDataBase(copyArgs.fromDB, copyArgs.toDB)
}


fun main(args: Array<String>) {
    if (args.isEmpty()) return exitIncorrectArgs()

    val option = args.first()
    val argsNoOption = args.drop(1).toList()
    when (option) {
        "-r", "--read" -> read(argsNoOption)
        "-c", "--create" -> create(argsNoOption, false)
        "-co", "--createo", "--createOverwrite" -> create(argsNoOption, true)
        "-a", "--add" -> add(argsNoOption, false)
        "-ao", "--addo", "--addOverwrite" -> add(argsNoOption, true)
        "-d", "--delete" -> delete(argsNoOption)
        "-cp", "--copy" -> copy(argsNoOption)
        "-h", "--help" -> exitHelp()
        else -> exitIncorrectArgs()
    }
}
