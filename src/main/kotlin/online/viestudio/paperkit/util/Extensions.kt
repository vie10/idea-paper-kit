package online.viestudio.paperkit.util

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.search.JavaSourceFilterScope
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.contextOfType
import org.jetbrains.jps.model.java.JavaResourceRootType
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.caches.lightClasses.annotations.KOTLINX_SERIALIZABLE_FQ_NAME
import org.jetbrains.kotlin.idea.util.rootManager
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPrimaryConstructor

val PLUGIN_FQ_REGEX = "online.viestudio.paperkit.plugin.*.Plugin".toRegex()
val COMMAND_FQ_REGEX = "online.viestudio.paperkit.command.*.Command".toRegex()
val LISTENER_FQ_REGEX = "online.viestudio.paperkit.listener.*.Listener".toRegex()

fun Module.findPluginImplementation(): KtClass? {
    FileTypeIndex.getFiles(KotlinFileType.INSTANCE, JavaSourceFilterScope(GlobalSearchScope.moduleScope(this)))
        .mapNotNull {
            project.psiManager.findFile(it) as? KtFile
        }.forEach { ktFile ->
            val result = ktFile.classes.find { it.isPluginImplementation() }?.ktClassOrNull
            if (result != null) return result
        }
    return null
}

fun PsiElement.isConfigImplementation(): Boolean {
    val ktClass = ktClassOrNull ?: return false
    if (ktClass.name?.endsWith("Config") != true) return false
    return ktClass.hasAnnotation(KOTLINX_SERIALIZABLE_FQ_NAME)
}

private fun KtClass.hasAnnotation(fqName: FqName): Boolean {
    return annotationEntries.any {
        it.typeReference?.text?.endsWith(fqName.shortName().asString()) == true
    }
}

fun PsiElement.isListenerImplementation(): Boolean = ktClassOrNull?.isImplementationOf(LISTENER_FQ_REGEX) == true

fun PsiElement.isCommandImplementation(): Boolean = ktClassOrNull?.isImplementationOf(COMMAND_FQ_REGEX) == true

fun PsiElement.isPluginImplementation(): Boolean = ktClassOrNull?.isImplementationOf(PLUGIN_FQ_REGEX) == true

private fun KtClass.isImplementationOf(fqNameRegex: Regex): Boolean {
    fqName?.asString()?.let {
        if (fqNameRegex.matches(it)) return true
    }
    superTypeListEntries.mapNotNull {
        it.typeAsUserType?.referenceExpression?.resolve()
    }.forEach {
        val result = when (it) {
            is KtClass -> it.isImplementationOf(fqNameRegex)
            is KtPrimaryConstructor -> it.contextOfType<KtClass>()?.isImplementationOf(fqNameRegex) ?: false
            else -> false
        }
        if (result) return true
    }

    return false
}

fun String.addSuffixIfHasNot(suffix: String) = if (this.endsWith(suffix)) this else this + suffix

val Project.moduleManager: ModuleManager get() = ModuleManager.getInstance(this)

val Project.fileTemplateManager: FileTemplateManager get() = FileTemplateManager.getInstance(this)

val Project.psiManager: PsiManager get() = PsiManager.getInstance(this)

val Project.dumbService: DumbService get() = DumbService.getInstance(this)

val PsiElement.ktClassOrNull: KtClass?
    get() {
        return when (this) {
            is KtUltraLightClass -> kotlinOrigin as? KtClass
            is KtClass -> this
            else -> null
        }
    }

fun Module.findResourceFile(name: String): VirtualFile? {
    rootManager.getSourceRoots(JavaResourceRootType.RESOURCE).filterIsInstance<VirtualFile>().forEach {
        return it.findChild(name)
    }
    return null
}