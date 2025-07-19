package star.intellijplugin.pkgfinder

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

/**
 * @author drawsta
 * @since 2025-01-16
 */
object PackageFinderBundle {
    private const val BUNDLE: String = "messages.PackageFinder"
    private val INSTANCE = DynamicBundle(PackageFinderBundle::class.java, BUNDLE)

    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
        return INSTANCE.getMessage(key, *params)
    }
}
