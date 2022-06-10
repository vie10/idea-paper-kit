package online.viestudio.paperkit.template

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import online.viestudio.paperkit.asset.MediaAssets
import online.viestudio.paperkit.asset.Templates
import online.viestudio.paperkit.util.addSuffixIfHasNot

class CreateComponent : CreateFileFromTemplateAction(
    "PaperKit",
    "Creates new PaperKit component",
    MediaAssets.PLUGIN_ICON
) {

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle("New Component")
            .addKind("Child command", MediaAssets.COMMAND_ICON, Templates.CHILD_COMMAND)
            .addKind("Config", MediaAssets.CONFIG_ICON, Templates.CONFIG)
            .addKind("Parent command", MediaAssets.COMMAND_ICON, Templates.PARENT_COMMAND)
            .addKind("Listener", MediaAssets.LISTENER_ICON, Templates.LISTENER)
            .addKind("Plugin", MediaAssets.PLUGIN_ICON, Templates.PLUGIN)
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String =
        "Create $newName"

    override fun createFile(name: String?, templateName: String?, dir: PsiDirectory?): PsiFile? {
        val finalName = when (templateName) {
            Templates.PARENT_COMMAND, Templates.CHILD_COMMAND -> name?.addSuffixIfHasNot("Command")
            Templates.PLUGIN -> name?.addSuffixIfHasNot("Plugin")
            Templates.CONFIG -> name?.addSuffixIfHasNot("Config")
            Templates.LISTENER -> name?.addSuffixIfHasNot("Listener")
            else -> name
        }
        return super.createFile(finalName, templateName, dir)
    }


}