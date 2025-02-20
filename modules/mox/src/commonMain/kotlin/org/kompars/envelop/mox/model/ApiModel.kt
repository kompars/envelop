package org.kompars.envelop.mox.model

import io.ktor.http.*
import kotlinx.datetime.*
import kotlinx.serialization.*
import org.kompars.envelop.common.*

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#Error)
 */
@Serializable
public data class Error(
    @SerialName("Code")
    val code: String,
    @SerialName("Message")
    val message: String,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#File)
 */
@Serializable
public data class File(
    @SerialName("Name")
    val name: String? = null,
    @SerialName("ContentType")
    val contentType: String? = null,
    @SerialName("ContentID")
    val contentId: String? = null,
    @SerialName("Data")
    val data: String,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#Message)
 */
@Serializable
public data class Message(
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
    @SerialName("MessageID")
    val messageId: EmailMessageId? = null,
    @SerialName("References")
    val reference: List<EmailMessageId> = emptyList(),
    @SerialName("Date")
    val date: Instant? = null,
    @SerialName("Subject")
    val subject: String? = null,
    @SerialName("Text")
    val text: String? = null,
    @SerialName("HTML")
    val html: String? = null,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageDeleteRequest)
 */
@Serializable
public data class MessageDeleteRequest(
    @SerialName("MsgID")
    val messageId: Int,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageDeleteResult)
 */
@Serializable
public data object MessageDeleteResult


/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageFlagsAddRequest)
 */
@Serializable
public data class MessageFlagsAddRequest(
    @SerialName("MsgID")
    val messageId: Int,
    @SerialName("Flags")
    val flags: List<String>,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageFlagsAddResult)
 */
@Serializable
public data object MessageFlagsAddResult

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageFlagsRemoveRequest)
 */
@Serializable
public data class MessageFlagsRemoveRequest(
    @SerialName("MsgID")
    val messageId: Int,
    @SerialName("Flags")
    val flags: List<String>,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageFlagsRemoveResult)
 */
@Serializable
public data object MessageFlagsRemoveResult

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageGetRequest)
 */
@Serializable
public data class MessageGetRequest(
    @SerialName("MsgID")
    val messageId: Int,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageGetResult)
 */
@Serializable
public data class MessageGetResult(
    @SerialName("Message")
    val message: Message,
    @SerialName("Structure")
    val structure: Structure,
    @SerialName("Meta")
    val meta: MessageMeta,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageMeta)
 */
@Serializable
public data class MessageMeta(
    @SerialName("Size")
    val size: Int,
    @SerialName("DSN")
    val dsn: Boolean,
    @SerialName("Flags")
    val flags: List<String>,
    @SerialName("MailFrom")
    val mailFrom: EmailAddress,
    @SerialName("MailFromValidated")
    val mailFromValidated: Boolean,
    @SerialName("RcptTo")
    val rcptTo: String,
    @SerialName("MsgFrom")
    val msgFrom: EmailAddress,
    @SerialName("MsgFromValidated")
    val msgFromValidated: Boolean,
    @SerialName("DKIMVerifiedDomains")
    val dkimVerifiedDomains: List<String>,
    @SerialName("RemoteIP")
    val remoteIp: String,
    @SerialName("MailboxName")
    val mailboxName: String,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageMoveRequest)
 */
@Serializable
public data class MessageMoveRequest(
    @SerialName("MsgID")
    val messageId: Int,
    @SerialName("DestMailboxName")
    val destMailboxName: String,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageMoveResult)
 */
@Serializable
public data object MessageMoveResult

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessagePartGetRequest)
 */
@Serializable
public data class MessagePartGetRequest(
    @SerialName("MsgID")
    val messageId: Int,
    @SerialName("PartPath")
    val partPath: List<Int>,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#MessageRawGetRequest)
 */
@Serializable
public data class MessageRawGetRequest(
    @SerialName("MsgID")
    val messageId: Int,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox@v0.0.13/webhook#NameAddress)
 */
@Serializable
public data class NameAddress(
    @SerialName("Name")
    val name: String? = null,
    @SerialName("Address")
    val address: EmailAddress,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#SendRequest)
 */
@Serializable
public data class SendRequest(
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
    @SerialName("MessageID")
    val messageId: EmailMessageId? = null,
    @SerialName("References")
    val references: List<EmailMessageId> = emptyList(),
    @SerialName("Date")
    val date: Instant? = null,
    @SerialName("Subject")
    val subject: String? = null,
    @SerialName("Text")
    val text: String? = null,
    @SerialName("HTML")
    val html: String? = null,
    @SerialName("Extra")
    val extra: Map<String, String> = emptyMap(),
    @SerialName("Headers")
    val headers: List<List<String>> = emptyList(),
    @SerialName("AlternativeFiles")
    val alternativeFiles: List<File> = emptyList(),
    @SerialName("InlineFiles")
    val inlineFiles: List<File> = emptyList(),
    @SerialName("AttachedFiles")
    val attachedFiles: List<File> = emptyList(),
    @SerialName("RequireTLS")
    val requireTls: Boolean = false,
    @SerialName("FutureRelease")
    val futureRelease: Instant? = null,
    @SerialName("SaveSent")
    val saveSent: Boolean = false,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#SendResult)
 */
@Serializable
public data class SendResult(
    @SerialName("MessageID")
    val messageId: EmailMessageId,
    @SerialName("Submissions")
    val submissions: List<Submission>,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#Submission)
 */
@Serializable
public data class Submission(
    @SerialName("Address")
    val address: EmailAddress,
    @SerialName("QueueMsgID")
    val queueMessageId: Int,
    @SerialName("FromID")
    val fromId: String,
)

/**
 * [Mox documentation](https://pkg.go.dev/github.com/mjl-/mox/webapi#hdr-Webapi_examples)
 */
public class Part(
    public val partType: PartType,
    public val content: ByteArray,
    public val name: String? = null,
    public val contentType: ContentType? = null,
    public val contentId: String? = null,
)

public enum class PartType(public val key: String) {
    AlternativeFile("alternativefile"),
    InlineFile("inlinefile"),
    AttachedFile("attachedfile")
}
