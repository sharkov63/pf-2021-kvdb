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

fun parseOption(option: String, args: List<String>) {
    when (option) {
        "-r", "--read" -> read(args)
        "-c", "--create" -> create(args, false)
        "-co", "--createo", "--createOverwrite" -> create(args, true)
        "-a", "--add" -> add(args, false)
        "-ao", "--addo", "--addOverwrite" -> add(args, true)
        "-d", "--delete" -> delete(args)
        "-cp", "--copy" -> copy(args)
        "-h", "--help" -> exitHelp()
        else -> exitIncorrectArgs()
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) return exitIncorrectArgs()

    val option = args.first()
    parseOption(option, args.drop(1).toList())
}
