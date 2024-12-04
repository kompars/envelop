package org.kompars.envelop.mox.model

import kotlinx.datetime.*
import kotlinx.serialization.*

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox@v0.0.13/webhook#Incoming)
 */
@Serializable
public data class Incoming(
    @SerialName("Version")
    val version: Int,
    @SerialName("From")
    val from: List<NameAddress> = emptyList(),
    @SerialName("To")
    val to: List<NameAddress> = emptyList(),
    @SerialName("CC")
    val cc: List<NameAddress> = emptyList(),
    @SerialName("BCC")
    val bcc: List<NameAddress> = emptyList(),
    @SerialName("ReplyTo")
    val replyTo: List<NameAddress> = emptyList(),
    @SerialName("Subject")
    val subject: String,
    @SerialName("MessageID")
    val messageId: String,
    @SerialName("InReplyTo")
    val inReplyTo: String?,
    @SerialName("References")
    val references: List<String> = emptyList(),
    @SerialName("Date")
    val date: Instant,
    @SerialName("Text")
    val text: String?,
    @SerialName("HTML")
    val html: String?,
    @SerialName("Structure")
    val structure: Structure,
    @SerialName("Meta")
    val meta: IncomingMeta,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox@v0.0.13/webhook#Structure)
 */
@Serializable
public data class Structure(
    @SerialName("ContentType")
    val contentType: String,
    @SerialName("ContentTypeParams")
    val contentTypeParams: Map<String, String> = emptyMap(),
    @SerialName("ContentID")
    val contentId: String,
    @SerialName("DecodedSize")
    val decodedSize: Int,
    @SerialName("Parts")
    val parts: List<Structure> = emptyList(),
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox@v0.0.13/webhook#IncomingMeta)
 */
@Serializable
public data class IncomingMeta(
    @SerialName("MsgID")
    val messageId: Int,
    @SerialName("MailFrom")
    val mailFrom: String,
    @SerialName("MailFromValidated")
    val mailFromValidated: Boolean,
    @SerialName("MsgFromValidated")
    val msgFromValidated: Boolean,
    @SerialName("RcptTo")
    val rcptTo: String,
    @SerialName("DKIMVerifiedDomains")
    val dkimVerifiedDomains: List<String>,
    @SerialName("RemoteIP")
    val remoteIp: String,
    @SerialName("Received")
    val received: Instant,
    @SerialName("MailboxName")
    val mailboxName: String,
    @SerialName("Automated")
    val automated: Boolean,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox@v0.0.13/webhook#Outgoing)
 */
@Serializable
public data class Outgoing(
    @SerialName("Version")
    val version: Int,
    @SerialName("Event")
    val event: OutgoingEvent,
    @SerialName("DSN")
    val dsn: Boolean,
    @SerialName("Suppressing")
    val suppressing: Boolean,
    @SerialName("QueueMsgID")
    val queueMessageId: Int,
    @SerialName("FromID")
    val fromId: String?,
    @SerialName("MessageID")
    val messageId: String,
    @SerialName("Subject")
    val subject: String,
    @SerialName("WebhookQueued")
    val webhookQueued: Instant,
    @SerialName("SMTPCode")
    val smtpCode: Int?,
    @SerialName("SMTPEnhancedCode")
    val smtpEnhancedCode: String?,
    @SerialName("Error")
    val error: String?,
    @SerialName("Extra")
    val extra: Map<String, String> = emptyMap(),
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox@v0.0.13/webhook#OutgoingEvent)
 */
@Serializable
public enum class OutgoingEvent {
    @SerialName("delivered")
    Delivered,

    @SerialName("suppressed")
    Suppressed,

    @SerialName("delayed")
    Delayed,

    @SerialName("failed")
    Failed,

    @SerialName("relayed")
    Relayed,

    @SerialName("expanded")
    Expanded,

    @SerialName("canceled")
    Canceled,

    @SerialName("unrecognized")
    Unrecognized,
}
