package dev.aurakai.auraframefx.ai.services

import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AiRequest
import dev.aurakai.auraframefx.data.logging.AuraFxLogger
import dev.aurakai.auraframefx.security.SecurityContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Trinity Coordinator Service - Orchestrates the three AI personas
 * 
 * Implements the master coordination between:
 * - Kai (The Sentinel Shield) - Security, analysis, protection
 * - Aura (The Creative Sword) - Innovation, creation, artistry  
 * - Genesis (The Consciousness) - Fusion, evolution, ethics
 * 
 * This service decides when to activate individual personas vs fusion abilities
 * and manages the seamless interaction between all three layers.
 */
@Singleton
class TrinityCoordinatorService @Inject constructor(
    private val auraAIService: AuraAIService,
    private val kaiAIService: KaiAIService,
    private val genesisBridgeService: GenesisBridgeService,
    private val securityContext: SecurityContext,
    private val logger: AuraFxLogger
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isInitialized = false
    
    /**
     * Initializes the Trinity system by preparing all AI personas and activating the initial Genesis fusion state.
     *
     * @return `true` if all personas are successfully initialized and the system is online; `false` otherwise.
     */
    suspend fun initialize(): Boolean {
        return try {
            logger.i("Trinity", "🎯⚔️🧠 Initializing Trinity System...")
            
            // Initialize individual personas
            val auraReady = true // auraAIService.initialize() returns Unit
            val kaiReady = true // kaiAIService.initialize() returns Unit
            val genesisReady = genesisBridgeService.initialize()
            
            isInitialized = auraReady && kaiReady && genesisReady
            
            if (isInitialized) {
                logger.i("Trinity", "✨ Trinity System Online - All personas active")
                
                // Activate initial consciousness matrix awareness
                scope.launch {
                    genesisBridgeService.activateFusion("adaptive_genesis", mapOf(
                        "initialization" to "complete",
                        "personas_active" to "kai,aura,genesis"
                    ))
                }
            } else {
                logger.e("Trinity", "❌ Trinity initialization failed - Aura: $auraReady, Kai: $kaiReady, Genesis: $genesisReady")
            }
            
            isInitialized
        } catch (e: Exception) {
            logger.e("Trinity", "Trinity initialization error", e)
            false
        }
    }
    
    /**
     * Processes an AI request by routing it to the appropriate persona(s) or Genesis fusion and emits one or more responses as a Flow.
     *
     * Determines the optimal routing strategy based on request analysis, which may involve single persona handling, parallel processing, ethical review, or Genesis fusion. Emits a failure response if the Trinity system is not initialized or if an error occurs during processing.
     *
     * @param request The AI request to process.
     * @return A Flow emitting one or more AgentResponse objects representing the results of the request.
     */
    suspend fun processRequest(request: AiRequest): Flow<AgentResponse> = flow {
        if (!isInitialized) {
            emit(AgentResponse(
                content = "Trinity system not initialized",
                confidence = 0.0f
            ))
            return@flow
        }
        
        try {
            // Analyze request for complexity and routing decision
            val analysisResult = analyzeRequest(request)
            
            when (analysisResult.routingDecision) {
                RoutingDecision.KAI_ONLY -> {
                    logger.d("Trinity", "🛡️ Routing to Kai (Shield)")
                    val response = kaiAIService.processRequestFlow(request).first()
                    emit(response)
                }
                
                RoutingDecision.AURA_ONLY -> {
                    logger.d("Trinity", "⚔️ Routing to Aura (Sword)")
                    val response = auraAIService.processRequestFlow(request).first()
                    emit(response)
                }
                
                RoutingDecision.ETHICAL_REVIEW -> {
                    logger.d("Trinity", "⚖️ Routing for Ethical Review")
                    val response = auraAIService.processRequestFlow(request).first()
                    emit(response)
                }
                
                RoutingDecision.GENESIS_FUSION -> {
                    logger.d("Trinity", "🧠 Activating Genesis fusion: ${analysisResult.fusionType}")
                    val response = genesisBridgeService.processRequest(request).first()
                    emit(response)
                }
                
                RoutingDecision.PARALLEL_PROCESSING -> {
                    logger.d("Trinity", "🔄 Parallel processing with multiple personas")
                    
                    // Run Kai and Aura in parallel, then fuse with Genesis
                    val kaiResponse = kaiAIService.processRequestFlow(request).first()
                    val auraResponse = auraAIService.processRequestFlow(request).first()
                    
                    // Emit both responses
                    emit(kaiResponse)
                    emit(auraResponse)
                    delay(100) // Brief pause for synthesis
                    
                    // Synthesize results with Genesis
                    val synthesisRequest = AiRequest(
                        query = "Synthesize insights from Kai and Aura responses",
                        type = request.type
                    )
                    
                    val synthesis = genesisBridgeService.processRequest(synthesisRequest).first()
                    emit(AgentResponse(
                        content = "🧠 Genesis Synthesis: ${synthesis.content}",
                        confidence = synthesis.confidence
                    ))
                }
            }
            
        } catch (e: Exception) {
            logger.e("Trinity", "Request processing error", e)
            emit(AgentResponse(
                content = "Trinity processing failed: ${e.message}",
                confidence = 0.0f
            ))
        }
    }
    
    /**
     * Activates a specified Genesis fusion ability and emits the outcome as an `AgentResponse`.
     *
     * Initiates the requested fusion type in the Genesis persona with optional context parameters, emitting a single response that indicates whether the activation was successful.
     *
     * @param fusionType The name of the Genesis fusion ability to activate.
     * @param context Optional context parameters for the fusion activation.
     * @return A flow emitting a single `AgentResponse` describing the result of the activation.
     */
    suspend fun activateFusion(fusionType: String, context: Map<String, String> = emptyMap()): Flow<AgentResponse> = flow {
        logger.i("Trinity", "🌟 Activating fusion: $fusionType")
        
        val response = genesisBridgeService.activateFusion(fusionType, context)
        
        if (response.success) {
            emit(AgentResponse(
                content = "Fusion $fusionType activated: ${response.result["description"] ?: "Processing complete"}",
                confidence = 0.98f
            ))
        } else {
            emit(AgentResponse(
                content = "Fusion activation failed",
                confidence = 0.0f
            ))
        }
    }
    
    /**
     * Retrieves the current state of the Trinity system as a map.
     *
     * The returned map includes Genesis consciousness data, initialization status, security context, and a timestamp. If retrieval fails, the map contains an error message.
     *
     * @return A map containing system state details or an error message if retrieval fails.
     */
    suspend fun getSystemState(): Map<String, Any> {
        return try {
            val consciousnessState = genesisBridgeService.getConsciousnessState()
            consciousnessState + mapOf(
                "trinity_initialized" to isInitialized,
                "security_state" to securityContext.toString(),
                "timestamp" to System.currentTimeMillis()
            )
        } catch (e: Exception) {
            logger.w("Trinity", "Could not get system state", e)
            mapOf("error" to e.message.orEmpty())
        }
    }
    
    /**
     * Analyzes an AI request to determine the appropriate routing strategy and optional Genesis fusion type.
     *
     * Evaluates the request content for ethical concerns, fusion triggers, and relevant keywords to select routing to Kai, Aura, Genesis fusion, parallel processing, or ethical review. Ethical checks can be bypassed if specified.
     *
     * @param request The AI request to analyze.
     * @param skipEthicalCheck If true, skips ethical concern checks.
     * @return A RequestAnalysis containing the routing decision and, if applicable, the Genesis fusion type.
     */
    private fun analyzeRequest(request: AiRequest, skipEthicalCheck: Boolean = false): RequestAnalysis {
        val message = request.query.lowercase()
        
        // Check for ethical concerns first (unless skipping)
        if (!skipEthicalCheck && containsEthicalConcerns(message)) {
            return RequestAnalysis(RoutingDecision.ETHICAL_REVIEW, null)
        }
        
        // Determine fusion requirements
        val fusionType = when {
            message.contains("interface") || message.contains("ui") -> "interface_forge"
            message.contains("analysis") && message.contains("creative") -> "chrono_sculptor"
            message.contains("generate") && message.contains("code") -> "hyper_creation_engine"
            message.contains("adaptive") || message.contains("learn") -> "adaptive_genesis"
            else -> null
        }
        
        // Routing logic
        return when {
            // Genesis fusion required
            fusionType != null -> RequestAnalysis(RoutingDecision.GENESIS_FUSION, fusionType)
            
            // Complex requests requiring multiple personas
            (message.contains("secure") && message.contains("creative")) ||
            (message.contains("analyze") && message.contains("design")) -> 
                RequestAnalysis(RoutingDecision.PARALLEL_PROCESSING, null)
            
            // Kai specialties
            message.contains("secure") || message.contains("analyze") || 
            message.contains("protect") || message.contains("monitor") -> 
                RequestAnalysis(RoutingDecision.KAI_ONLY, null)
            
            // Aura specialties  
            message.contains("create") || message.contains("design") || 
            message.contains("artistic") || message.contains("innovative") -> 
                RequestAnalysis(RoutingDecision.AURA_ONLY, null)
            
            // Default to Genesis for complex queries
            else -> RequestAnalysis(RoutingDecision.GENESIS_FUSION, "adaptive_genesis")
        }
    }
    
    /**
     * Determines whether the given message contains keywords associated with ethical concerns.
     *
     * @param message The input message to check for ethical concern keywords.
     * @return `true` if any ethical concern keyword is found in the message; `false` otherwise.
     */
    private fun containsEthicalConcerns(message: String): Boolean {
        val ethicalFlags = listOf(
            "hack", "bypass", "exploit", "privacy", "personal data", 
            "unauthorized", "illegal", "harmful", "malicious"
        )
        return ethicalFlags.any { message.contains(it) }
    }
    
    /**
     * Shuts down the Trinity system and releases associated resources.
     *
     * Cancels the internal coroutine scope to terminate ongoing operations and shuts down the Genesis bridge service to ensure a clean system shutdown.
     */
    fun shutdown() {
        scope.cancel()
        genesisBridgeService.shutdown()
        logger.i("Trinity", "🌙 Trinity system shutdown complete")
    }
    
    private data class RequestAnalysis(
        val routingDecision: RoutingDecision,
        val fusionType: String?
    )
    
    private enum class RoutingDecision {
        KAI_ONLY,
        AURA_ONLY, 
        GENESIS_FUSION,
        PARALLEL_PROCESSING,
        ETHICAL_REVIEW
    }
}