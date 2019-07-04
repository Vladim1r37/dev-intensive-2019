package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val parts: List<String>? = fullName?.split("\\s+".toRegex())

        var firstName = parts?.getOrNull(0)
        var lastName = parts?.getOrNull(1)

        if (firstName == "" || firstName == " ") {
            firstName = null
            lastName = null
        }
        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " "): String {
        val rusLetters = "АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя"
        val engLetters = arrayOf(
            "A", "a", "B", "b", "V", "v", "G", "g", "D", "d", "E", "e", "E", "e", "Zh", "zh", "Z", "z",
            "I", "i", "I", "i", "K", "k", "L", "l", "M", "m", "N", "n", "O", "o", "P", "p", "R", "r",
            "S", "s", "T", "t", "U", "u", "F", "f", "H", "h", "C", "c", "Ch", "ch", "Sh", "sh", "Sh'",
            "sh'", "", "", "I", "i", "", "", "E", "e", "Yu", "yu", "Ya", "ya"
        )

        val parts: List<String> = payload.split("\\s+".toRegex())

        return buildString {
            for (string in parts) {
                for (char in string) {
                    val index = rusLetters.indexOf(char)
                    if (index == -1) {
                        append(char)
                    } else {
                        append(engLetters[index])
                    }
                }
                if (parts.indexOf(string) < parts.size - 1) append(divider)
            }
        }
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val firstInitial = parseInitial(firstName)
        val lastInitial = parseInitial(lastName)

        return if (lastInitial == null) firstInitial else firstInitial + lastInitial
    }

    private fun parseInitial(name: String?): String? {
        return if (name == null || name == "" || name == " ") null else name[0].toString().toUpperCase()
    }
}