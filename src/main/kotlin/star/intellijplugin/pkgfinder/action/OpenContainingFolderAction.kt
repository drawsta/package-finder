package star.intellijplugin.pkgfinder.action

import com.intellij.ide.actions.RevealFileAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vfs.LocalFileSystem
import star.intellijplugin.pkgfinder.util.Icons
import java.io.File

/**
 * @author drawsta
 * @LastModified: 2025-07-13
 * @since 2025-07-13
 */
class OpenContainingFolderAction(
    private val path: String
) : AnAction("Open Containing Folder", "Open containing folder", Icons.FOLDER.getThemeBasedIcon()) {
    override fun actionPerformed(e: AnActionEvent) {
        val virtualFile = LocalFileSystem.getInstance().findFileByPath(path)
        if (virtualFile == null || !virtualFile.exists()) {
            return
        }

        RevealFileAction.openFile(File(path))
    }
}
