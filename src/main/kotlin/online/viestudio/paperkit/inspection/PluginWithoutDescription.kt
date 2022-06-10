package online.viestudio.paperkit.inspection

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parentOfType
import online.viestudio.paperkit.asset.Templates
import online.viestudio.paperkit.common.Paper
import online.viestudio.paperkit.facet.isNotInFacetModule
import online.viestudio.paperkit.util.*
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.idea.util.module
import org.jetbrains.kotlin.idea.util.sourceRoots
import org.jetbrains.kotlin.psi.KtFile

class PluginWithoutDescription : AbstractKotlinInspection() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor = Visitor(holder)

    class Visitor(
        private val holder: ProblemsHolder,
    ) : PsiElementVisitor() {

        override fun visitElement(element: PsiElement) {
            if (element.isNotInFacetModule()) return
            val ktClass = element.ktClassOrNull ?: return
            if (!ktClass.isPluginImplementation()) return
            val ktFile = ktClass.parentOfType() as? KtFile ?: return
            if (ktFile.module?.findResourceFile(Paper.PLUGIN_CONFIG_NAME) != null) return
            val identifier = ktClass.nameIdentifier ?: return
            holder.registerProblem(
                identifier,
                "Plugin has not plugin.yml config which is required by Paper",
                Fix(element)
            )
        }
    }

    class Fix(element: PsiElement) : LocalQuickFixOnPsiElement(element) {

        override fun getFamilyName(): String = "Create file"

        override fun getText(): String = "Create plugin.yml file"

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val module = file.module ?: return
            val root = module.sourceRoots.find { it.name == "resources" } ?: return
            val dir = project.psiManager.findDirectory(root) ?: return
            val templateManager = project.fileTemplateManager
            val template = templateManager.getInternalTemplate(Templates.PAPER_PLUGIN_CONFIG)
            CreateFileFromTemplateAction.createFileFromTemplate(
                Paper.PLUGIN_CONFIG_NAME,
                template,
                dir,
                null,
                true
            )
        }
    }
}