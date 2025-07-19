package star.intellijplugin.pkgfinder.maven

/**
 * 依赖作用域
 *
 * @author drawsta
 * @LastModified: 2025-07-03
 * @since 2025-07-03
 */
enum class DependencyScope(val displayName: String, val gradleScopeConfig: String, val mavenScopeConfig: String?) {
    COMPILE("Compile", "implementation", null),
    TEST("Test", "testImplementation", "test"),
    PROVIDED("Provided", "compileOnly", "provided"),
    RUNTIME("Runtime", "runtimeOnly", "runtime")
}
