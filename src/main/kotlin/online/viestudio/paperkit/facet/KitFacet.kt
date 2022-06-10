package online.viestudio.paperkit.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.util.application

open class KitFacet(
    facetType: FacetType<out Facet<*>, *>,
    module: Module,
    name: String,
    configuration: KitFacetConfiguration,
    underlyingFacet: Facet<*>?,
) : Facet<KitFacetConfiguration>(facetType, module, name, configuration, underlyingFacet) {

    companion object {

        fun Module.addKitFacetIfHasNot() {
            val facetManager = FacetManager.getInstance(this)
            if (facetManager.getFacetByType(KitFacetType.FACET_TYPE_ID) != null) return
            application.runWriteAction {
                facetManager.addFacet(KitFacetType(), KitFacetType.FACET_NAME, null)
            }
        }
    }
}