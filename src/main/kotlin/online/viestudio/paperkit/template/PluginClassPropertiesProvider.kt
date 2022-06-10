package online.viestudio.paperkit.template

import com.intellij.ide.fileTemplates.DefaultTemplatePropertiesProvider
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiDirectory
import com.intellij.psi.impl.search.JavaSourceFilterScope
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import online.viestudio.paperkit.common.Keys
import online.viestudio.paperkit.facet.isNotInFacetModule
import online.viestudio.paperkit.util.isPluginImplementation
import online.viestudio.paperkit.util.ktClassOrNull
import online.viestudio.paperkit.util.psiManager
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.util.module
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import java.util.*

class PluginClassPropertiesProvider : DefaultTemplatePropertiesProvider {

    override fun fillProperties(directory: PsiDirectory, props: Properties) {
        if (directory.isNotInFacetModule()) return
        val module = directory.module ?: return
        val pluginClass: KtClass = module.findPluginImplementation() ?: return
        props.setProperty(Keys.PLUGIN_QUALIFIER, pluginClass.fqName!!.asString())
    }

    private fun Module.findPluginImplementation(): KtClass? {
        FileTypeIndex.getFiles(KotlinFileType.INSTANCE, JavaSourceFilterScope(GlobalSearchScope.moduleScope(this)))
            .mapNotNull {
                project.psiManager.findFile(it) as? KtFile
            }.forEach { ktFile ->
                val result = ktFile.classes.find { it.isPluginImplementation() }?.ktClassOrNull
                if (result != null) return result
            }
        return null
    }
}