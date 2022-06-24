package online.viestudio.paperkit.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import online.viestudio.paperkit.util.findPluginImplementation
import org.jetbrains.kotlin.idea.util.module

class ComponentContext : TemplateContextType("PAPER_KIT_COMPONENT", "PaperKit") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return templateActionContext.file.module?.findPluginImplementation() != null
    }
}