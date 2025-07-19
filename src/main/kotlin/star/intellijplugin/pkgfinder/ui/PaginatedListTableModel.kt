package star.intellijplugin.pkgfinder.ui

import com.intellij.util.ui.ListTableModel
import kotlin.math.ceil

/**
 * 支持分页的 [ListTableModel]
 *
 * tip：原生 Swing [javax.swing.JTable] 没有分页概念，表格只有一页，items 就是表格的所有数据。
 * 当我们支持分页后，items 实际变为表格当前页数据，而分页模型 [Pagination] 中的 [Pagination.data] 才是表格所有数据
 *
 * @author drawsta
 * @LastModified: 2025-01-21
 * @since 2025-01-21
 */
abstract class PaginatedListTableModel<T> : ListTableModel<T>() {

    init {
        items = emptyList()
    }

    private val pagination: Pagination<T> = Pagination()

    /**
     * 更新表格的所有数据
     */
    fun updateTableData(data: List<T>) {
        pagination.data = data
    }

    /**
     * 更新表格当前页数据
     */
    fun updateCurrentPageData() {
        items = pagination.getCurrentPageData()
        fireTableDataChanged()
    }

    /**
     * 获取表格当前页数据
     */
    fun getCurrentPageData(): List<T> = items

    /**
     * 获取当前页码
     */
    fun getCurrentPage(): Int = pagination.currentPage

    /**
     * 更新当前页码
     */
    fun setCurrentPage(page: Int) {
        pagination.currentPage = page
    }

    /**
     * 获取总页数
     */
    fun getTotalPages(): Int = pagination.totalPages
}

/**
 * 分页模型
 */
private class Pagination<T>(
    // 每页条数
    var pageSize: Int = 10,
    // 整个表格的数据，初次加载的全量本地 Maven 依赖包信息，或匹配某关键字的全量搜索结果
    var data: List<T> = emptyList()
) {
    var currentPage: Int = 1

    val totalPages: Int
        get() = ceil(data.size.toDouble() / pageSize).toInt()

    fun getCurrentPageData(): List<T> {
        val fromIndex = (currentPage - 1) * pageSize
        val toIndex = minOf(fromIndex + pageSize, data.size)
        return data.subList(fromIndex, toIndex)
    }
}
