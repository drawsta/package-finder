package star.intellijplugin.pkgfinder.npm

import kotlinx.serialization.json.Json
import star.intellijplugin.pkgfinder.util.HttpRequestHelper
import star.intellijplugin.pkgfinder.util.showErrorDialog

/**
 * refer Npm Registry API Doc: https://github.com/npm/registry/blob/main/docs/REGISTRY-API.md
 *
 * @author drawsta
 * @LastModified: 2025-07-16
 * @since 2025-01-27
 */
object NpmRegistryService {

    private const val NPM_PACKAGE_SEARCH_ENDPOINT = "https://registry.npmjs.com/-/v1/search"

    fun search(keyword: String): List<NpmObject> {
        val url = npmPackageSearchUrl(keyword)

        return when (val result = HttpRequestHelper.getForObject(url, ::parseResponse)) {
            is HttpRequestHelper.RequestResult.Success -> {
                result.data?.objects ?: emptyList()
            }

            is HttpRequestHelper.RequestResult.Error -> {
                showErrorDialog(result.exception.localizedMessage)
                emptyList()
            }
        }
    }

    private fun parseResponse(responseBody: String): NpmRegistrySearchResult {
        val json = Json { ignoreUnknownKeys = true } // 反序列化时忽略掉类中不存在的属性
        return json.decodeFromString(NpmRegistrySearchResult.serializer(), responseBody)
    }

    private fun npmPackageSearchUrl(text: String): String {
        return "$NPM_PACKAGE_SEARCH_ENDPOINT?text=$text"
    }
}
