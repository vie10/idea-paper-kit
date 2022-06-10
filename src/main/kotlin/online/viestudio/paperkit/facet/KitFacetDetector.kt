package online.viestudio.paperkit.facet

import com.intellij.facet.FacetType
import com.intellij.facet.impl.ui.libraries.LibrariesValidatorContext
import com.intellij.framework.detection.FacetBasedFrameworkDetector
import com.intellij.framework.detection.FileContentPattern
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ModuleRootModel
import com.intellij.openapi.roots.ui.configuration.DefaultModulesProvider
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.roots.ui.configuration.libraries.LibraryPresentationManager
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PatternCondition
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileContent
import online.viestudio.paperkit.presentation.paperKitLibraryKind
import org.jetbrains.kotlin.idea.KotlinFileType

private const val DETECTOR_ID = "paper-kit-facet-detector"

class KitFacetDetector : FacetBasedFrameworkDetector<KitFacet, KitFacetConfiguration>(DETECTOR_ID) {

    private val facetType = KitFacetType()

    override fun getFileType(): FileType = KotlinFileType.INSTANCE

    override fun createSuitableFilePattern(): ElementPattern<FileContent> = FileContentPattern.fileContent()
        .with(object : PatternCondition<FileContent>("") {

            override fun accepts(t: FileContent, context: ProcessingContext?): Boolean {
                return ModuleUtilCore.findModuleForFile(t.psiFile)?.isPaperKitModule() == true
            }
        })

    override fun getFacetType(): FacetType<KitFacet, KitFacetConfiguration> = facetType

    companion object {

        fun Module.isPaperKitModule(): Boolean {
            val manager = LibraryPresentationManager.getInstance()
            val context = LibrariesValidatorContextImpl(this)
            var isPaperKitModule = false
            context.rootModel
                .orderEntries()
                .using(context.modulesProvider)
                .recursively()
                .librariesOnly()
                .forEachLibrary {
                    isPaperKitModule =
                        manager.isLibraryOfKind(it, context.librariesContainer, setOf(paperKitLibraryKind))
                    !isPaperKitModule
                }
            return isPaperKitModule
        }
    }

    private class LibrariesValidatorContextImpl(
        private val module: Module,
    ) : LibrariesValidatorContext {

        private val librariesContainer = LibrariesContainerFactory.createContainer(module)

        override fun getRootModel(): ModuleRootModel = module.rootManager

        override fun getModifiableRootModel(): ModifiableRootModel? = null

        override fun getModulesProvider(): ModulesProvider = DefaultModulesProvider(module.project)

        override fun getModule(): Module = module

        override fun getLibrariesContainer(): LibrariesContainer = librariesContainer
    }
}