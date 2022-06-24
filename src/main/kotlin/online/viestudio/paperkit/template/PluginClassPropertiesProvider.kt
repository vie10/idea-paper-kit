package online.viestudio.paperkit.template

import com.intellij.ide.fileTemplates.DefaultTemplatePropertiesProvider
import com.intellij.psi.PsiDirectory
import online.viestudio.paperkit.common.Keys
import online.viestudio.paperkit.facet.isNotInFacetModule
import online.viestudio.paperkit.util.findPluginImplementation
import org.jetbrains.kotlin.idea.util.module
import org.jetbrains.kotlin.psi.KtClass
import java.util.*

class PluginClassPropertiesProvider : DefaultTemplatePropertiesProvider {

    override fun fillProperties(directory: PsiDirectory, props: Properties) {
        if (directory.isNotInFacetModule()) return
        val module = directory.module ?: return
        val pluginClass: KtClass = module.findPluginImplementation() ?: return
        props.setProperty(Keys.PLUGIN_QUALIFIER, pluginClass.fqName!!.asString())
    }
}