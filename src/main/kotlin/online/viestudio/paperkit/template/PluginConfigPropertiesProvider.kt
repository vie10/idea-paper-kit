package online.viestudio.paperkit.template

import com.intellij.ide.fileTemplates.DefaultTemplatePropertiesProvider
import com.intellij.psi.PsiDirectory
import online.viestudio.paperkit.common.Keys
import online.viestudio.paperkit.common.Paper
import online.viestudio.paperkit.facet.isNotInFacetModule
import online.viestudio.paperkit.util.findResourceFile
import online.viestudio.paperkit.util.psiManager
import org.jetbrains.kotlin.idea.util.module
import java.util.*

class PluginConfigPropertiesProvider : DefaultTemplatePropertiesProvider {

    override fun fillProperties(directory: PsiDirectory, props: Properties) {
        if (directory.isNotInFacetModule()) return
        val config = directory.module?.findResourceFile(Paper.PLUGIN_CONFIG_NAME) ?: return
        val file = directory.project.psiManager.findFile(config) ?: return
        file.text.split(System.lineSeparator()).forEach {
            when (it.substringBefore(":")) {
                "name" -> {
                    props.setProperty(Keys.PLUGIN_NAME, it.readStrValueOrNull())
                }
            }
        }
    }

    private fun String.readStrValueOrNull(): String? {
        val result = substringAfter(":").trim().removePrefix("\"").removeSuffix("\"")
        if (result == this) return null
        return result
    }
}