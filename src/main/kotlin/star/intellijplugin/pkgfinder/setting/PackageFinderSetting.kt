package star.intellijplugin.pkgfinder.setting

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.xmlb.XmlSerializerUtil
import star.intellijplugin.pkgfinder.listener.SettingsChangedListener
import star.intellijplugin.pkgfinder.maven.DependencyFormat
import star.intellijplugin.pkgfinder.maven.DependencyScope
import star.intellijplugin.pkgfinder.maven.MavenRepositorySource

/**
 * @author drawsta
 * @LastModified: 2025-09-08
 * @since 2025-07-08
 */
@Service(Service.Level.APP)
@State(name = "PackageFinder", storages = [Storage("PackageFinder-config.xml")])
class PackageFinderSetting : PersistentStateComponent<PackageFinderSettingState> {

    private var myState = PackageFinderSettingState()

    private val log = Logger.getInstance(javaClass)

    companion object {
        val instance: PackageFinderSetting
            get() = ApplicationManager.getApplication().getService(PackageFinderSetting::class.java)
    }

    var repoSource: MavenRepositorySource
        get() = myState.repoSource
        set(value) {
            myState.repoSource = value
            log.info("Maven repository source updated to $value")
            ApplicationManager.getApplication().messageBus.syncPublisher(SettingsChangedListener.TOPIC).onSettingsChanged()
        }

    var dependencyScope: DependencyScope
        get() = myState.dependencyScope
        set(value) {
            myState.dependencyScope = value
            log.info("Dependency scope updated to $value")
            ApplicationManager.getApplication().messageBus.syncPublisher(SettingsChangedListener.TOPIC).onSettingsChanged()
        }

    var dependencyFormat: DependencyFormat
        get() = myState.dependencyFormat
        set(value) {
            myState.dependencyFormat = value
            log.info("Dependency format updated to $value")
            ApplicationManager.getApplication().messageBus.syncPublisher(SettingsChangedListener.TOPIC).onSettingsChanged()
        }

    var nexusServerUrl: String
        get() = myState.nexusServerUrl
        set(value) {
            myState.nexusServerUrl = value
            log.info("Nexus server url updated to $value")
        }

    override fun getState(): PackageFinderSettingState {
        log.info("getState() called, returning myState = $myState")
        return myState
    }

    override fun loadState(state: PackageFinderSettingState) {
        log.info("loadState(...) called. incoming state = $state")
        XmlSerializerUtil.copyBean(state, this.myState)
        log.info("after copyBean, myState = $myState")
    }
}
