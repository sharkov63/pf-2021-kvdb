import io.*
import java.io.File
import kotlin.system.exitProcess

fun printIncorrectArgsMsg() {
    println("Incorrect arguments. Please use -h or --help option to see help message.")
    exitProcess(1)
}

fun printHelpMsg() {
    TODO("write help message")
    exitProcess(0)
}

fun parseOption(option: String, args: List<String>) {
    when (option) {
        "-h", "--help" -> printHelpMsg()
        else -> printIncorrectArgsMsg()
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) return printIncorrectArgsMsg()

    val option = args.first()
    args.drop(1)
    parseOption(option, args.toList())
}
