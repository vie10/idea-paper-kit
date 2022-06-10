package online.viestudio.paperkit.facet

import com.intellij.facet.ui.FacetEditorTab
import javax.swing.JComponent
import javax.swing.JPanel

class KitFacetEditorTab : FacetEditorTab() {

    override fun createComponent(): JComponent = JPanel()
    override fun apply() {}
    override fun isModified(): Boolean = false
    override fun getDisplayName(): String = KitFacetType.FACET_NAME
}