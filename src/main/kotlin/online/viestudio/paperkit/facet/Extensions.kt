package online.viestudio.paperkit.facet

import com.intellij.facet.FacetManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

@Suppress("UnstableApiUsage")
fun PsiElement?.isNotInFacetModule(): Boolean {
    if (this == null) return false
    val module = when (this) {
        is PsiFile -> ModuleUtilCore.findModuleForFile(this)
        else -> ModuleUtilCore.findModuleForPsiElement(this)
    } ?: return false
    return FacetManager.getInstance(module).getFacetByType(KitFacetType.FACET_TYPE_ID) == null
}