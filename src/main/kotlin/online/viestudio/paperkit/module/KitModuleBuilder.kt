package online.viestudio.paperkit.module

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.*
import com.intellij.openapi.projectRoots.SdkTypeId
import online.viestudio.paperkit.asset.MediaAssets
import org.jetbrains.kotlin.idea.framework.KotlinSdkType
import javax.swing.Icon

class KitModuleBuilder : AbstractNewProjectWizardBuilder() {

    override fun createStep(context: WizardContext): NewProjectWizardStep = RootNewProjectWizardStep(context)
        .chain(::NewProjectWizardBaseStep, ::NewProjectPluginStep)

    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean = sdkType is KotlinSdkType

    override fun getDescription(): String = "PaperKit plugin"

    override fun getNodeIcon(): Icon = MediaAssets.PAPER_KIT_ICON

    override fun getPresentableName(): String = "PaperKit"
}