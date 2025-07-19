package star.intellijplugin.pkgfinder.npm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Npm Registry 搜索 API 返回结果模型
 *
 * refer:
 * - https://registry.npmjs.com/-/v1/search?text=vue
 * - https://www.npmjs.com/search?q=vue
 *
 * @author drawsta
 * @LastModified: 2025-01-27
 * @since 2025-01-27
 */
@Serializable
data class NpmRegistrySearchResult(
    val objects: List<NpmObject>
)

@Serializable
data class NpmObject(
    val downloads: Downloads,
    val dependents: Int,
    val `package`: Package,
)

@Serializable
data class Downloads(
    val monthly: Int,
    val weekly: Int
)

@Serializable
data class Package(
    @SerialName("name")
    val packageName: String,
    val version: String,
    val description: String = "N/A",
    val publisher: Publisher? = null,
    val license: String = "N/A",
    val date: String
)

@Serializable
data class Publisher(
    val email: String,
    val username: String
)
