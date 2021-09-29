import java.io.File
import kotlin.test.*
import io.*

internal class IOTests {
    private val filename = "testData/io/testDatabase.db"

    private fun ioTestTemplate(data: Map<String, String>) {
        val file = File(filename)
        writeDatabaseToFile(file, data)
        val readedData = readDatabaseFromFile(file)
        assertEquals(data, readedData)
        assert(file.delete())
    }

    @Test
    fun simpleTest() {
        val data = mapOf(
            "foo" to "bar",
            "zero" to "0",
            "one" to "1",
        )
        ioTestTemplate(data)
    }

    @Test
    fun emptyString() {
        val data = mapOf(
            "" to "emptyString",
            "empty string" to "",
        )
        ioTestTemplate(data)
    }

    @Test
    fun emptyDataBase() {
        val data: Map<String, String> = mapOf()
        ioTestTemplate(data)
    }

    @Test
    fun basicLatin() {
        val data = mapOf(
            "!\"#$%&'()*+,-./:;<=>?" to "@[\\]^_`{|}~",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" to "abcdefghijklmnopqrstuvwxyz",
        )
        ioTestTemplate(data)
    }

    @Test
    fun latin1Supplement() {
        val data = mapOf(
            "¡¢£¤¥¦§¨©ª«¬\u00AD®¯°±²³´µ¶·¸¹º»¼½¾¿" to "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜ",
            "ÝÞßàáâãäåæçèéêëìíîï" to "ðñòóôõö÷øùúûüýþÿ",
        )
        ioTestTemplate(data)
    }

    @Test
    fun cyrillic() {
        val data = mapOf(
            "русский" to "russian",
            "language" to "язык",
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" to "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ",
            "Ѐ Ё Ђ Ѓ Є Ѕ І Ї Ј Љ Њ Ћ Ќ Ѝ Ў Џ" to "ѐ ё ђ ѓ є ѕ і ї ј љ њ ћ ќ ѝ ў џ",
        )
        ioTestTemplate(data)
    }

    @Test
    fun cjkCompatibility() {
        val data = mapOf(
            "㌀ ㌁ ㌂ ㌃ ㌄ ㌅ ㌆ ㌇ ㌈ ㌉ ㌊ ㌋ ㌌ ㌍ ㌎ ㌏ ㌐ ㌑ ㌒ ㌓ ㌔ ㌕ ㌖ ㌗" to "㌘ ㌙ ㌚ ㌛ ㌜ ㌝ ㌞ ㌟ ㌠ ㌡ ㌢ ㌣ ㌤ ㌥ ㌦ ㌧ ㌨ ㌩ ㌪ ㌫ ㌬ ㌭ ㌮ ㌯ ㌰ ㌱",
            "㌲ ㌳ ㌴ ㌵ ㌶ ㌷ ㌸ ㌹ ㌺ ㌻ ㌼ ㌽ ㌾ ㌿ ㍀ ㍁ ㍂ ㍃ ㍄ ㍅ ㍆ ㍇ ㍈ ㍉ ㍊" to "㍋ ㍌ ㍍ ㍎ ㍏ ㍐ ㍑ ㍒ ㍓ ㍔ ㍕ ㍖ ㍗ ㍘ ㍙ ㍚ ㍛ ㍜",
            "㍝ ㍞ ㍟ ㍠ ㍡ ㍢ ㍣ ㍤ ㍥ ㍦ ㍧ ㍨ ㍩ ㍪ ㍫ ㍬" to "㍭ ㍮ ㍯ ㍰ ㍱ ㍲ ㍳ ㍴ ㍵ ㍶ ㍻ ㍼ ㍽ ㍾ ㍿ ㎀ ㎁ ㎂ ㎃",
        )
        ioTestTemplate(data)
    }
}
