package star.intellijplugin.pkgfinder.gradle

import com.intellij.openapi.diagnostic.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import star.intellijplugin.pkgfinder.util.showErrorDialog
import java.io.IOException

/**
 * @author drawsta
 * @LastModified: 2025-07-20
 * @since 2025-07-15
 */
object GradlePluginPortalService {

    private val log = Logger.getInstance(javaClass)

    private const val GRADLE_PLUGIN_PORTAL_URL = "https://plugins.gradle.org"

    private const val USER_AGENT =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36"

    fun searchPage(pageLink: String): Triple<List<GradlePluginInfo>, String, String> {
        return search("$GRADLE_PLUGIN_PORTAL_URL$pageLink")
    }

    fun searchBy(text: String): Triple<List<GradlePluginInfo>, String, String> {
        return search("$GRADLE_PLUGIN_PORTAL_URL/search?term=$text")
    }

    private fun search(url: String): Triple<List<GradlePluginInfo>, String, String> {
        val document: Document = try {
            Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(10_000)
                .get()
        } catch (e: IOException) {
            log.error("Failed to load gradle plugin page data", e)
            showErrorDialog(e.localizedMessage)
            return Triple(emptyList(), "", "")
        }

        val elements: List<Element> = document.select("#search-results tbody tr")
        if (elements.size == 1 && elements.first().select(".plugin-id a").isEmpty()) {
            log.info("No plugins found.")
            return Triple(emptyList(), "", "")
        }

        val pluginInfos = elements.map { element ->
            GradlePluginInfo(
                pluginName = element.select(".plugin-id a").text(),
                latestVersion = element.select(".latest-version").text(),
                releaseDate = element.select(".date").text(),
                description = element.select("p").text()
            )
        }

        val pageLinks = document.select("div.page-link.clearfix a.btn.btn-default")
        var prevHref = ""
        var nextHref = ""
        for (element in pageLinks) {
            val href = element.attr("href")
            when (element.text()) {
                "Previous" -> prevHref = href
                "Next" -> nextHref = href
            }
        }
        return Triple(pluginInfos, prevHref, nextHref)
    }
}
