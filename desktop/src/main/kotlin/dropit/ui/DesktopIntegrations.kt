package dropit.ui

import dropit.ui.DesktopIntegrations.OperatingSystem.*
import dropit.ui.image.LinuxPngTransfer
import org.eclipse.swt.dnd.ByteArrayTransfer
import org.eclipse.swt.dnd.ImageTransfer
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DesktopIntegrations @Inject constructor() {
    enum class OperatingSystem {
        WINDOWS, LINUX, MACOSX
    }

    val currentOS = when (System.getProperty("os.name").toLowerCase()) {
        in "win" -> WINDOWS
        in "mac" -> MACOSX
        else -> LINUX
    }

    fun openFolderSelectFile(file: File) {
        when (currentOS) {
            WINDOWS -> ProcessBuilder("explorer.exe", "/select,$file").start()
            MACOSX -> ProcessBuilder("open", "-R", file.toString()).start()
            LINUX -> ProcessBuilder("xdg-open", file.parent).start()
        }
    }

    fun openFolder(folder: File) {
        when (currentOS) {
            WINDOWS -> ProcessBuilder("explorer.exe", folder.toString()).start()
            MACOSX -> ProcessBuilder("open", folder.toString()).start()
            LINUX -> ProcessBuilder("xdg-open", folder.toString()).start()
        }
    }

    fun openFile(file: File) {
        when (currentOS) {
            WINDOWS -> ProcessBuilder("rundll32.exe", "url.dll,FileProtocolHandler", file.toString()).start()
            MACOSX -> ProcessBuilder("open", file.toString()).start()
            LINUX -> ProcessBuilder("xdg-open", file.toString()).start()
        }
    }

    /**
     * Workaround for ImageTransfers not working properly on Linux
     */
    fun getImageTransfer(): ByteArrayTransfer =
        when (currentOS) {
            LINUX -> LinuxPngTransfer.instance
            else -> ImageTransfer.getInstance()
        }
}