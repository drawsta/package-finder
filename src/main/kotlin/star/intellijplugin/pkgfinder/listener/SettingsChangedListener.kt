package star.intellijplugin.pkgfinder.listener

import com.intellij.util.messages.Topic

/**
 * @author drawsta
 * @LastModified: 2025-09-08
 * @since 2025-09-08
 */
interface SettingsChangedListener {

    companion object {
        val TOPIC = Topic.create("PackageFinder.SettingsChanged", SettingsChangedListener::class.java)
    }

    fun onSettingsChanged()
}
