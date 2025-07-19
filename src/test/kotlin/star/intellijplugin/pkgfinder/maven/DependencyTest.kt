package star.intellijplugin.pkgfinder.maven

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author drawsta
 * @LastModified: 2025-07-03
 * @since 2025-01-26
 */
class DependencyTest {

    @Test
    fun getGradleGroovyDeclarationCompile() {
        val instance = object : Dependency("org.projectlombok", "lombok", "1.18.36") {}
        assertEquals(
            "implementation 'org.projectlombok:lombok:1.18.36'",
            instance.getGradleGroovyDeclaration(DependencyScope.COMPILE)
        )
    }

    @Test
    fun getGradleGroovyDeclarationTest() {
        val instance = object : Dependency("junit", "junit", "4.13.2") {}
        assertEquals(
            "testImplementation 'junit:junit:4.13.2'",
            instance.getGradleGroovyDeclaration(DependencyScope.TEST)
        )
    }

    @Test
    fun getGradleGroovyDeclarationProvided() {
        val instance = object : Dependency("javax.servlet", "javax.servlet-api", "4.0.1") {}
        assertEquals(
            "compileOnly 'javax.servlet:javax.servlet-api:4.0.1'",
            instance.getGradleGroovyDeclaration(DependencyScope.PROVIDED)
        )
    }

    @Test
    fun getGradleGroovyDeclarationRuntime() {
        val instance = object : Dependency("mysql", "mysql-connector-java", "8.0.33") {}
        assertEquals(
            "runtimeOnly 'mysql:mysql-connector-java:8.0.33'",
            instance.getGradleGroovyDeclaration(DependencyScope.RUNTIME)
        )
    }

    @Test
    fun getGradleKotlinDeclarationCompile() {
        val instance = object : Dependency("org.projectlombok", "lombok", "1.18.36") {}
        assertEquals(
            """implementation("org.projectlombok:lombok:1.18.36")""",
            instance.getGradleKotlinDeclaration(DependencyScope.COMPILE)
        )
    }

    @Test
    fun getGradleKotlinDeclarationTest() {
        val instance = object : Dependency("junit", "junit", "4.13.2") {}
        assertEquals(
            """testImplementation("junit:junit:4.13.2")""",
            instance.getGradleKotlinDeclaration(DependencyScope.TEST)
        )
    }

    @Test
    fun getGradleKotlinDeclarationProvided() {
        val instance = object : Dependency("javax.servlet", "javax.servlet-api", "4.0.1") {}
        assertEquals(
            """compileOnly("javax.servlet:javax.servlet-api:4.0.1")""",
            instance.getGradleKotlinDeclaration(DependencyScope.PROVIDED)
        )
    }

    @Test
    fun getGradleKotlinDeclarationRuntime() {
        val instance = object : Dependency("mysql", "mysql-connector-java", "8.0.33") {}
        assertEquals(
            """runtimeOnly("mysql:mysql-connector-java:8.0.33")""",
            instance.getGradleKotlinDeclaration(DependencyScope.RUNTIME)
        )
    }

    @Test
    fun getMavenDeclarationCompile() {
        val instance = object : Dependency("org.slf4j", "slf4j-api", "2.0.16") {}
        assertEquals(
            """
                <dependency>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-api</artifactId>
                    <version>2.0.16</version>
                </dependency>
            """.trimIndent(),
            instance.getMavenDeclaration(DependencyScope.COMPILE)
        )
    }

    @Test
    fun getMavenDeclarationTest() {
        val instance = object : Dependency("junit", "junit", "4.13.2") {}
        assertEquals(
            """
                <dependency>
                    <groupId>junit</groupId>
                    <artifactId>junit</artifactId>
                    <version>4.13.2</version>
                    <scope>test</scope>
                </dependency>
            """.trimIndent(),
            instance.getMavenDeclaration(DependencyScope.TEST)
        )
    }

    @Test
    fun getMavenDeclarationProvided() {
        val instance = object : Dependency("javax.servlet", "javax.servlet-api", "4.0.1") {}
        assertEquals(
            """
                <dependency>
                    <groupId>javax.servlet</groupId>
                    <artifactId>javax.servlet-api</artifactId>
                    <version>4.0.1</version>
                    <scope>provided</scope>
                </dependency>
            """.trimIndent(),
            instance.getMavenDeclaration(DependencyScope.PROVIDED)
        )
    }

    @Test
    fun getMavenDeclarationRuntime() {
        val instance = object : Dependency("mysql", "mysql-connector-java", "8.0.33") {}
        assertEquals(
            """
                <dependency>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                    <version>8.0.33</version>
                    <scope>runtime</scope>
                </dependency>
            """.trimIndent(),
            instance.getMavenDeclaration(DependencyScope.RUNTIME)
        )
    }
}
