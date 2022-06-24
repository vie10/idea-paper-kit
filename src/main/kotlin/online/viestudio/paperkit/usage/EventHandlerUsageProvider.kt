package online.viestudio.paperkit.usage

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import online.viestudio.paperkit.util.isListenerImplementation
import org.jetbrains.kotlin.asJava.elements.KtLightMethod

private const val EVENT_HANDLER_ANNOTATION_QUALIFIED_NAME = "org.bukkit.event.EventHandler"

class EventHandlerUsageProvider : ImplicitUsageProvider {

    override fun isImplicitUsage(element: PsiElement): Boolean {
        if (element !is KtLightMethod) return false
        if (!element.annotations.any { it.qualifiedName == EVENT_HANDLER_ANNOTATION_QUALIFIED_NAME }) return false
        if (element.findParentOfType<PsiClass>()?.isListenerImplementation() != true) return false
        return true
    }

    override fun isImplicitRead(element: PsiElement): Boolean = false

    override fun isImplicitWrite(element: PsiElement): Boolean = false
}