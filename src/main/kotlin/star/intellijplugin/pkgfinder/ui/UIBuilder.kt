package star.intellijplugin.pkgfinder.ui

import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

/**
 * @author drawsta
 * @LastModified: 2025-01-22
 * @since 2025-01-22
 */
fun boxPanel(init: JPanel.() -> Unit) = object : JPanel() {
    init {
        // BoxLayout 能根据组件大小和布局方向（水平或垂直）动态调整子组件的大小和位置
        layout = BoxLayout(this, BoxLayout.X_AXIS) // 组件水平排列
        alignmentX = LEFT_ALIGNMENT
        init()
    }
}

fun borderPanel(init: JPanel.() -> Unit) = object : JPanel() {
    init {
        layout = BorderLayout(0, 0)
        init()
    }
}

fun scrollPanel(init: JScrollPane.() -> Unit) = object : JScrollPane() {
    init {
        // refer com.intellij.ui.ScrollPaneFactory#setScrollPaneEmptyBorder
        // set empty border, because setting null doesn't always take effect
        border = JBUI.Borders.empty()
        viewportBorder = JBUI.Borders.empty()

        horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_NEVER
        verticalScrollBarPolicy = VERTICAL_SCROLLBAR_ALWAYS

        init()
    }
}
