package online.viestudio.paperkit.asset

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

abstract class Assets {

    protected fun loadIcon(path: String): Icon {
        return IconLoader.getIcon("/assets/icons/$path", Assets::class.java)
    }
}