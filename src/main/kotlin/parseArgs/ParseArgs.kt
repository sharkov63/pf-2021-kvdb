package parseArgs

/* Read args */

data class ReadArgs(val dbFileName: String, val keys: List<String>)

fun parseReadArgs(args: List<String>): ReadArgs? {
    if (args.isEmpty()) return null
    val dbFileName = args[0]
    return ReadArgs(dbFileName, args.drop(1).toList())
}


/* Modify args */

data class ModifyArgs(val dbFileName: String, val dataToWrite: Map<String, String>)

fun parseModifyArgs(args: List<String>): ModifyArgs? {
    if (args.isEmpty()) return null
    if (args.size % 2 != 1) return null
    val dbFileName = args[0]
    val data: MutableMap<String, String> = mutableMapOf()
    val kvArgs = args.drop(1)
    for (i in kvArgs.indices step 2) {
        val key = kvArgs[i]
        val value = kvArgs[i + 1]
        if (data.containsKey(key) && data[key] != value)
            return null
        data[key] = value
    }
    return ModifyArgs(dbFileName, data.toMap())
}

/* Write args */

data class WriteArgs(val originalDBFileName: String, val newDBFileName: String, val dataToWrite: Map<String, String>)

fun parseWriteArgs(args: List<String>): WriteArgs? {
    if (args.size < 2) return null
    if (args.size % 2 != 0) return null
    val originalDBFileName = args[0]
    val newDBFileName = if (args[1] == "!") originalDBFileName else args[1]
    val data: MutableMap<String, String> = mutableMapOf()
    val kvArgs = args.drop(2)
    for (i in kvArgs.indices step 2) {
        val key = kvArgs[i]
        val value = kvArgs[i + 1]
        if (data.containsKey(key) && data[key] != value)
            return null
        data[key] = value
    }
    return WriteArgs(originalDBFileName, newDBFileName, data.toMap())
}