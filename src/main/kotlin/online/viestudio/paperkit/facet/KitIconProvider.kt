package online.viestudio.paperkit.facet

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import online.viestudio.paperkit.asset.MediaAssets
import javax.swing.Icon

class KitIconProvider : IconProvider() {

    override fun getIcon(element: PsiElement, flags: Int): Icon? = with(element) {
        if (element.isNotInFacetModule()) return null
        if (this !is PsiDirectory) return null
        if (parent?.name != "src") return null
        MediaAssets.PAPER_KIT_ICON
    }
}