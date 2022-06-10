package online.viestudio.paperkit.presentation

import com.intellij.framework.library.LibraryVersionProperties
import com.intellij.openapi.roots.libraries.LibraryKind
import com.intellij.openapi.roots.libraries.LibraryKindRegistry
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider
import com.intellij.openapi.vfs.VirtualFile
import online.viestudio.paperkit.asset.MediaAssets
import javax.swing.Icon

private const val KIND_ID = "paper-kit"
private const val GROUP_ID = "com.github.paper-kit"
private const val ARTIFACT_ID = "paper-kit"
private const val ARTIFACT_PATH = "$GROUP_ID/$ARTIFACT_ID"

val paperKitLibraryKind: LibraryKind
    get() = LibraryKindRegistry.getInstance().findKindById(KIND_ID) ?: LibraryKind.create(KIND_ID)

class PaperKitPresentationProvider : LibraryPresentationProvider<LibraryVersionProperties>(paperKitLibraryKind) {

    override fun detect(classesRoots: MutableList<VirtualFile>): LibraryVersionProperties? {
        for (classesRoot in classesRoots) {
            val path = classesRoot.path
            if (!path.contains(ARTIFACT_PATH)) continue
            val version = path.substringAfter(ARTIFACT_PATH).substringBefore(ARTIFACT_ID)
            return LibraryVersionProperties(version)
        }
        return null
    }

    override fun getIcon(properties: LibraryVersionProperties?): Icon = MediaAssets.PAPER_KIT_ICON
}