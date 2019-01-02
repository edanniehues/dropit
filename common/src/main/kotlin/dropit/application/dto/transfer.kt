package dropit.application.dto

import java.io.Serializable
import java.util.*

data class TransferRequest(val name: String? = null, val sendToClipboard: Boolean? = false, val files: List<FileRequest> = emptyList()) : Serializable

data class FileRequest(
        val id: String? = null,
        val fileName: String? = null,
        val mimeType: String? = null,
        val fileSize: Long? = null) : Serializable

data class PendingTransfer(val id: String? = null, val items: List<String> = emptyList()) : Serializable

data class TransferInfo(val status: TransferStatus? = null, val files: Map<String, FileStatus> = emptyMap()) : Serializable

enum class TransferStatus {
    PENDING, FINISHED
}

enum class FileStatus {
    PENDING, FAILED, FINISHED
}

data class SentFileId(val id: UUID = UUID.randomUUID())

data class DownloadStatus(val id: UUID = UUID.randomUUID(), val bytes: Long = 0L)