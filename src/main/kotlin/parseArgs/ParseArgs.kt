package parseArgs

/**
 * This package contains functions, which would parse arguments from command line for different options.
 *
 * Each function returns null if given arguments are not valid.
 * Otherwise, it returns correct data for upcoming operations.
 */


/* Aux functions */

private fun parseKeyValueSequence(kvArgs: List<String>): Map<String, String>? {
    if (kvArgs.size % 2 == 1)
        return null // Wrong parity

    val data: MutableMap<String, String> = mutableMapOf()
    for (i in kvArgs.indices step 2) {
        val key = kvArgs[i]
        val value = kvArgs[i + 1]
        if (data.containsKey(key) && data[key] != value)
            return null // Contradiction
        data[key] = value
    }
    return data
}

private fun parseTwoDBFileNames(dbFileName1: String, dbFileName2: String): Pair<String, String> {
    return Pair(
        dbFileName1,
        if (dbFileName2 != "!")
            dbFileName2
        else
            dbFileName1
    )
}


/**
 * Read args.
 * The format is as follows:
 *
 * DATABASE_FILE KEY1 KEY2 KEY3 ...
 *
 * Keys do not have to be distinct. The number of keys may be zero.
 */

data class ReadArgs(val dbFileName: String, val keys: List<String>)

fun parseReadArgs(args: List<String>): ReadArgs? {
    if (args.isEmpty())
        return null

    val dbFileName = args[0]
    return ReadArgs(dbFileName, args.drop(1).toList())
}


/**
 * Create args.
 * The format is as follows:
 *
 * DATABASE_FILE KEY1 VALUE1 KEY2 VALUE2 ...
 *
 * Keys may coincide, but only if corresponding values are equal.
 * The number of key-value tokens has to be even (it must end with a value)
 * There may be zero key-value pairs
 */

data class CreateArgs(val dbFileName: String, val dataToWrite: Map<String, String>)

fun parseCreateArgs(args: List<String>): CreateArgs? {
    if (args.isEmpty())
        return null
    if (args.size % 2 != 1)
        return null // Wrong parity

    val dbFileName = args[0]
    val data = parseKeyValueSequence(args.drop(1)) ?: return null
    return CreateArgs(dbFileName, data)
}


/**
 * Add args.
 * The format is as follows:
 *
 * ORIGINAL_DATABASE_FILE NEW_DATABASE_FILE KEY1 VALUE1 KEY2 VALUE2 ...
 *
 * Keys may coincide, but only if corresponding values are equal.
 * The number of key-value tokens has to be even (it must end with a value)
 * There may be zero key-value pairs
 *
 * NEW_DATABASE_FILE might be equal to "!",
 * in which case it is considered equal to ORIGINAL_DATABASE_FILE.
 */

data class AddArgs(val originalDBFileName: String, val newDBFileName: String, val dataToWrite: Map<String, String>)

fun parseAddArgs(args: List<String>): AddArgs? {
    if (args.size < 2)
        return null
    if (args.size % 2 != 0)
        return null // Wrong parity

    val (originalDBFileName, newDBFileName) = parseTwoDBFileNames(args[0], args[1])
    val data = parseKeyValueSequence(args.drop(2)) ?: return null
    return AddArgs(originalDBFileName, newDBFileName, data)
}


/**
 * Delete args.
 * The format is as follows:
 *
 * ORIGINAL_DATABASE_FILE NEW_DATABASE_FILE KEY1 KEY2 KEY3 ...
 *
 * Keys do not have to be distinct. The number of keys may be zero.
 *
 * NEW_DATABASE_FILE might be equal to "!",
 * in which case it is considered equal to ORIGINAL_DATABASE_FILE.
 */

data class DeleteArgs(val originalDBFileName: String, val newDBFileName: String, val keys: List<String>)

fun parseDeleteArgs(args: List<String>): DeleteArgs? {
    if (args.size < 2)
        return null

    val (originalDBFileName, newDBFileName) = parseTwoDBFileNames(args[0], args[1])
    val keys = args.drop(2)
    return DeleteArgs(originalDBFileName, newDBFileName, keys)
}


/**
 * Copy args.
 * The format is as follows:
 *
 * FROM_DATABASE_FILE TO_DATABASE_FILE
 *
 * TO_DATABASE_FILE might be equal to "!",
 * in which case it is considered equal to FROM_DATABASE_FILE.
 *
 * Everything after TO_DATABASE_FILE is ignored.
 */

data class CopyArgs(val fromDB: String, val toDB: String)

fun parseCopyArgs(args: List<String>): CopyArgs? {
    if (args.size < 2)
        return null

    val (fromDB, toDB) = parseTwoDBFileNames(args[0], args[1])
    return CopyArgs(fromDB, toDB)
}