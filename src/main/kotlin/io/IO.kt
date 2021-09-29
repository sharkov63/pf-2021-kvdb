package io

import java.io.File
import java.io.InputStream
import java.io.OutputStream



fun intToFourBytes(x: Int) = byteArrayOf(x.toByte(), (x shr 8).toByte(), (x shr 16).toByte(), (x shr 24).toByte())
fun fourBytesToInt(bytes: ByteArray): Int {
    assert(bytes.size == 4)
    return bytes[0].toInt() or (bytes[1].toInt() shl 8) or (bytes[2].toInt() shl 16) or (bytes[3].toInt() shl 24)
}

fun writeIntAsFourBytes(stream: OutputStream, x: Int) {
    stream.write(intToFourBytes(x))
}

fun writeString(stream: OutputStream, s: String) {
    val bytes = s.toByteArray()
    writeIntAsFourBytes(stream, bytes.size)
    stream.write(bytes)
}

fun writeDatabaseToFile(file: File, data: Map<String, String>) {
    val stream = file.outputStream()
    data.forEach { (key, value) ->
        writeString(stream, key)
        writeString(stream, value)
    }
    stream.close()
}

fun readInt(stream: InputStream): Int? {
    val bytes = stream.readNBytes(4)
    return if (bytes.size == 4)
        fourBytesToInt(bytes)
    else {
        stream.close()
        null
    }
}

fun readString(stream: InputStream, byteCount: Int): String? {
    val bytes = stream.readNBytes(byteCount)
    return if (bytes.size == byteCount)
        bytes.decodeToString()
    else {
        stream.close()
        null
    }
}

fun readDatabaseFromFile(file: File): Map<String, String>? {
    val stream = file.inputStream()
    val data: MutableMap<String, String> = mutableMapOf()
    while (true) {
        val keyLen = readInt(stream) ?: break
        val key = readString(stream, keyLen) ?: return null
        val valueLen = readInt(stream) ?: return null
        val value = readString(stream, valueLen) ?: return null
        data[key] = value
    }
    stream.close()
    return data.toMap()
}