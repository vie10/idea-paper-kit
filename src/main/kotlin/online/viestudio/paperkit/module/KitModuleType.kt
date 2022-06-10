package online.viestudio.paperkit.module

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import online.viestudio.paperkit.asset.MediaAssets
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel

private const val MODULE_ID = "PAPER_KIT"

class KitModuleType : ModuleType<KitModuleBuilder>(MODULE_ID) {

    override fun createModuleBuilder(): KitModuleBuilder = KitModuleBuilder()

    override fun getName(): String = "PaperKit"

    override fun getDescription(): String = "PaperKit plugin"

    override fun getNodeIcon(isOpened: Boolean): Icon = MediaAssets.PAPER_KIT_ICON

    override fun getIcon(): Icon = MediaAssets.PAPER_KIT_ICON

    override fun createWizardSteps(
        wizardContext: WizardContext,
        moduleBuilder: KitModuleBuilder,
        modulesProvider: ModulesProvider,
    ): Array<ModuleWizardStep> = arrayOf(
        object : ModuleWizardStep() {
            override fun getComponent(): JComponent = JPanel()

            override fun updateDataModel() {

            }
        }
    )

    companion object {

        val INSTANCE = KitModuleType()
    }
}