package org.kompars.envelop.kotlinx.html

import kotlinx.html.*
import kotlinx.html.stream.*
import org.kompars.envelop.*

public fun EmailMessageBuilder.htmlBody(block: HTML.() -> Unit) {
    htmlBody(buildString { appendHTML().html(block = block) })
}
