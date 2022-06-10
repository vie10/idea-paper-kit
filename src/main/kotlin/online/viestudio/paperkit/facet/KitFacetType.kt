package online.viestudio.paperkit.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetType
import com.intellij.facet.FacetTypeId
import com.intellij.openapi.module.JavaModuleType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import online.viestudio.paperkit.asset.MediaAssets
import javax.swing.Icon

class KitFacetType : FacetType<KitFacet, KitFacetConfiguration>(FACET_TYPE_ID, FACET_ID, FACET_NAME) {

    override fun createDefaultConfiguration(): KitFacetConfiguration = KitFacetConfiguration()

    override fun createFacet(
        module: Module,
        name: String,
        configuration: KitFacetConfiguration,
        underlyingFacet: Facet<*>?,
    ): KitFacet = KitFacet(this, module, name, configuration, underlyingFacet)

    override fun isSuitableModuleType(moduleType: ModuleType<*>?): Boolean = moduleType is JavaModuleType

    override fun getIcon(): Icon = MediaAssets.PAPER_KIT_ICON

    companion object {
        const val FACET_ID = "PAPER_KIT_FACET_ID"
        const val FACET_NAME = "PaperKit"
        val FACET_TYPE_ID = FacetTypeId<KitFacet>(FACET_ID)
    }
}