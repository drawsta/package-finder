package star.intellijplugin.pkgfinder.maven

import com.intellij.openapi.diagnostic.Logger
import kotlinx.serialization.json.Json
import star.intellijplugin.pkgfinder.setting.PackageFinderSetting
import star.intellijplugin.pkgfinder.util.HttpRequestHelper
import star.intellijplugin.pkgfinder.util.showDialogWithConfigButton
import star.intellijplugin.pkgfinder.util.showErrorDialog
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * refer: https://central.sonatype.org/search/rest-api-guide/
 *
 * @author drawsta
 * @LastModified: 2025-07-16
 * @since 2025-01-19
 */
object DependencyService {

    private val log = Logger.getInstance(javaClass)

    fun searchFromMavenCentral(text: String): List<Dependency> {
        val url = mavenSearchUrl(text, rowsLimit = 200)
        val result = HttpRequestHelper.getForList(url) { response -> parseCentralResponse(response) }
        return when (result) {
            is HttpRequestHelper.RequestResult.Success -> result.data
            is HttpRequestHelper.RequestResult.Error -> {
                showErrorDialog(result.exception.localizedMessage)
                emptyList()
            }
        }
    }

    fun searchFromNexus(text: String): List<Dependency> {
        return nexusSearchUrl(text)?.let { url ->
            when (val result = HttpRequestHelper.getForList(url, ::parseNexusResponse)) {
                is HttpRequestHelper.RequestResult.Success -> result.data
                is HttpRequestHelper.RequestResult.Error -> {
                    showDialogWithConfigButton(result.exception.localizedMessage)
                    emptyList()
                }
            }
        } ?: emptyList()
    }

    private fun parseCentralResponse(responseBody: String): List<Dependency> {
        return try {
            val json = Json { ignoreUnknownKeys = true } // 反序列化时忽略掉类中不存在的属性
            val artifactResponse = json.decodeFromString(MavenSearchResult.serializer(), responseBody)
            artifactResponse.response.centralDependencies
        } catch (e: Exception) {
            log.error("Failed to parse dependency info from Maven Central: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    private fun parseNexusResponse(responseBody: String): List<NexusDependency> {
        return try {
            val json = Json { ignoreUnknownKeys = true } // 反序列化时忽略掉类中不存在的属性
            val searchResult = json.decodeFromString(NexusSearchResult.serializer(), responseBody)
            val result = mutableListOf<NexusDependency>()

            for (item in searchResult.items) {
                if (item.assets.isEmpty()) continue

                val firstAsset = item.assets.first()
                val maven2Info = firstAsset.maven2
                val uploaderIp = firstAsset.uploaderIp

                val downloadInfos = item.assets.map { asset ->
                    DownloadInfo(
                        extension = asset.maven2.extension,
                        downloadUrl = asset.downloadUrl
                    )
                }

                result.add(
                    NexusDependency(
                        groupId = maven2Info.groupId,
                        artifactId = maven2Info.artifactId,
                        version = maven2Info.version,
                        uploaderIp = uploaderIp,
                        downloadInfos = downloadInfos
                    )
                )
            }

            result
        } catch (e: Exception) {
            log.error("Failed to parse dependency info from Nexus: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    /**
     * 构建 Maven 中央仓库的搜索 URL
     *
     * @param query 搜索关键字，支持以下格式：
     * - `g:group`：按 group 搜索（例如：`g:com.example`）
     * - `a:artifact`：按 artifact 搜索（例如：`a:lombok`）
     * - `group:artifact`：按 group 和 artifact 组合搜索（例如：`com.example:lombok`）
     * - `artifact`：直接按 artifact 搜索（例如：`lombok`）
     * @param version 可选参数，指定版本号（例如：`1.0.0`）
     * @param packaging 可选参数，指定打包类型（例如：`jar`）
     * @param rowsLimit 可选参数，指定搜索结果的最大条数，默认为 20
     * @return 返回构建好的 Maven 中央仓库搜索 URL
     *
     * 示例：
     * ```
     * mavenSearchUrl("g:com.example") // 按 group 搜索
     * mavenSearchUrl("a:lombok") // 按 artifact 搜索
     * mavenSearchUrl("lombok") // 按 artifact 搜索
     * mavenSearchUrl("com.example:lombok") // 按 group 和 artifact 组合搜索
     * ```
     */
    fun mavenSearchUrl(
        query: String, // 支持 g:group、a:artifact、artifact 或 group:artifact 格式
        version: String? = null, // 可选参数：版本号
        packaging: String? = null, // 可选参数：打包类型
        rowsLimit: Int = 20 // 可选参数：搜索结果的最大条数
    ): String {
        val (group, artifactId) = when {
            query.startsWith("g:") -> Pair(query.removePrefix("g:"), null)
            query.startsWith("a:") -> Pair(null, query.removePrefix("a:"))
            query.contains(":") -> {
                val parts = query.split(":")
                Pair(parts[0], parts[1])
            }

            else -> Pair(null, query)
        }

        // 构建查询条件
        val q = listOf(
            "g" to group,
            "a" to artifactId,
            "v" to version,
            "p" to packaging
        )
            // 过滤掉值为 null 的键值对
            .filter { it.second != null }
            .joinToString(separator = " AND ") { "${it.first}:\"${it.second}\"" }

        return "https://search.maven.org/solrsearch/select?q=${q.encodeURL()}&core=gav&rows=$rowsLimit&wt=json"
    }

    fun mavenDownloadUrl(
        group: String,
        artifactId: String,
        version: String,
        ec: String
    ): String {
        val groupPath = group.replace(".", "/")
        return "https://search.maven.org/remotecontent?filepath=$groupPath/$artifactId/$version/$artifactId-$version$ec"
    }

    private fun nexusSearchUrl(q: String): String? {
        val setting = PackageFinderSetting.instance
        if (setting.nexusServerUrl.isBlank()) {
            showDialogWithConfigButton()
            return null
        }

        val nexusServerUrl = setting.nexusServerUrl.trimEnd('/')
        return "$nexusServerUrl/service/rest/v1/search?sort=version&direction=desc&q=$q"
    }

    private fun String.encodeURL(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
}
