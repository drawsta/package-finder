package star.intellijplugin.pkgfinder.maven

import org.junit.Test
import star.intellijplugin.pkgfinder.maven.DependencyService.mavenDownloadUrl
import star.intellijplugin.pkgfinder.maven.DependencyService.mavenSearchUrl

/**
 * @author drawsta
 * @LastModified: 2025-01-23
 * @since 2025-01-23
 */
class DependencyServiceTest {

    @Test
    fun `test mavenSearchUrl`() {
        // 根据 group 查询
        assert(
            mavenSearchUrl(
                "g:org.springframework"
            ) == "https://search.maven.org/solrsearch/select?q=g%3A%22org.springframework%22&core=gav&rows=20&wt=json"
        )
        // 根据 artifact 查询
        assert(
            mavenSearchUrl(
                "a:spring-core"
            ) == "https://search.maven.org/solrsearch/select?q=a%3A%22spring-core%22&core=gav&rows=20&wt=json"
        )
        // 根据 group 和 artifact 查询
        assert(
            mavenSearchUrl(
                "org.springframework:spring-core"
            ) == "https://search.maven.org/solrsearch/select?q=g%3A%22org.springframework%22+AND+a%3A%22spring-core%22&core=gav&rows=20&wt=json"
        )
        // 传入其他可选参数
        assert(
            mavenSearchUrl(
                "org.springframework:spring-core",
                version = "5.3.9",
                packaging = "jar",
                rowsLimit = 10
            ) == "https://search.maven.org/solrsearch/select?q=g%3A%22org.springframework%22+AND+a%3A%22spring-core%22+AND+v%3A%225.3.9%22+AND+p%3A%22jar%22&core=gav&rows=10&wt=json"
        )
    }

    @Test
    fun `test mavenDownloadUrl`() {
        // 构建下载 jar 文件的 URL
        assert(
            mavenDownloadUrl(
                "org.projectlombok", "lombok", "1.18.36", ".jar"
            ) == "https://search.maven.org/remotecontent?filepath=org/projectlombok/lombok/1.18.36/lombok-1.18.36.jar"
        )

        // 构建下载 pom 文件的 URL
        assert(
            mavenDownloadUrl(
                "org.projectlombok", "lombok", "1.18.36", ".pom"
            ) == "https://search.maven.org/remotecontent?filepath=org/projectlombok/lombok/1.18.36/lombok-1.18.36.pom"
        )

        // 构建下载源码 sources.jar 文件的 URL
        assert(
            mavenDownloadUrl(
                "org.projectlombok", "lombok", "1.18.36", "-sources.jar"
            ) == "https://search.maven.org/remotecontent?filepath=org/projectlombok/lombok/1.18.36/lombok-1.18.36-sources.jar"
        )

        // 构建下载 javadoc 文档 javadoc.jar 文件的 URL
        assert(
            mavenDownloadUrl(
                "org.projectlombok", "lombok", "1.18.36", "-javadoc.jar"
            ) == "https://search.maven.org/remotecontent?filepath=org/projectlombok/lombok/1.18.36/lombok-1.18.36-javadoc.jar"
        )
    }
}
