package star.intellijplugin.pkgfinder.maven

/**
 * 缓存，避免切换 Maven 仓库来源后，再切换回来，之前数据丢失，导致需要重新加载/查询
 *
 * @author drawsta
 * @LastModified: 2025-07-08
 * @since 2025-01-20
 */
class DependencyCache {

    /**
     * 缓存初次加载的全量本地 Maven 依赖包信息，避免反复递归本地 Maven 仓库
     *
     * 作用：
     * 1. 从本地 Maven 仓库搜索依赖时，直接从缓存取
     * 2. 切换 Maven 仓库来源时，当切回本地 Maven 仓库，需在表格显示之前加载过的本地 Maven 依赖包信息
     *
     * 更新缓存
     * 1. 点击重新加载数据按钮时
     */
    var localDependencies: List<Dependency> = emptyList()

    /**
     * 缓存从 Maven 中央仓库查询到的 Maven 依赖包信息
     *
     * 作用：
     * 1. 切换 Maven 仓库来源时，当切回 Maven 中央仓库，需在表格显示之前从 Maven 中央仓库查询出的依赖包信息
     */
    var centralDependencies: List<Dependency> = emptyList()

    /**
     * 缓存从 Nexus 私服仓库查询到的 Maven 依赖包信息
     *
     * 作用：
     * 1. 切换 Maven 仓库来源时，当切回 Nexus 私服仓库，需在表格显示之前从 Nexus 私服仓库查询出的依赖包信息
     */
    var nexusDependencies: List<Dependency> = emptyList()
}
