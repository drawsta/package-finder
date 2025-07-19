package star.intellijplugin.pkgfinder.maven

/**
 * 依赖声明格式
 *
 * @author drawsta
 * @LastModified: 2025-07-03
 * @since 2025-02-01
 */
enum class DependencyFormat(val displayName: String) {
    MavenDeclaration("Maven"),
    GradleGroovyDeclaration("Gradle (short)"),
    GradleKotlinDeclaration("Gradle (Kotlin)")
}
