package org.kompars.envelop.blob

public interface Blob

public class InMemoryBlob(public val byteArray: ByteArray) : Blob {
    public val size: Int get() = byteArray.size
}

public interface BlobStorage {
    public suspend fun write(content: ByteArray): Blob
    public suspend fun read(blob: Blob): ByteArray
}

public object InMemoryBlobStorage : BlobStorage {
    override suspend fun write(content: ByteArray): Blob {
        return InMemoryBlob(content)
    }

    override suspend fun read(blob: Blob): ByteArray {
        return (blob as InMemoryBlob).byteArray
    }
}
