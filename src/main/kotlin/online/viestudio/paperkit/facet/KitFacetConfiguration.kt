package online.viestudio.paperkit.facet

import com.intellij.facet.FacetConfiguration
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.components.PersistentStateComponent

class KitFacetConfiguration : FacetConfiguration, PersistentStateComponent<KitFacetState> {

    private var state: KitFacetState = KitFacetState()

    override fun getState(): KitFacetState = state

    override fun loadState(state: KitFacetState) {
        this.state = state
    }

    override fun createEditorTabs(
        editorContext: FacetEditorContext?,
        validatorsManager: FacetValidatorsManager?,
    ): Array<FacetEditorTab> = arrayOf(KitFacetEditorTab())
}