package star.intellijplugin.pkgfinder.maven

/**
 * Maven 依赖核心信息 GAV
 *
 * @author drawsta
 * @LastModified: 2025-01-20
 * @since 2025-01-20
 */
abstract class Dependency(
    open val groupId: String,
    open val artifactId: String,
    open val version: String
) {
    // 不参与序列化的父类，必须有一个无参构造
    // [NON_SERIALIZABLE_PARENT_MUST_HAVE_NOARG_CTOR] Impossible to make this class serializable
    // because its parent is not serializable and does not have exactly one constructor without parameters
    constructor() : this("", "", "")

    fun getGradleGroovyDeclaration(dependencyScope: DependencyScope): String =
        "${dependencyScope.gradleScopeConfig} '$groupId:$artifactId:$version'"

    fun getGradleKotlinDeclaration(dependencyScope: DependencyScope): String =
        """${dependencyScope.gradleScopeConfig}("$groupId:$artifactId:$version")"""

    fun getMavenDeclaration(dependencyScope: DependencyScope): String {
        val base = """
            <dependency>
                <groupId>$groupId</groupId>
                <artifactId>$artifactId</artifactId>
                <version>$version</version>
        """.trimIndent()

        val scopePart = dependencyScope.mavenScopeConfig?.let {
            "    <scope>$it</scope>\n"
        } ?: ""

        return if (scopePart.isNotEmpty()) {
            "$base\n$scopePart</dependency>"
        } else {
            "$base\n</dependency>"
        }
    }
}
