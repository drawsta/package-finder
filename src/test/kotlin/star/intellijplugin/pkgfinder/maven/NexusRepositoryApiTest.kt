package star.intellijplugin.pkgfinder.maven

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author drawsta
 * @LastModified: 2025-07-07
 * @since 2025-07-07
 */
class NexusRepositoryApiTest {
    @Test
    fun `test search`() {
        val searchKeyword = "org.junit"
        // 测试结尾加了斜杠的情况
        val host = "http://localhost:8081/"
        val url = "${host.trimEnd('/')}/service/rest/v1/search?q=$searchKeyword"
        assertEquals("http://localhost:8081/service/rest/v1/search?q=org.junit", url)
    }
}
