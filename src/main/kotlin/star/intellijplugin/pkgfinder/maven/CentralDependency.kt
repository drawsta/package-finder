package star.intellijplugin.pkgfinder.maven

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 从 Maven 中央仓库查询到的 Maven 依赖包信息模型
 *
 * @author drawsta
 * @LastModified: 2025-01-20
 * @since 2025-01-20
 */
@Serializable
data class CentralDependency(
    val id: String,
    @SerialName("g")
    override val groupId: String,
    @SerialName("a")
    override val artifactId: String,
    @SerialName("v")
    override val version: String,
    @SerialName("p")
    val packaging: String,
    val timestamp: Long,
    // 标识 Maven 构件的扩展名（Extension）和分类器（Classifier），
    // 比如 -sources.jar、.jar、-javadoc.jar、.pom、.pom.sha512、.module、.jar.asc.sha512 等
    // 同时也标识了该构件提供的制品（比如 ec 数组中只有 .jar、.pom，则表示该构件只发布了 jar 包和 pom 文件，没有源码包、javadoc 等制品）
    // 可以参考 https://search.maven.org/solrsearch/select?q=g:org.json+AND+a:json&core=gav&rows=200&wt=json
    val ec: List<String>
) : Dependency(groupId, artifactId, version)

@Serializable
data class ArtifactResponse(
    val numFound: Int,
    @SerialName("docs")
    val centralDependencies: List<CentralDependency>
)

@Serializable
data class MavenSearchResult(
    val response: ArtifactResponse
)
