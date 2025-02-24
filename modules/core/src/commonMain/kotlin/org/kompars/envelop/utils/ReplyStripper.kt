package org.kompars.envelop.utils

public object ReplyStripper {
    private val delimiters: List<Regex> = listOf(
        "--- Original Message ---".toRegex(),
        "---------Original Message---------".toRegex(),
        "---------- Původní e-mail ----------".toRegex(),
        ".* odesílatel .* napsal:".toRegex(),
        "On .* wrote:".toRegex(),
        "Dne .* napsal.*:".toRegex(),
        "\\d{1,2}\\..*\\d{1,2}\\..*\\d{4} v \\d{1,2}:\\d{2}, .*<.*@.*>\\:".toRegex(),
        "From: .*".toRegex(),
        "\\*\\*From:\\*\\* .*".toRegex(),
        "\\*\\*Od:\\*\\* .*".toRegex(),
    )

    public fun strip(text: String): String {
        for (delimiter in delimiters) {
            val match = delimiter.find(text)

            if (match != null) {
                return text.substring(0, match.range.first).trim()
            }
        }

        return text
    }
}
