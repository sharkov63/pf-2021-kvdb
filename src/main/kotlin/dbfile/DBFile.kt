package dbfile

import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * This package provides reading of and writing to a file in a custom format.
 * Files in this format shall have ".db" extension.
 *
 * The format is as follows.
 * The file contains an even number of strings, where each two strings form a (key, value) pair:
 * KEY1 VALUE1 KEY2 VALUE2 ...
 * Each string starts with a 32-byte integer N, which declares the length of the string.
 * Then, N next bytes decode the string.
 * String records are consecutive and have no separators in-between.
 */



/* Conversion */

private fun intToFourBytes(x: Int) = byteArrayOf(x.toByte(), (x shr 8).toByte(), (x shr 16).toByte(), (x shr 24).toByte())

private fun fourBytesToInt(bytes: ByteArray): Int {
    assert(bytes.size == 4)
    return bytes[0].toInt() or (bytes[1].toInt() shl 8) or (bytes[2].toInt() shl 16) or (bytes[3].toInt() shl 24)
}


/* Write */

private fun writeIntAsFourBytes(stream: OutputStream, x: Int) {
    stream.write(intToFourBytes(x))
}

private fun writeString(stream: OutputStream, s: String) {
    val bytes = s.toByteArray()
    writeIntAsFourBytes(stream, bytes.size)
    stream.write(bytes)
}


/**
 * Writes contents of [data] to a [file] in the mentioned format.
 *
 * If it cannot write to [file], throws an exception.
 */
fun writeDatabaseToFile(file: File, data: Map<String, String>) {
    file.createNewFile()
    if (!file.canWrite()) {
        throw Exception("Cannot write to a file at \"${file.path}\"")
    }
    val stream = file.outputStream()
    data.forEach { (key, value) ->
        writeString(stream, key)
        writeString(stream, value)
    }
    stream.close()
}


/* Read */

private fun readNBytes(stream: InputStream, N: Int): ByteArray {
    var bytes = byteArrayOf()
    for (i in 1..N) {
        val byte = stream.read()
        if (byte == -1) break
        bytes += byte.toByte()
    }
    return bytes
}

private fun readInt(stream: InputStream): Int? {
    val bytes = readNBytes(stream, 4)
    return if (bytes.size == 4)
        fourBytesToInt(bytes)
    else {
        stream.close()
        null
    }
}

private fun readString(stream: InputStream, byteCount: Int): String? {
    val bytes = readNBytes(stream, byteCount)
    return if (bytes.size == byteCount)
        bytes.decodeToString()
    else {
        stream.close()
        null
    }
}


/**
 * Reads database content from [file].
 *
 * Returns null if the database could not be read or the database doesn't match the format.
 * Otherwise, returns the data in a [Map].
 */
fun readDatabaseFromFile(file: File): Map<String, String>? {
    if (!file.canRead())
        return null

    val stream = file.inputStream()
    val data: MutableMap<String, String> = mutableMapOf()
    while (true) {
        val keyLen = readInt(stream) ?: break // read key length, otherwise EOF
        val key = readString(stream, keyLen) ?: return null // read key, otherwise invalid database
        val valueLen = readInt(stream) ?: return null // read value length, otherwise invalid database
        val value = readString(stream, valueLen) ?: return null // read value, otherwise invalid database
        if (data.containsKey(key))
            return null // invalid database: keys are not unique
        data[key] = value // new record
    }
    stream.close()
    return data
}