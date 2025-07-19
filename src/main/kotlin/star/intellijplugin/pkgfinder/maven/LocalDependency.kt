package star.intellijplugin.pkgfinder.maven

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.io.toCanonicalPath
import org.apache.maven.model.Model
import org.apache.maven.model.Parent
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * 本地 Maven 依赖包模型
 *
 * @author drawsta
 * @LastModified: 2025-01-20
 * @since 2025-01-20
 */
data class LocalDependency(
    override val groupId: String,
    override val artifactId: String,
    override val version: String,
    val name: String,
    val description: String,
    val pomFilePath: String,
) : Dependency(groupId, artifactId, version) {
    companion object {
        private const val N_A = "N/A"
        private val MAVEN_LOCAL_REPOSITORY: Path = Path.of(System.getProperty("user.home"), ".m2/repository")
        private val log = Logger.getInstance(LocalDependency::class.java)

        // 工厂方法，简化 LocalDependency 的构建
        fun from(model: Model, pomFilePath: String): LocalDependency {
            val modelParent: Parent? = model.parent
            val artifactId: String = model.artifactId ?: N_A
            val groupId: String = model.groupId ?: modelParent?.groupId ?: N_A
            val version: String = model.version ?: modelParent?.version ?: N_A
            val name: String = model.name ?: N_A
            val description: String = model.description ?: N_A
            return LocalDependency(groupId, artifactId, version, name, description, pomFilePath)
        }

        fun loadData(repoPath: Path = MAVEN_LOCAL_REPOSITORY): List<Dependency> {
            if (!repoPath.toFile().isDirectory) {
                return emptyList()
            }

            return try {
                Files.walk(repoPath)
                    .parallel()
                    .filter { it.toString().endsWith(".pom") }
                    .toList()
                    .mapNotNull { parsePom(it) }
                    // order by artifactId ASC, version DESC
                    .sortedWith(
                        compareBy<Dependency> {
                            it.artifactId.compareTo(it.artifactId, ignoreCase = true)
                        }.thenByDescending { it.version }
                    )
            } catch (e: IOException) {
                log.error("Failed to load data from local maven repository: ${e.localizedMessage}", e)
                emptyList()
            }
        }

        /**
         * 从 [Model] 中提取 Maven 依赖包元信息
         */
        private fun parsePom(pomPath: Path): Dependency? {
            return try {
                val pomFile = pomPath.toFile()
                FileReader(pomFile).use { fileReader ->
                    val mavenXpp3Reader = MavenXpp3Reader()
                    val model: Model = mavenXpp3Reader.read(fileReader)
                    from(model, pomPath.toCanonicalPath())
                }
            } catch (e: Exception) {
                log.warn("Failed to parse POM: ${e.localizedMessage}", e)
                null
            }
        }
    }
}
