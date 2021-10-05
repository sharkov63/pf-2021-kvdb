package unitTests

import java.io.File
import kotlin.test.*
import dbfile.*

internal class DBFileTests {
    private val filename = "testData/dbfile/testDatabase.db"

    private fun dbFileTestTemplate(data: Map<String, String>) {
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
        dbFileTestTemplate(data)
    }

    @Test
    fun emptyString() {
        val data = mapOf(
            "" to "emptyString",
            "empty string" to "",
        )
        dbFileTestTemplate(data)
    }

    @Test
    fun emptyDataBase() {
        val data: Map<String, String> = mapOf()
        dbFileTestTemplate(data)
    }

    @Test
    fun basicLatin() {
        val data = mapOf(
            "!\"#$%&'()*+,-./:;<=>?" to "@[\\]^_`{|}~",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" to "abcdefghijklmnopqrstuvwxyz",
        )
        dbFileTestTemplate(data)
    }

    @Test
    fun latin1Supplement() {
        val data = mapOf(
            "¡¢£¤¥¦§¨©ª«¬\u00AD®¯°±²³´µ¶·¸¹º»¼½¾¿" to "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜ",
            "ÝÞßàáâãäåæçèéêëìíîï" to "ðñòóôõö÷øùúûüýþÿ",
        )
        dbFileTestTemplate(data)
    }

    @Test
    fun cyrillic() {
        val data = mapOf(
            "русский" to "russian",
            "language" to "язык",
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" to "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ",
            "Ѐ Ё Ђ Ѓ Є Ѕ І Ї Ј Љ Њ Ћ Ќ Ѝ Ў Џ" to "ѐ ё ђ ѓ є ѕ і ї ј љ њ ћ ќ ѝ ў џ",
        )
        dbFileTestTemplate(data)
    }

    @Test
    fun cjkCompatibility() {
        val data = mapOf(
            "㌀ ㌁ ㌂ ㌃ ㌄ ㌅ ㌆ ㌇ ㌈ ㌉ ㌊ ㌋ ㌌ ㌍ ㌎ ㌏ ㌐ ㌑ ㌒ ㌓ ㌔ ㌕ ㌖ ㌗" to "㌘ ㌙ ㌚ ㌛ ㌜ ㌝ ㌞ ㌟ ㌠ ㌡ ㌢ ㌣ ㌤ ㌥ ㌦ ㌧ ㌨ ㌩ ㌪ ㌫ ㌬ ㌭ ㌮ ㌯ ㌰ ㌱",
            "㌲ ㌳ ㌴ ㌵ ㌶ ㌷ ㌸ ㌹ ㌺ ㌻ ㌼ ㌽ ㌾ ㌿ ㍀ ㍁ ㍂ ㍃ ㍄ ㍅ ㍆ ㍇ ㍈ ㍉ ㍊" to "㍋ ㍌ ㍍ ㍎ ㍏ ㍐ ㍑ ㍒ ㍓ ㍔ ㍕ ㍖ ㍗ ㍘ ㍙ ㍚ ㍛ ㍜",
            "㍝ ㍞ ㍟ ㍠ ㍡ ㍢ ㍣ ㍤ ㍥ ㍦ ㍧ ㍨ ㍩ ㍪ ㍫ ㍬" to "㍭ ㍮ ㍯ ㍰ ㍱ ㍲ ㍳ ㍴ ㍵ ㍶ ㍻ ㍼ ㍽ ㍾ ㍿ ㎀ ㎁ ㎂ ㎃",
        )
        dbFileTestTemplate(data)
    }
}
