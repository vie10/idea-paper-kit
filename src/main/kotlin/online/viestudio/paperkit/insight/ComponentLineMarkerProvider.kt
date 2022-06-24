package online.viestudio.paperkit.insight

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.findParentOfType
import online.viestudio.paperkit.asset.MediaAssets.COMMAND_ICON
import online.viestudio.paperkit.asset.MediaAssets.CONFIG_ICON
import online.viestudio.paperkit.asset.MediaAssets.LISTENER_ICON
import online.viestudio.paperkit.asset.MediaAssets.PLUGIN_ICON
import online.viestudio.paperkit.facet.isNotInFacetModule
import online.viestudio.paperkit.util.isCommandImplementation
import online.viestudio.paperkit.util.isConfigImplementation
import online.viestudio.paperkit.util.isListenerImplementation
import online.viestudio.paperkit.util.isPluginImplementation
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.psi.KtClass
import javax.swing.Icon

class ComponentLineMarkerProvider : LineMarkerProviderDescriptor() {

    override fun getName(): String = "PaperKit component line marker"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = with(element) {
        if (element.isNotInFacetModule()) return null
        if (elementType !is KtKeywordToken) return null
        val context = element.context ?: return null

        fun PsiElement.className() = findParentOfType<KtClass>()?.name ?: ""

        return if (context.isPluginImplementation()) {
            element.newLineMarkerInfo(PLUGIN_ICON, "paperkit plugin indicator") {
                "PaperKit plugin implementation ${it.className()}"
            }
        } else if (context.isCommandImplementation()) {
            element.newLineMarkerInfo(COMMAND_ICON, "paperkit command indicator") {
                "PaperKit command implementation ${it.className()}"
            }
        } else if (context.isListenerImplementation()) {
            element.newLineMarkerInfo(LISTENER_ICON, "paperkit listener indicator") {
                "PaperKit listener implementation ${it.className()}"
            }
        } else if (context.isConfigImplementation()) {
            element.newLineMarkerInfo(CONFIG_ICON, "paperkit config indicator") {
                "PaperKit config implementation ${it.className()}"
            }
        } else null
    }

    private fun PsiElement.newLineMarkerInfo(icon: Icon, accessibleName: String, tooltip: (PsiElement) -> String) =
        LineMarkerInfo<PsiElement>(
            this,
            textRange,
            icon,
            tooltip,
            null,
            GutterIconRenderer.Alignment.RIGHT
        ) { accessibleName }
}