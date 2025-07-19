package star.intellijplugin.pkgfinder

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.junit.Test
import star.intellijplugin.pkgfinder.gradle.GradlePluginInfo
import java.io.IOException

/**
 * ref: https://plugins.gradle.org/
 *
 * @author drawsta
 * @LastModified: 2025-02-05
 * @since 2025-02-05
 */
class GradlePluginPortalServiceTest {
    @Test
    fun `test search`() {
        val searchKeyword = "org.springframework.boot"
        val url = "https://plugins.gradle.org/search?term=$searchKeyword"
        val pluginInfos = mutableListOf<GradlePluginInfo>()
        search(url, { it.select("#search-results tbody tr") }) { elements ->
            if (elements.size == 1 && elements.first().select(".plugin-id a").isEmpty()) {
                println("No plugins found or invalid data, skipping parsing.")
                return@search
            }

            for (element in elements) {
                val pluginName = element.select(".plugin-id a").text()
                val latestVersion = element.select(".latest-version").text()
                val description = element.select("p").text()
                val releaseDate = element.select(".date").text()
                pluginInfos.add(GradlePluginInfo(pluginName, latestVersion, releaseDate, description))
            }

            println(pluginInfos)
        }
    }

    @Test
    fun `get other versions`() {
        val searchKeyword = "org.springframework.boot"
        val otherVersions = mutableListOf<String>()
        search(
            "https://plugins.gradle.org/plugin/$searchKeyword",
            { it.select("div.other-versions.dropdown .dropdown-menu li") }) {
            for (element in it) {
                val version = element.select("a").text()
                otherVersions.add(version)
            }
        }
        println(otherVersions)
    }

    private fun search(url: String, selectAction: (Document) -> List<Element>, parseAction: (List<Element>) -> Unit) {
        try {
            val document: Document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36")
                .timeout(10000)
                .get()
            val elements = selectAction(document)
            parseAction(elements)
        } catch (e: IOException) {
            println("Error fetching the page: ${e.message}")
        }
    }
}
