import parseArgs.*
import exitFuncs.*
import operations.*



fun read(args: List<String>) {
    val readArgs = parseReadArgs(args) ?: return exitIncorrectArgs()
    readKeys(readArgs.dbFileName, readArgs.keys)
}

fun readFound(args: List<String>) {
    val readArgs = parseReadArgs(args) ?: return exitIncorrectArgs()
    readFoundKeys(readArgs.dbFileName, readArgs.keys)
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

fun merge(args: List<String>, overwrite: Boolean = false) {
    val mergeArgs = parseMergeArgs(args) ?: return exitIncorrectArgs()
    mergeDataBases(mergeArgs.db1, mergeArgs.db2, mergeArgs.dbOut, overwrite)
}


fun main(args: Array<String>) {
    if (args.isEmpty()) return exitIncorrectArgs()

    val option = args.first()
    val argsNoOption = args.drop(1).toList()
    when (option) {
        "-r", "--read" -> read(argsNoOption)
        "-rf", "--readf", "--readFound" -> readFound(argsNoOption)
        "-c", "--create" -> create(argsNoOption, false)
        "-co", "--createo", "--createOverwrite" -> create(argsNoOption, true)
        "-a", "--add" -> add(argsNoOption, false)
        "-ao", "--addo", "--addOverwrite" -> add(argsNoOption, true)
        "-d", "--delete" -> delete(argsNoOption)
        "-cp", "--copy" -> copy(argsNoOption)
        "-m", "--merge" -> merge(argsNoOption, false)
        "-mo", "--mergeo", "--mergeOverwrite" -> merge(argsNoOption, true)
        "-h", "--help" -> exitHelp()
        else -> exitIncorrectArgs()
    }
}
