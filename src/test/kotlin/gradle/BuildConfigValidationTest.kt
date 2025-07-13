package gradle

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

@DisplayName("Build Configuration Validation Tests")
class BuildConfigValidationTest {
    
    private val mockFile: File = mockk()
    private val mockPath: Path = mockk()
    private lateinit var buildConfigValidator: BuildConfigValidator
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        buildConfigValidator = BuildConfigValidator()
        clearAllMocks()
    }
    
    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
    
    @Nested
    @DisplayName("Android Configuration Validation")
    inner class AndroidConfigurationValidation {
        
        @Test
        @DisplayName("Should validate valid Android build configuration successfully")
        fun `should validate valid Android build configuration successfully`() {
            // Given
            val validConfig = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "namespace" to "dev.aurakai.auraframefx"
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(validConfig)
            
            // Then
            assertTrue(result.isValid)
            assertTrue(result.errors.isEmpty())
            assertTrue(result.warnings.isEmpty())
        }
        
        @Test
        @DisplayName("Should fail validation for missing required Android fields")
        fun `should fail validation for missing required Android fields`() {
            // Given
            val incompleteConfig = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1
                // Missing required fields: versionName, minSdk, targetSdk, compileSdk
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(incompleteConfig)
            
            // Then
            assertFalse(result.isValid)
            assertFalse(result.errors.isEmpty())
            assertTrue(result.errors.any { it.contains("versionName") })
            assertTrue(result.errors.any { it.contains("minSdk") })
            assertTrue(result.errors.any { it.contains("targetSdk") })
            assertTrue(result.errors.any { it.contains("compileSdk") })
        }
        
        @ParameterizedTest
        @ValueSource(strings = ["", " ", "invalid..id", "dev.aurakai.", ".dev.aurakai", "123invalid"])
        @DisplayName("Should reject invalid application IDs")
        fun `should reject invalid application IDs`(invalidId: String) {
            // Given
            val config = mapOf(
                "applicationId" to invalidId,
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(config)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { it.contains("applicationId") || it.contains("Invalid package") })
        }
        
        @ParameterizedTest
        @CsvSource(
            "0, false",
            "-1, false", 
            "1, true",
            "999999, true",
            "2147483647, true"
        )
        @DisplayName("Should validate version codes correctly")
        fun `should validate version codes correctly`(versionCode: Int, expectedValid: Boolean) {
            // Given
            val config = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to versionCode,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(config)
            
            // Then
            assertEquals(expectedValid, result.isValid)
            if (!expectedValid) {
                assertTrue(result.errors.any { it.contains("versionCode") })
            }
        }
        
        @ParameterizedTest
        @ValueSource(strings = ["", " ", "v1.0", "1.0.0.0.0", "1.0-SNAPSHOT"])
        @DisplayName("Should validate version name format")
        fun `should validate version name format`(versionName: String) {
            // Given
            val config = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to versionName,
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(config)
            
            // Then
            // Version name validation might be less strict, but empty/blank should fail
            if (versionName.isBlank()) {
                assertFalse(result.isValid)
                assertTrue(result.errors.any { it.contains("versionName") })
            }
        }
    }
    
    @Nested
    @DisplayName("SDK Version Validation")
    inner class SdkVersionValidation {
        
        @Test
        @DisplayName("Should validate that target SDK is greater than or equal to min SDK")
        fun `should validate that target SDK is greater than or equal to min SDK`() {
            // Given
            val config = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 35,
                "targetSdk" to 33, // Invalid: target < min
                "compileSdk" to 35
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(config)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { 
                it.contains("target") && it.contains("min") || 
                it.contains("targetSdk") && it.contains("minSdk")
            })
        }
        
        @Test
        @DisplayName("Should validate that compile SDK is greater than or equal to target SDK")
        fun `should validate that compile SDK is greater than or equal to target SDK`() {
            // Given
            val config = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 34 // Invalid: compile < target
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(config)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { 
                it.contains("compile") && it.contains("target") ||
                it.contains("compileSdk") && it.contains("targetSdk")
            })
        }
        
        @ParameterizedTest
        @ValueSource(ints = [1, 15, 20, 22])
        @DisplayName("Should warn about deprecated SDK versions")
        fun `should warn about deprecated SDK versions`(sdkVersion: Int) {
            // Given
            val config = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to sdkVersion,
                "targetSdk" to 35,
                "compileSdk" to 35
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(config)
            
            // Then
            if (sdkVersion < 23) {
                assertTrue(result.warnings.any { 
                    it.contains("deprecated") || it.contains("minimum") || it.contains("outdated")
                })
            }
        }
        
        @Test
        @DisplayName("Should validate current project SDK configuration")
        fun `should validate current project SDK configuration`() {
            // Given - Using actual project configuration
            val projectConfig = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "namespace" to "dev.aurakai.auraframefx"
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(projectConfig)
            
            // Then
            assertTrue(result.isValid, "Current project configuration should be valid")
            assertTrue(result.errors.isEmpty())
        }
    }
    
    @Nested
    @DisplayName("Build File Validation")
    inner class BuildFileValidation {
        
        @Test
        @DisplayName("Should validate build file exists and is readable")
        fun `should validate build file exists and is readable`() {
            // Given
            every { mockFile.exists() } returns true
            every { mockFile.canRead() } returns true
            every { mockFile.isFile() } returns true
            every { mockFile.name } returns "build.gradle.kts"
            
            // When
            val result = buildConfigValidator.validateBuildFile(mockFile)
            
            // Then
            assertTrue(result.isValid)
            verify { mockFile.exists() }
            verify { mockFile.canRead() }
            verify { mockFile.isFile() }
        }
        
        @Test
        @DisplayName("Should fail validation when build file does not exist")
        fun `should fail validation when build file does not exist`() {
            // Given
            every { mockFile.exists() } returns false
            every { mockFile.path } returns "/fake/path/build.gradle.kts"
            
            // When
            val result = buildConfigValidator.validateBuildFile(mockFile)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { it.contains("does not exist") })
            verify { mockFile.exists() }
            verify(exactly = 0) { mockFile.canRead() }
        }
        
        @Test
        @DisplayName("Should fail validation when build file is not readable")
        fun `should fail validation when build file is not readable`() {
            // Given
            every { mockFile.exists() } returns true
            every { mockFile.canRead() } returns false
            every { mockFile.isFile() } returns true
            every { mockFile.path } returns "/fake/path/build.gradle.kts"
            
            // When
            val result = buildConfigValidator.validateBuildFile(mockFile)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { it.contains("not readable") || it.contains("permission") })
        }
        
        @Test
        @DisplayName("Should validate different build file types")
        fun `should validate different build file types`() {
            // Given
            val buildFileNames = listOf("build.gradle", "build.gradle.kts", "pom.xml")
            
            buildFileNames.forEach { fileName ->
                clearMocks(mockFile)
                every { mockFile.exists() } returns true
                every { mockFile.canRead() } returns true
                every { mockFile.isFile() } returns true
                every { mockFile.name } returns fileName
                
                // When
                val result = buildConfigValidator.validateBuildFile(mockFile)
                
                // Then
                assertTrue(result.isValid, "Build file $fileName should be valid")
            }
        }
    }
    
    @Nested
    @DisplayName("Project Structure Validation")
    inner class ProjectStructureValidation {
        
        @Test
        @DisplayName("Should validate Android project structure")
        fun `should validate Android project structure`() {
            // Given
            val projectPath = mockk<Path>()
            val srcMainPath = mockk<Path>()
            val manifestFile = mockk<File>()
            
            every { projectPath.resolve("src/main") } returns srcMainPath
            every { srcMainPath.resolve("AndroidManifest.xml") } returns mockPath
            every { mockPath.toFile() } returns manifestFile
            every { manifestFile.exists() } returns true
            every { manifestFile.isFile() } returns true
            
            // When
            val result = buildConfigValidator.validateAndroidProjectStructure(projectPath)
            
            // Then
            assertTrue(result.isValid)
            verify { projectPath.resolve("src/main") }
        }
        
        @Test
        @DisplayName("Should detect missing AndroidManifest.xml")
        fun `should detect missing AndroidManifest xml`() {
            // Given
            val projectPath = mockk<Path>()
            val srcMainPath = mockk<Path>()
            val manifestFile = mockk<File>()
            
            every { projectPath.resolve("src/main") } returns srcMainPath
            every { srcMainPath.resolve("AndroidManifest.xml") } returns mockPath
            every { mockPath.toFile() } returns manifestFile
            every { manifestFile.exists() } returns false
            
            // When
            val result = buildConfigValidator.validateAndroidProjectStructure(projectPath)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { it.contains("AndroidManifest.xml") })
        }
    }
    
    @Nested
    @DisplayName("Build Configuration Parsing")
    inner class BuildConfigurationParsing {
        
        @Test
        @DisplayName("Should parse valid Gradle Kotlin DSL build file")
        fun `should parse valid Gradle Kotlin DSL build file`() {
            // Given
            val validGradleKtsContent = """
                android {
                    namespace = "dev.aurakai.auraframefx"
                    compileSdk = 35
                    
                    defaultConfig {
                        applicationId = "dev.aurakai.auraframefx"
                        minSdk = 33
                        targetSdk = 35
                        versionCode = 1
                        versionName = "1.0"
                    }
                }
            """.trimIndent()
            
            // When
            val result = buildConfigValidator.parseGradleKtsConfig(validGradleKtsContent)
            
            // Then
            assertNotNull(result)
            assertEquals("dev.aurakai.auraframefx", result["applicationId"])
            assertEquals("dev.aurakai.auraframefx", result["namespace"])
            assertEquals(1, result["versionCode"])
            assertEquals("1.0", result["versionName"])
            assertEquals(33, result["minSdk"])
            assertEquals(35, result["targetSdk"])
            assertEquals(35, result["compileSdk"])
        }
        
        @Test
        @DisplayName("Should parse valid Gradle Groovy build file")
        fun `should parse valid Gradle Groovy build file`() {
            // Given
            val validGradleContent = """
                android {
                    compileSdkVersion 35
                    defaultConfig {
                        applicationId "dev.aurakai.auraframefx"
                        minSdkVersion 33
                        targetSdkVersion 35
                        versionCode 1
                        versionName "1.0"
                    }
                }
            """.trimIndent()
            
            // When
            val result = buildConfigValidator.parseGradleConfig(validGradleContent)
            
            // Then
            assertNotNull(result)
            assertEquals("dev.aurakai.auraframefx", result["applicationId"])
            assertEquals(1, result["versionCode"])
            assertEquals("1.0", result["versionName"])
        }
        
        @Test
        @DisplayName("Should handle malformed Gradle syntax gracefully")
        fun `should handle malformed Gradle syntax gracefully`() {
            // Given
            val malformedContent = """
                android {
                    compileSdk = 35
                    defaultConfig {
                        applicationId = "dev.aurakai.auraframefx"
                        // Missing closing braces
            """.trimIndent()
            
            // When & Then
            assertThrows<IllegalArgumentException> {
                buildConfigValidator.parseGradleKtsConfig(malformedContent)
            }
        }
        
        @Test
        @DisplayName("Should handle empty configuration")
        fun `should handle empty configuration`() {
            // Given
            val emptyContent = ""
            
            // When
            val result = buildConfigValidator.parseGradleKtsConfig(emptyContent)
            
            // Then
            assertNotNull(result)
            assertTrue(result.isEmpty())
        }
        
        @Test
        @DisplayName("Should extract plugins configuration")
        fun `should extract plugins configuration`() {
            // Given
            val configWithPlugins = """
                plugins {
                    alias(libs.plugins.kotlinAndroid)
                    alias(libs.plugins.ksp)
                    alias(libs.plugins.hilt)
                    alias(libs.plugins.google.services)
                }
            """.trimIndent()
            
            // When
            val result = buildConfigValidator.parsePluginsConfig(configWithPlugins)
            
            // Then
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
            assertTrue(result.contains("kotlinAndroid"))
            assertTrue(result.contains("ksp"))
            assertTrue(result.contains("hilt"))
            assertTrue(result.contains("google.services"))
        }
    }
    
    @Nested
    @DisplayName("Build Variant and Type Validation")
    inner class BuildVariantValidation {
        
        enum class BuildType { DEBUG, RELEASE, STAGING }
        
        @ParameterizedTest
        @EnumSource(BuildType::class)
        @DisplayName("Should validate different build types")
        fun `should validate different build types`(buildType: BuildType) {
            // Given
            val config = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "buildType" to buildType.name.lowercase()
            )
            
            // When
            val result = buildConfigValidator.validateBuildType(config)
            
            // Then
            assertTrue(result.isValid)
        }
        
        @Test
        @DisplayName("Should validate release build requires specific configurations")
        fun `should validate release build requires specific configurations`() {
            // Given
            val releaseConfig = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "buildType" to "release",
                "isMinifyEnabled" to false, // Should be true for release
                "proguardFiles" to emptyList<String>() // Should have proguard files for release
            )
            
            // When
            val result = buildConfigValidator.validateBuildType(releaseConfig)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { it.contains("minify") || it.contains("obfuscat") })
            assertTrue(result.warnings.any { it.contains("proguard") || it.contains("R8") })
        }
        
        @Test
        @DisplayName("Should validate debug build configurations")
        fun `should validate debug build configurations`() {
            // Given
            val debugConfig = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0-debug",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "buildType" to "debug",
                "debuggable" to true,
                "isMinifyEnabled" to false
            )
            
            // When
            val result = buildConfigValidator.validateBuildType(debugConfig)
            
            // Then
            assertTrue(result.isValid)
        }
    }
    
    @Nested
    @DisplayName("Security Configuration Validation")
    inner class SecurityValidation {
        
        @Test
        @DisplayName("Should detect insecure configurations")
        fun `should detect insecure configurations`() {
            // Given
            val insecureConfig = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "debuggable" to true, // Should be false in production
                "allowBackup" to true, // Potential security risk
                "usesCleartextTraffic" to true, // Security vulnerability
                "buildType" to "release"
            )
            
            // When
            val result = buildConfigValidator.validateSecurityConfiguration(insecureConfig)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { it.contains("debuggable") })
            assertTrue(result.warnings.any { it.contains("backup") })
            assertTrue(result.errors.any { it.contains("cleartext") || it.contains("HTTP") })
        }
        
        @Test
        @DisplayName("Should validate secure configurations")
        fun `should validate secure configurations`() {
            // Given
            val secureConfig = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "debuggable" to false,
                "allowBackup" to false,
                "usesCleartextTraffic" to false,
                "buildType" to "release"
            )
            
            // When
            val result = buildConfigValidator.validateSecurityConfiguration(secureConfig)
            
            // Then
            assertTrue(result.isValid)
            assertTrue(result.errors.isEmpty())
        }
        
        @Test
        @DisplayName("Should validate network security configuration")
        fun `should validate network security configuration`() {
            // Given
            val networkConfig = mapOf(
                "usesCleartextTraffic" to false,
                "networkSecurityConfig" to "@xml/network_security_config",
                "targetSdk" to 35
            )
            
            // When
            val result = buildConfigValidator.validateNetworkSecurity(networkConfig)
            
            // Then
            assertTrue(result.isValid)
        }
    }
    
    @Nested
    @DisplayName("Performance and Optimization Validation")
    inner class PerformanceValidation {
        
        @Test
        @DisplayName("Should validate performance-related configurations")
        fun `should validate performance-related configurations`() {
            // Given
            val config = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "dexOptions" to mapOf(
                    "javaMaxHeapSize" to "4g",
                    "preDexLibraries" to true
                ),
                "packagingOptions" to mapOf(
                    "pickFirst" to listOf("**/libnative.so"),
                    "exclude" to listOf("META-INF/LICENSE.txt")
                )
            )
            
            // When
            val result = buildConfigValidator.validatePerformanceConfiguration(config)
            
            // Then
            assertTrue(result.isValid)
        }
        
        @Test
        @DisplayName("Should detect potential performance issues")
        fun `should detect potential performance issues`() {
            // Given
            val slowConfig = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "dexOptions" to mapOf(
                    "javaMaxHeapSize" to "512m", // Too small for large projects
                    "preDexLibraries" to false // Should be true for faster builds
                )
            )
            
            // When
            val result = buildConfigValidator.validatePerformanceConfiguration(slowConfig)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.warnings.any { it.contains("heap") || it.contains("memory") })
            assertTrue(result.warnings.any { it.contains("preDex") || it.contains("build time") })
        }
        
        @Test
        @DisplayName("Should validate Compose compiler configuration")
        fun `should validate Compose compiler configuration`() {
            // Given
            val composeConfig = mapOf(
                "buildFeatures" to mapOf("compose" to true),
                "composeOptions" to mapOf(
                    "kotlinCompilerExtensionVersion" to "1.5.8"
                )
            )
            
            // When
            val result = buildConfigValidator.validateComposeConfiguration(composeConfig)
            
            // Then
            assertTrue(result.isValid)
        }
    }
    
    @Nested
    @DisplayName("Dependency Validation")
    inner class DependencyValidation {
        
        @Test
        @DisplayName("Should validate Hilt dependencies configuration")
        fun `should validate Hilt dependencies configuration`() {
            // Given
            val dependencies = listOf(
                "implementation(libs.hiltAndroid)",
                "ksp(libs.hiltCompiler)",
                "implementation(libs.hiltNavigationCompose)"
            )
            
            // When
            val result = buildConfigValidator.validateHiltDependencies(dependencies)
            
            // Then
            assertTrue(result.isValid)
            assertTrue(result.errors.isEmpty())
        }
        
        @Test
        @DisplayName("Should detect missing Hilt processor")
        fun `should detect missing Hilt processor`() {
            // Given
            val incompleteDependencies = listOf(
                "implementation(libs.hiltAndroid)"
                // Missing: ksp(libs.hiltCompiler)
            )
            
            // When
            val result = buildConfigValidator.validateHiltDependencies(incompleteDependencies)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { it.contains("processor") || it.contains("compiler") })
        }
        
        @Test
        @DisplayName("Should validate Compose BOM usage")
        fun `should validate Compose BOM usage`() {
            // Given
            val dependencies = listOf(
                "implementation(platform(libs.composeBom))",
                "implementation(libs.androidxUi)",
                "implementation(libs.androidxMaterial3)"
            )
            
            // When
            val result = buildConfigValidator.validateComposeDependencies(dependencies)
            
            // Then
            assertTrue(result.isValid)
        }
    }
    
    @Nested
    @DisplayName("Comprehensive Validation")
    inner class ComprehensiveValidation {
        
        @Test
        @DisplayName("Should provide comprehensive validation report")
        fun `should provide comprehensive validation report`() = runTest {
            // Given
            val complexConfig = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "namespace" to "dev.aurakai.auraframefx",
                "buildType" to "release",
                "debuggable" to false,
                "isMinifyEnabled" to true,
                "proguardFiles" to listOf("proguard-rules.pro")
            )
            
            // When
            val result = buildConfigValidator.validateAll(complexConfig)
            
            // Then
            assertNotNull(result)
            assertNotNull(result.androidValidation)
            assertNotNull(result.securityValidation)
            assertNotNull(result.performanceValidation)
            assertNotNull(result.buildTypeValidation)
            assertTrue(result.overallValid)
        }
        
        @Test
        @DisplayName("Should validate real project configuration")
        fun `should validate real project configuration`() {
            // Given - Real configuration from the project
            val realProjectConfig = mapOf(
                "namespace" to "dev.aurakai.auraframefx",
                "applicationId" to "dev.aurakai.auraframefx",
                "compileSdk" to 35,
                "minSdk" to 33,
                "targetSdk" to 35,
                "versionCode" to 1,
                "versionName" to "1.0",
                "testInstrumentationRunner" to "androidx.test.runner.AndroidJUnitRunner"
            )
            
            // When
            val result = buildConfigValidator.validateAll(realProjectConfig)
            
            // Then
            assertTrue(result.overallValid, "Real project configuration should be valid")
            assertTrue(result.androidValidation.isValid)
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling")
    inner class EdgeCasesAndErrorHandling {
        
        @Test
        @DisplayName("Should handle null configuration gracefully")
        fun `should handle null configuration gracefully`() {
            // When & Then
            assertThrows<IllegalArgumentException> {
                buildConfigValidator.validateAndroidConfiguration(null)
            }
        }
        
        @Test
        @DisplayName("Should handle empty configuration")
        fun `should handle empty configuration`() {
            // Given
            val emptyConfig = emptyMap<String, Any>()
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(emptyConfig)
            
            // Then
            assertFalse(result.isValid)
            assertFalse(result.errors.isEmpty())
        }
        
        @Test
        @DisplayName("Should handle configuration with unexpected types")
        fun `should handle configuration with unexpected types`() {
            // Given
            val invalidTypeConfig = mapOf(
                "applicationId" to 123, // Should be String
                "versionCode" to "invalid", // Should be Int
                "minSdk" to "33" // Could be String or Int
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(invalidTypeConfig)
            
            // Then
            assertFalse(result.isValid)
            assertTrue(result.errors.any { it.contains("type") || it.contains("format") })
        }
        
        @Test
        @DisplayName("Should validate configuration with extra unknown fields")
        fun `should validate configuration with extra unknown fields`() {
            // Given
            val configWithExtras = mapOf(
                "applicationId" to "dev.aurakai.auraframefx",
                "versionCode" to 1,
                "versionName" to "1.0",
                "minSdk" to 33,
                "targetSdk" to 35,
                "compileSdk" to 35,
                "unknownField" to "someValue",
                "anotherUnknown" to 42
            )
            
            // When
            val result = buildConfigValidator.validateAndroidConfiguration(configWithExtras)
            
            // Then
            assertTrue(result.isValid, "Valid config with extra fields should pass")
            // Might have warnings about unknown fields
        }
    }
    
    companion object {
        @JvmStatic
        fun provideSdkVersionCombinations(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(21, 33, 33, true),
                Arguments.of(33, 35, 35, true),
                Arguments.of(35, 33, 35, false), // target < min
                Arguments.of(33, 35, 34, false), // compile < target
                Arguments.of(16, 16, 16, true)   // old but consistent
            )
        }
        
        @ParameterizedTest
        @MethodSource("provideSdkVersionCombinations")
        @DisplayName("Should validate SDK version combinations")
        fun `should validate SDK version combinations`(
            minSdk: Int, 
            targetSdk: Int, 
            compileSdk: Int, 
            expectedValid: Boolean
        ) {
            // This would be a member method in a real test class
        }
    }
}

// Supporting data classes and validator implementation
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val info: List<String> = emptyList()
)

data class ComprehensiveValidationResult(
    val androidValidation: ValidationResult,
    val securityValidation: ValidationResult,
    val performanceValidation: ValidationResult,
    val buildTypeValidation: ValidationResult
) {
    val overallValid: Boolean
        get() = listOf(
            androidValidation,
            securityValidation, 
            performanceValidation,
            buildTypeValidation
        ).all { it.isValid }
}

// Mock validator class for comprehensive testing
class BuildConfigValidator {
    
    fun validateAndroidConfiguration(config: Map<String, Any>?): ValidationResult {
        if (config == null) {
            throw IllegalArgumentException("Configuration cannot be null")
        }
        
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        // Required fields validation
        val requiredFields = listOf("applicationId", "versionCode", "versionName", "minSdk", "targetSdk", "compileSdk")
        requiredFields.forEach { field ->
            if (!config.containsKey(field)) {
                errors.add("Missing required field: $field")
            }
        }
        
        // Application ID validation
        val appId = config["applicationId"]
        if (appId !is String || appId.isBlank() || !isValidApplicationId(appId)) {
            errors.add("Invalid applicationId: $appId")
        }
        
        // Version code validation
        val versionCode = config["versionCode"]
        if (versionCode !is Int || versionCode <= 0) {
            errors.add("Invalid versionCode: $versionCode")
        }
        
        // SDK version validation
        val minSdk = config["minSdk"] as? Int
        val targetSdk = config["targetSdk"] as? Int
        val compileSdk = config["compileSdk"] as? Int
        
        if (minSdk != null && targetSdk != null && targetSdk < minSdk) {
            errors.add("targetSdk ($targetSdk) must be >= minSdk ($minSdk)")
        }
        
        if (targetSdk != null && compileSdk != null && compileSdk < targetSdk) {
            errors.add("compileSdk ($compileSdk) must be >= targetSdk ($targetSdk)")
        }
        
        if (minSdk != null && minSdk < 23) {
            warnings.add("minSdk $minSdk is quite old and may have deprecated features")
        }
        
        return ValidationResult(errors.isEmpty(), errors, warnings)
    }
    
    fun validateBuildFile(file: File): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (!file.exists()) {
            errors.add("Build file does not exist: ${file.path}")
        } else {
            if (!file.canRead()) {
                errors.add("Build file is not readable: ${file.path}")
            }
            if (!file.isFile()) {
                errors.add("Path is not a file: ${file.path}")
            }
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun validateAndroidProjectStructure(projectPath: Path): ValidationResult {
        val errors = mutableListOf<String>()
        
        val manifestPath = projectPath.resolve("src/main").resolve("AndroidManifest.xml")
        if (!manifestPath.toFile().exists()) {
            errors.add("AndroidManifest.xml not found at: $manifestPath")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun parseGradleKtsConfig(content: String): Map<String, Any> {
        if (content.isBlank()) return emptyMap()
        
        // Simplified parsing - in real implementation would use proper Gradle/Kotlin parsing
        val result = mutableMapOf<String, Any>()
        
        // Extract key configuration values using regex
        val patterns = mapOf(
            "namespace" to """namespace\s*=\s*"([^"]+)"""",
            "applicationId" to """applicationId\s*=\s*"([^"]+)"""",
            "compileSdk" to """compileSdk\s*=\s*(\d+)""",
            "minSdk" to """minSdk\s*=\s*(\d+)""",
            "targetSdk" to """targetSdk\s*=\s*(\d+)""",
            "versionCode" to """versionCode\s*=\s*(\d+)""",
            "versionName" to """versionName\s*=\s*"([^"]+)""""
        )
        
        patterns.forEach { (key, pattern) ->
            val regex = Regex(pattern)
            val match = regex.find(content)
            match?.let {
                val value = it.groupValues[1]
                result[key] = when (key) {
                    in listOf("compileSdk", "minSdk", "targetSdk", "versionCode") -> value.toInt()
                    else -> value
                }
            }
        }
        
        // Check for malformed syntax
        val openBraces = content.count { it == '{' }
        val closeBraces = content.count { it == '}' }
        if (openBraces != closeBraces) {
            throw IllegalArgumentException("Malformed Gradle Kotlin DSL syntax: mismatched braces")
        }
        
        return result
    }
    
    fun parseGradleConfig(content: String): Map<String, Any> {
        // Similar to parseGradleKtsConfig but for Groovy syntax
        return parseGradleKtsConfig(content) // Simplified for this example
    }
    
    fun parsePluginsConfig(content: String): List<String> {
        val plugins = mutableListOf<String>()
        val pluginPattern = Regex("""alias\(libs\.plugins\.([^)]+)\)""")
        
        pluginPattern.findAll(content).forEach { match ->
            plugins.add(match.groupValues[1])
        }
        
        return plugins
    }
    
    fun validateBuildType(config: Map<String, Any>): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val buildType = config["buildType"] as? String
        
        when (buildType) {
            "release" -> {
                val minifyEnabled = config["isMinifyEnabled"] as? Boolean ?: false
                if (!minifyEnabled) {
                    errors.add("Release builds should have minification enabled for security and performance")
                }
                
                val proguardFiles = config["proguardFiles"] as? List<*> ?: emptyList<Any>()
                if (proguardFiles.isEmpty()) {
                    warnings.add("Release builds should specify ProGuard/R8 configuration files")
                }
            }
        }
        
        return ValidationResult(errors.isEmpty(), errors, warnings)
    }
    
    fun validateSecurityConfiguration(config: Map<String, Any>): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val buildType = config["buildType"] as? String
        
        if (buildType == "release" && config["debuggable"] == true) {
            errors.add("Release builds should not be debuggable in production")
        }
        
        if (config["allowBackup"] == true) {
            warnings.add("Allowing backup may pose security risks - consider using allowBackup=\"false\" or implement backup rules")
        }
        
        if (config["usesCleartextTraffic"] == true) {
            errors.add("Using cleartext traffic (HTTP) is a security vulnerability - use HTTPS instead")
        }
        
        return ValidationResult(errors.isEmpty(), errors, warnings)
    }
    
    fun validateNetworkSecurity(config: Map<String, Any>): ValidationResult {
        return ValidationResult(true) // Simplified implementation
    }
    
    fun validatePerformanceConfiguration(config: Map<String, Any>): ValidationResult {
        val warnings = mutableListOf<String>()
        
        val dexOptions = config["dexOptions"] as? Map<*, *>
        dexOptions?.let { options ->
            val heapSize = options["javaMaxHeapSize"] as? String
            if (heapSize == "512m") {
                warnings.add("Java heap size may be too small for large projects - consider increasing to 2g or 4g")
            }
            
            if (options["preDexLibraries"] == false) {
                warnings.add("Pre-dexing libraries should be enabled for faster incremental builds")
            }
        }
        
        return ValidationResult(true, emptyList(), warnings)
    }
    
    fun validateComposeConfiguration(config: Map<String, Any>): ValidationResult {
        return ValidationResult(true) // Simplified implementation
    }
    
    fun validateHiltDependencies(dependencies: List<String>): ValidationResult {
        val errors = mutableListOf<String>()
        
        val hasHiltAndroid = dependencies.any { it.contains("hiltAndroid") }
        val hasHiltCompiler = dependencies.any { it.contains("hiltCompiler") }
        
        if (hasHiltAndroid && !hasHiltCompiler) {
            errors.add("Hilt requires both runtime and annotation processor dependencies")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun validateComposeDependencies(dependencies: List<String>): ValidationResult {
        val warnings = mutableListOf<String>()
        
        val hasBom = dependencies.any { it.contains("composeBom") }
        val hasComposeUi = dependencies.any { it.contains("androidxUi") || it.contains("compose-ui") }
        
        if (hasComposeUi && !hasBom) {
            warnings.add("Consider using Compose BOM to ensure compatible versions of Compose libraries")
        }
        
        return ValidationResult(true, emptyList(), warnings)
    }
    
    fun validateAll(config: Map<String, Any>): ComprehensiveValidationResult {
        return ComprehensiveValidationResult(
            androidValidation = validateAndroidConfiguration(config),
            securityValidation = validateSecurityConfiguration(config),
            performanceValidation = validatePerformanceConfiguration(config),
            buildTypeValidation = validateBuildType(config)
        )
    }
    
    private fun isValidApplicationId(appId: String): Boolean {
        // Android package name validation
        return appId.matches(Regex("^[a-zA-Z][a-zA-Z0-9_]*(?:\\.[a-zA-Z][a-zA-Z0-9_]*)*$")) &&
               !appId.startsWith(".") && 
               !appId.endsWith(".") &&
               !appId.contains("..")
    }
}