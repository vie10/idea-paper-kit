package online.viestudio.paperkit.facet

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import online.viestudio.paperkit.asset.MediaAssets
import online.viestudio.paperkit.util.isCommandImplementation
import online.viestudio.paperkit.util.isConfigImplementation
import online.viestudio.paperkit.util.isListenerImplementation
import online.viestudio.paperkit.util.isPluginImplementation
import javax.swing.Icon

class ComponentIconProvider : IconProvider() {

    override fun getIcon(element: PsiElement, flags: Int): Icon? = with(element) {
        if (element.isNotInFacetModule()) return null
        if (isPluginImplementation()) return MediaAssets.PLUGIN_ICON
        if (isCommandImplementation()) return MediaAssets.COMMAND_ICON
        if (isListenerImplementation()) return MediaAssets.LISTENER_ICON
        if (isConfigImplementation()) return MediaAssets.CONFIG_ICON
        null
    }
}