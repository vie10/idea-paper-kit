package online.viestudio.paperkit.module

import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardBaseStep
import com.intellij.openapi.observable.util.trim
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.validation.*
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.util.SystemProperties
import com.intellij.util.application
import online.viestudio.paperkit.asset.Templates
import online.viestudio.paperkit.common.Keys
import online.viestudio.paperkit.common.Links
import online.viestudio.paperkit.facet.KitFacet.Companion.addKitFacetIfHasNot
import online.viestudio.paperkit.facet.KitFacetDetector.Companion.isPaperKitModule
import online.viestudio.paperkit.util.addSuffixIfHasNot
import online.viestudio.paperkit.util.dumbService
import online.viestudio.paperkit.util.fileTemplateManager
import online.viestudio.paperkit.util.moduleManager
import org.jetbrains.kotlin.lombok.utils.capitalize
import org.jetbrains.kotlin.tools.projectWizard.KotlinNewProjectWizard
import org.jetbrains.kotlin.tools.projectWizard.plugins.buildSystem.BuildSystemType
import java.io.File
import java.util.*

private val PLUGIN_NAME_REGEX = "[a-zA-Z]{4,25}".toRegex()
private val CHECK_PLUGIN_NAME = validationErrorIf<String>("Invalid plugin name") {
    !it.matches(PLUGIN_NAME_REGEX)
}

class NewProjectPluginStep(parentStep: NewProjectWizardBaseStep) : AbstractNewProjectWizardStep(parentStep) {

    private val pluginNameProperty = propertyGraph.lazyProperty(::suggestPluginName)
    private val pluginDescriptionProperty = propertyGraph.lazyProperty(::suggestPluginDescription)
    private val gitIgnoreProperty = propertyGraph.lazyProperty(::isGitIgnoreNeeded)
    private val groupIdProperty = propertyGraph.lazyProperty(::suggestGroupId)

    private val pluginName by pluginNameProperty
    private val pluginDescription by pluginDescriptionProperty
    private val groupId by groupIdProperty
    private val gitIgnore by gitIgnoreProperty

    override fun setupUI(builder: Panel): Unit = with(builder) {
        row("GroupId") {
            textField()
                .bindText(groupIdProperty.trim())
                .columns(COLUMNS_MEDIUM)
                .validationRequestor(AFTER_GRAPH_PROPAGATION(propertyGraph))
                .trimmedTextValidation(CHECK_NON_EMPTY, CHECK_NO_WHITESPACES, CHECK_GROUP_ID)
                .gap(RightGap.SMALL)
        }.bottomGap(BottomGap.SMALL)
        row("Plugin name") {
            textField()
                .bindText(pluginNameProperty.trim())
                .columns(COLUMNS_MEDIUM)
                .validationRequestor(AFTER_GRAPH_PROPAGATION(propertyGraph))
                .trimmedTextValidation(CHECK_NON_EMPTY, CHECK_NO_WHITESPACES, CHECK_PLUGIN_NAME)
                .gap(RightGap.SMALL)
        }.bottomGap(BottomGap.SMALL)
        row("Description") {
            textField()
                .bindText(pluginDescriptionProperty.trim())
                .horizontalAlign(HorizontalAlign.FILL)
                .validationRequestor(AFTER_GRAPH_PROPAGATION(propertyGraph))
                .gap(RightGap.SMALL)
        }.bottomGap(BottomGap.SMALL)
        row {
            checkBox("Create .gitignore file")
                .bindSelected(gitIgnoreProperty)
                .validationRequestor(AFTER_GRAPH_PROPAGATION(propertyGraph))
                .gap(RightGap.SMALL)
        }.bottomGap(BottomGap.MEDIUM)
        row {
            browserLink("Notice that PaperKit is licensed under the GPL-3.0 license", Links.GITHUB)
        }
    }

    private fun suggestGroupId(): String {
        val username = SystemProperties.getUserName()
        if (!username.matches("[\\w\\s]+".toRegex())) return "org.example"
        val usernameAsGroupId =
            username.trim().lowercase(Locale.getDefault()).split("\\s+".toRegex()).joinToString(separator = ".")
        return "online.$usernameAsGroupId"
    }

    private fun isGitIgnoreNeeded(): Boolean = false

    private fun suggestPluginName(): String = "PaperKitPlugin"

    private fun suggestPluginDescription(): String = "A great plugin based on PaperKit"

    override fun setupProject(project: Project) {
        KotlinNewProjectWizard.generateProject(
            project,
            context.projectFileDirectory,
            context.projectName,
            context.projectJdk,
            BuildSystemType.GradleKotlinDsl,
            version = "1.0.0",
            addSampleCode = false
        )
        project.applyTemplates()
        project.applyFacet()
    }

    private fun Project.applyFacet() {
        dumbService.runWhenSmart {
            application.runReadAction {
                moduleManager.modules.forEach {
                    if (!it.isPaperKitModule()) return@forEach
                    it.addKitFacetIfHasNot()
                }
            }
            application.runWriteAction { VirtualFileManager.getInstance().syncRefresh() }
        }
    }

    private fun Project.applyTemplates() {
        val pluginNameWithSuffix = pluginName.capitalize().addSuffixIfHasNot("Plugin")
        val pluginNameWithoutSuffix = pluginName.capitalize().removeSuffix("Plugin")
        val templateManager = fileTemplateManager
        val mainResources = "src/main/resources"
        val mainKotlin = "src/main/kotlin"
        val groupIdAsPath = groupId.replace(".", "/")

        val attributes = templateManager.defaultProperties.apply {
            setProperty(Keys.PLUGIN_NAME, pluginNameWithoutSuffix)
            setProperty(Keys.NAME, pluginNameWithSuffix)
            setProperty(Keys.PACKAGE_NAME, groupId)
            setProperty(Keys.PLUGIN_DESCRIPTION, pluginDescription)
            setProperty(Keys.PLUGIN_QUALIFIER, "$groupId.$pluginNameWithSuffix")
            setProperty(Keys.GROUP_ID, groupId)
        }

        infix fun FileTemplate.writeIntoFile(path: String) = writeFile(path, getText(attributes))
        infix fun FileTemplateManager.find(template: String): FileTemplate = getInternalTemplate(template)

        templateManager find Templates.READY_PAPER_PLUGIN_CONFIG writeIntoFile "$mainResources/plugin.yml"
        templateManager find Templates.PLUGIN writeIntoFile "$mainKotlin/$groupIdAsPath/${pluginNameWithSuffix}.kt"
        templateManager find Templates.BUILD_GRADLE writeIntoFile "build.gradle.kts"
        if (gitIgnore) {
            templateManager find Templates.GITIGNORE writeIntoFile ".gitignore"
        }
    }

    private fun Project.writeFile(path: String, text: String) {
        val basePath = basePath ?: return
        File(basePath).resolve(path).apply {
            parentFile.apply { if (!exists()) mkdirs() }
            writeText(text)
        }
    }
}