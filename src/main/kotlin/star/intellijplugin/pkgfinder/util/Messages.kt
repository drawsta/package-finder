package star.intellijplugin.pkgfinder.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.ui.Messages
import star.intellijplugin.pkgfinder.PackageFinderBundle.message
import star.intellijplugin.pkgfinder.setting.PackageFinderSettingWindow

/**
 * @author drawsta
 * @LastModified: 2025-07-16
 * @since 2025-01-27
 */
private const val GENERAL_NOTIFICATION_GROUP_ID = "package.finder.notification.general"

fun showInformationNotification(content: String) {
    val notification = NotificationGroupManager.getInstance()
        .getNotificationGroup(GENERAL_NOTIFICATION_GROUP_ID)
        .createNotification(content, NotificationType.INFORMATION)
    Notifications.Bus.notify(notification)
}

fun showErrorDialog(message: String) {
    ApplicationManager.getApplication().invokeLater {
        Messages.showErrorDialog(
            message(
                "api.error.dialog.message",
                message
            ), message("api.error.dialog.title")
        )
    }
}

fun showDialogWithConfigButton(vararg messages: Any) {
    ApplicationManager.getApplication().invokeLater {
        Messages.showDialog(
            message("nexus.dependency.query.error.message", messages),
            message("api.error.dialog.title"),
            arrayOf(
                message("nexus.dependency.query.error.option.configure"),
                message("nexus.dependency.query.error.option.cancel")
            ),
            0, // 默认选中第 0 个 option
            Messages.getErrorIcon()
        ).takeIf { it == 0 }?.also {
            // 用户点击了「去配置」
            ShowSettingsUtil.getInstance().showSettingsDialog(null, PackageFinderSettingWindow::class.java)
        }
    }
}
