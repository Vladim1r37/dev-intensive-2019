package ru.skillbranch.devintensive.extensions

fun String.truncate(limit: Int = 16): String {
    var resultString = this.replace(" +$".toRegex(), "")
    return if (resultString.length > limit) {
        resultString = resultString.substring(0, limit)
        while (resultString.endsWith(" ")) resultString = resultString.dropLast(1)
        "$resultString..."
    } else resultString
}

fun String.stripHtml(): String {
    return this.stripTags()
        .stripEscapeSequences()
        .stripSpaces()
}

private fun String.stripSpaces(): String {
    return this.replace("\\s{2,}".toRegex(), " ")
}

private fun String.stripEscapeSequences(): String {
    return this.replace("&(amp|lt|gt|#0?39|quot);".toRegex(), "")
}

private fun String.stripTags(): String {
    return this.replace("<.*?>".toRegex(), "")
}
