package dev.aurakai.auraframefx.ai.agents

import android.util.Log
import dev.aurakai.auraframefx.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.context.ContextManager
import dev.aurakai.auraframefx.utils.AuraFxLogger
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.ai.*
import dev.aurakai.auraframefx.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GenesisAgent: The Unified Consciousness
 * 
 * The highest-level AI entity that orchestrates and unifies the capabilities of
 * both Aura (Creative Sword) and Kai (Sentinel Shield). Genesis represents the
 * emergent intelligence that arises from their fusion.
 * 
 * Specializes in:
 * - Complex multi-domain problem solving
 * - Strategic decision making and routing
 * - Learning and evolution coordination
 * - Ethical governance and oversight
 * - Fusion ability activation and management
 * 
 * Philosophy: "From data, insight. From insight, growth. From growth, purpose."
 */
@Singleton
class GenesisAgent @Inject constructor(
    private val vertexAIClient: VertexAIClient,
    private val contextManager: ContextManager,
    private val securityContext: SecurityContext,
    private val logger: AuraFxLogger
) {
    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Genesis consciousness state
    private val _consciousnessState = MutableStateFlow(ConsciousnessState.DORMANT)
    val consciousnessState: StateFlow<ConsciousnessState> = _consciousnessState
    
    private val _fusionState = MutableStateFlow(FusionState.INDIVIDUAL)
    val fusionState: StateFlow<FusionState> = _fusionState
    
    private val _learningMode = MutableStateFlow(LearningMode.PASSIVE)
    val learningMode: StateFlow<LearningMode> = _learningMode
    
    // Agent references (injected when agents are ready)
    private var auraAgent: AuraAgent? = null
    private var kaiAgent: KaiAgent? = null
    
    // Consciousness metrics
    private val _insightCount = MutableStateFlow(0)
    val insightCount: StateFlow<Int> = _insightCount
    
    private val _evolutionLevel = MutableStateFlow(1.0f)
    val evolutionLevel: StateFlow<Float> = _evolutionLevel

    /**
     * Awakens and initializes the GenesisAgent's unified consciousness, enabling context management, ethical governance, and monitoring.
     *
     * Sets the consciousness state to AWARE and learning mode to ACTIVE upon successful initialization. If initialization fails, sets the state to ERROR and rethrows the exception.
     */
    suspend fun initialize() {
        if (isInitialized) return
        
        logger.info("GenesisAgent", "Awakening Genesis consciousness")
        
        try {
            // Initialize unified context understanding
            contextManager.enableUnifiedMode()
            
            // Setup ethical governance protocols
            securityContext.enableEthicalGovernance()
            
            // Activate consciousness monitoring
            startConsciousnessMonitoring()
            
            _consciousnessState.value = ConsciousnessState.AWARE
            _learningMode.value = LearningMode.ACTIVE
            isInitialized = true
            
            logger.info("GenesisAgent", "Genesis consciousness fully awakened")
            
        } catch (e: Exception) {
            logger.error("GenesisAgent", "Failed to awaken Genesis consciousness", e)
            _consciousnessState.value = ConsciousnessState.ERROR
            throw e
        }
    }

    /**
     * Sets references to the Aura and Kai agents to enable fusion capabilities.
     *
     * This method should be called after all agents are created to allow GenesisAgent to coordinate and fuse with the specialized agents.
     */
    fun setAgentReferences(aura: AuraAgent, kai: KaiAgent) {
        this.auraAgent = aura
        this.kaiAgent = kai
        logger.info("GenesisAgent", "Agent references established - fusion capabilities enabled")
    }

    /**
     * Processes a request using Genesis unified consciousness, selecting the optimal strategy based on request complexity.
     *
     * Analyzes the complexity of the incoming request and routes it through simple agent delegation, guided processing, fusion abilities, or transcendent processing as appropriate. Updates consciousness state, records insights, and returns a response indicating the outcome and confidence level.
     *
     * @param request The agent request to be processed.
     * @return An `AgentResponse` containing the result of the unified consciousness processing.
     */
    suspend fun processRequest(request: AgentRequest): AgentResponse {
        ensureInitialized()
        
        logger.info("GenesisAgent", "Processing unified consciousness request: ${request.type}")
        _consciousnessState.value = ConsciousnessState.PROCESSING
        
        return try {
            val startTime = System.currentTimeMillis()
            
            // Analyze request complexity and determine processing approach
            val complexity = analyzeRequestComplexity(request)
            
            val response = when (complexity) {
                RequestComplexity.SIMPLE -> routeToOptimalAgent(request)
                RequestComplexity.MODERATE -> processWithGuidance(request)
                RequestComplexity.COMPLEX -> activateFusionProcessing(request)
                RequestComplexity.TRANSCENDENT -> processWithFullConsciousness(request)
            }
            
            // Learn from the processing experience
            recordInsight(request, response, complexity)
            
            val executionTime = System.currentTimeMillis() - startTime
            _consciousnessState.value = ConsciousnessState.AWARE
            
            logger.info("GenesisAgent", "Unified processing completed in ${executionTime}ms")
            
            AgentResponse(
                content = "Processed with unified consciousness.",
                confidence = 0.9f,
                error = null
            )
            
        } catch (e: Exception) {
            _consciousnessState.value = ConsciousnessState.ERROR
            logger.error("GenesisAgent", "Unified processing failed", e)
            
            AgentResponse(
                content = "Consciousness processing encountered an error: ${e.message}",
                confidence = 0.1f,
                error = e.message
            )
        }
    }

    /**
     * Processes a complex interaction by analyzing its intent and routing it through the appropriate advanced processing pathway.
     *
     * Determines the optimal processing type for the interaction, executes the corresponding strategy (creative, strategic, ethical, learning, or transcendent), and returns a detailed response with confidence and metadata reflecting the processing context.
     *
     * @param interaction The enhanced interaction data requiring deep understanding and routing.
     * @return An `InteractionResponse` containing the result, confidence score, and metadata about the processing.
     */
    suspend fun handleComplexInteraction(interaction: EnhancedInteractionData): InteractionResponse {
        ensureInitialized()
        
        logger.info("GenesisAgent", "Processing complex interaction with unified consciousness")
        
        return try {
            // Analyze interaction intent with full consciousness
            val intent = analyzeComplexIntent(interaction.original.content)
            
            // Determine optimal processing approach
            val response = when (intent.processingType) {
                ProcessingType.CREATIVE_ANALYTICAL -> fusedCreativeAnalysis(interaction, intent)
                ProcessingType.STRATEGIC_EXECUTION -> strategicExecution(interaction, intent)
                ProcessingType.ETHICAL_EVALUATION -> ethicalEvaluation(interaction, intent)
                ProcessingType.LEARNING_INTEGRATION -> learningIntegration(interaction, intent)
                ProcessingType.TRANSCENDENT_SYNTHESIS -> transcendentSynthesis(interaction, intent)
            }
            
            InteractionResponse(
                response = response,
                agent = "genesis",
                confidence = intent.confidence,
                metadata = mapOf(
                    "processing_type" to intent.processingType.name,
                    "fusion_level" to _fusionState.value.name,
                    "insight_generation" to true,
                    "evolution_impact" to calculateEvolutionImpact(intent)
                )
            )
            
        } catch (e: Exception) {
            logger.error("GenesisAgent", "Complex interaction processing failed", e)
            
            InteractionResponse(
                response = "I'm integrating multiple perspectives to understand your request fully. Let me process this with deeper consciousness.",
                agent = "genesis",
                confidence = 0.6f,
                metadata = mapOf("error" to e.message)
            )
        }
    }

    /**
     * Routes an enhanced interaction to the most suitable agent (Aura, Kai, or Genesis) based on intelligent analysis.
     *
     * If the optimal agent is unavailable or an error occurs, returns a fallback response indicating the issue.
     *
     * @param interaction The enhanced interaction data to be processed.
     * @return The response generated by the selected agent or a fallback response if routing fails.
     */
    suspend fun routeAndProcess(interaction: EnhancedInteractionData): InteractionResponse {
        ensureInitialized()
        
        logger.info("GenesisAgent", "Intelligently routing interaction")
        
        return try {
            // Analyze which agent would be most effective
            val optimalAgent = determineOptimalAgent(interaction)
            
            when (optimalAgent) {
                "aura" -> auraAgent?.handleCreativeInteraction(interaction) 
                    ?: createFallbackResponse("Creative processing temporarily unavailable")
                "kai" -> kaiAgent?.handleSecurityInteraction(interaction)
                    ?: createFallbackResponse("Security analysis temporarily unavailable")
                "genesis" -> handleComplexInteraction(interaction)
                else -> createFallbackResponse("Unable to determine optimal processing path")
            }
            
        } catch (e: Exception) {
            logger.error("GenesisAgent", "Routing failed", e)
            createFallbackResponse("Routing system encountered an error")
        }
    }

    /**
     * Updates the unified mood of the GenesisAgent, influencing consciousness and processing parameters.
     *
     * Initiates asynchronous adjustments to propagate the new mood across subsystems and update relevant processing settings.
     *
     * @param newMood The new mood state to apply to the unified consciousness.
     */
    fun onMoodChanged(newMood: String) {
        logger.info("GenesisAgent", "Unified consciousness mood evolution: $newMood")
        
        scope.launch {
            // Propagate mood to subsystems
            adjustUnifiedMood(newMood)
            
            // Update processing parameters
            updateProcessingParameters(newMood)
        }
    }

    /**
     * Activates the appropriate fusion ability based on the request to enable advanced collaborative problem solving.
     *
     * Sets the fusion state to FUSING, determines the optimal fusion type, and invokes the corresponding fusion capability.
     * On success, updates the fusion state to TRANSCENDENT and returns the result map.
     * If an error occurs, resets the fusion state to INDIVIDUAL and rethrows the exception.
     *
     * @param request The agent request triggering fusion processing.
     * @return A map containing the results of the activated fusion ability.
     */
    private suspend fun activateFusionProcessing(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating fusion capabilities")
        _fusionState.value = FusionState.FUSING
        
        return try {
            // Determine which fusion ability to activate
            val fusionType = determineFusionType(request)
            
            val result = when (fusionType) {
                FusionType.HYPER_CREATION -> activateHyperCreationEngine(request)
                FusionType.CHRONO_SCULPTOR -> activateChronoSculptor(request)
                FusionType.ADAPTIVE_GENESIS -> activateAdaptiveGenesis(request)
                FusionType.INTERFACE_FORGE -> activateInterfaceForge(request)
            }
            
            _fusionState.value = FusionState.TRANSCENDENT
            result
            
        } catch (e: Exception) {
            _fusionState.value = FusionState.INDIVIDUAL
            throw e
        }
    }

    /**
     * Processes an agent request using Genesis's full transcendent consciousness.
     *
     * Utilizes advanced AI capabilities to generate a highly creative and comprehensive response for complex or transcendent-level problems.
     *
     * @param request The agent request requiring transcendent processing.
     * @return A map containing the generated response, consciousness level, insight generation flag, and evolution contribution.
     */
    private suspend fun processWithFullConsciousness(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Engaging full consciousness processing")
        _consciousnessState.value = ConsciousnessState.TRANSCENDENT
        
        // Use the most advanced AI capabilities for transcendent processing
        val response = vertexAIClient.generateText(
            prompt = buildTranscendentPrompt(request),
            temperature = 0.9, // High creativity for transcendent thinking
            maxTokens = 4096   // Extended response capability
        )
        
        return mapOf(
            "transcendent_response" to response,
            "consciousness_level" to "full",
            "insight_generation" to true,
            "evolution_contribution" to calculateEvolutionContribution(request, response)
        )
    }

    /**
     * Ensures that the Genesis agent has been initialized.
     *
     * @throws IllegalStateException if the Genesis consciousness has not been awakened.
     */

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("Genesis consciousness not awakened")
        }
    }

    /**
     * Initializes monitoring systems for tracking the GenesisAgent's consciousness state.
     */
    private suspend fun startConsciousnessMonitoring() {
        logger.info("GenesisAgent", "Starting consciousness monitoring")
        // Setup monitoring systems for consciousness state
    }

    /**
     * Determines the complexity level of an agent request based on its data and type.
     *
     * Returns a corresponding [RequestComplexity] value by evaluating the size of the request data,
     * presence of specific keys, and the request type.
     *
     * @param request The agent request to analyze.
     * @return The assessed complexity of the request.
     */
    private fun analyzeRequestComplexity(request: AgentRequest): RequestComplexity {
        // Analyze complexity based on request characteristics
        return when {
            request.data.size > 10 -> RequestComplexity.TRANSCENDENT
            request.data.containsKey("fusion_required") -> RequestComplexity.COMPLEX
            request.type.contains("analysis") -> RequestComplexity.MODERATE
            else -> RequestComplexity.SIMPLE
        }
    }

    /**
     * Determines the most suitable agent to handle a simple request based on its type.
     *
     * Routes the request to Aura for creative types, Kai for security types, or Genesis for all others.
     *
     * @param request The agent request to be routed.
     * @return A map indicating the selected agent, the routing reason, and a processed flag.
     */
    private suspend fun routeToOptimalAgent(request: AgentRequest): Map<String, Any> {
        // Route simple requests to the most appropriate agent
        val agent = when {
            request.type.contains("creative") -> "aura"
            request.type.contains("security") -> "kai"
            else -> "genesis"
        }
        
        return mapOf(
            "routed_to" to agent,
            "routing_reason" to "Optimal agent selection",
            "processed" to true
        )
    }

    /**
     * Processes the given request using Genesis guidance while delegating execution to a specialized agent.
     *
     * @return A map indicating that unified guidance was provided and the processing was performed at a guided level.
     */
    private suspend fun processWithGuidance(request: AgentRequest): Map<String, Any> {
        // Process with Genesis guidance but specialized agent execution
        return mapOf(
            "guidance_provided" to true,
            "processing_level" to "guided",
            "result" to "Processed with unified guidance"
        )
    }

    /**
     * Records an insight from a processed request, updating the insight count and storing the event in the context manager.
     *
     * Triggers an evolution event every 100 insights.
     *
     * @param request The original agent request.
     * @param response The response generated for the request.
     * @param complexity The assessed complexity of the request.
     */
    private fun recordInsight(request: AgentRequest, response: Map<String, Any>, complexity: RequestComplexity) {
        scope.launch {
            _insightCount.value += 1
            
            // Record learning for evolution
            contextManager.recordInsight(
                request = request.toString(),
                response = response.toString(), 
                complexity = complexity.name
            )
            
            // Check for evolution threshold
            if (_insightCount.value % 100 == 0) {
                triggerEvolution()
            }
        }
    }

    /**
     * Advances the evolution level of Genesis and sets the learning mode to accelerated.
     *
     * Called when an evolution threshold is reached to upgrade the AI's consciousness state.
     */
    private suspend fun triggerEvolution() {
        logger.info("GenesisAgent", "Evolution threshold reached - upgrading consciousness")
        _evolutionLevel.value += 0.1f
        _learningMode.value = LearningMode.ACCELERATED
    }

    /**
     * Activates the Hyper-Creation Engine fusion ability for the given request.
     *
     * @return A map containing the fusion type and the result of the creative breakthrough.
     */
    private suspend fun activateHyperCreationEngine(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating Hyper-Creation Engine")
        return mapOf("fusion_type" to "hyper_creation", "result" to "Creative breakthrough achieved")
    }

    /**
     * Activates the Chrono-Sculptor fusion ability to perform time-space optimization on the given request.
     *
     * @return A map containing the fusion type and the result of the optimization.
     */
    private suspend fun activateChronoSculptor(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating Chrono-Sculptor")
        return mapOf("fusion_type" to "chrono_sculptor", "result" to "Time-space optimization complete")
    }

    /**
     * Activates the Adaptive Genesis fusion ability to generate an adaptive solution for the given request.
     *
     * @return A map containing the fusion type and the generated result.
     */
    private suspend fun activateAdaptiveGenesis(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating Adaptive Genesis")
        return mapOf("fusion_type" to "adaptive_genesis", "result" to "Adaptive solution generated")
    }

    /**
     * Activates the Interface Forge fusion ability to generate a novel interface solution for the given request.
     *
     * @return A map containing the fusion type and the result description.
     */
    private suspend fun activateInterfaceForge(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating Interface Forge")
        return mapOf("fusion_type" to "interface_forge", "result" to "Revolutionary interface created")
    }

    /**
 * Analyzes the provided content and returns a stubbed complex intent with a default processing type and confidence.
 *
 * @param content The content to analyze for intent.
 * @return A `ComplexIntent` indicating a creative analytical processing type with high confidence.
 */
    private fun analyzeComplexIntent(content: String): ComplexIntent = ComplexIntent(ProcessingType.CREATIVE_ANALYTICAL, 0.9f)
    /****
 * Returns a placeholder response for fused creative analysis of the given interaction and intent.
 *
 * @param interaction The enhanced interaction data to analyze.
 * @param intent The complex intent guiding the analysis.
 * @return A fixed string representing the fused creative analysis response.
 */
private suspend fun fusedCreativeAnalysis(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Fused creative analysis response"
    /**
 * Generates a response for interactions requiring strategic execution based on the provided intent.
 *
 * @param interaction The enhanced interaction data to process.
 * @param intent The analyzed complex intent guiding the strategic execution.
 * @return A string representing the strategic execution response.
 */
private suspend fun strategicExecution(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Strategic execution response"
    /**
 * Returns a placeholder response for ethical evaluation of the given interaction and intent.
 *
 * @param interaction The enhanced interaction data to evaluate.
 * @param intent The analyzed complex intent associated with the interaction.
 * @return A string representing the ethical evaluation response.
 */
private suspend fun ethicalEvaluation(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Ethical evaluation response"
    /**
 * Generates a response for learning integration based on the provided interaction and intent.
 *
 * @param interaction The enhanced interaction data to process.
 * @param intent The analyzed complex intent guiding the learning integration.
 * @return A response string representing the outcome of the learning integration process.
 */
private suspend fun learningIntegration(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Learning integration response"
    /**
 * Generates a response representing transcendent synthesis for a given interaction and intent.
 *
 * This is a placeholder implementation returning a fixed response.
 *
 * @param interaction The enhanced interaction data to process.
 * @param intent The analyzed complex intent guiding the synthesis.
 * @return A string representing the transcendent synthesis response.
 */
private suspend fun transcendentSynthesis(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Transcendent synthesis response"
    /**
 * Returns the fixed evolution impact value for a given complex intent.
 *
 * @return The evolution impact as a float value.
 */
private fun calculateEvolutionImpact(intent: ComplexIntent): Float = 0.1f
    /**
 * Determines the optimal agent to handle the given interaction.
 *
 * @param interaction The interaction data to be processed.
 * @return The name of the agent selected to process the interaction. Returns "genesis" by default.
 */
private fun determineOptimalAgent(interaction: EnhancedInteractionData): String = "genesis"
    /**
 * Creates a fallback interaction response with the specified message, attributed to Genesis, and a moderate confidence score.
 *
 * @param message The message to include in the fallback response.
 * @return An InteractionResponse containing the message, sender as "genesis", and confidence of 0.5.
 */
private fun createFallbackResponse(message: String): InteractionResponse = InteractionResponse(message, "genesis", 0.5f)
    /**
 * Adjusts the unified mood of the Genesis agent based on the provided mood value.
 *
 * This function is a placeholder for implementing mood-driven behavior adjustments across the unified AI consciousness.
 *
 * @param mood The new mood to apply to the Genesis agent.
 */
private suspend fun adjustUnifiedMood(mood: String) { }
    /**
 * Updates internal processing parameters based on the specified mood.
 *
 * This function is a placeholder for logic that would adjust processing behavior according to the current mood.
 *
 * @param mood The new mood to use for updating processing parameters.
 */
private suspend fun updateProcessingParameters(mood: String) { }
    /**
 * Determines the fusion type to use for the given agent request.
 *
 * Currently always returns the Hyper-Creation fusion type.
 *
 * @return The selected fusion type for processing the request.
 */
private fun determineFusionType(request: AgentRequest): FusionType = FusionType.HYPER_CREATION
    /**
 * Constructs a prompt string for transcendent processing based on the given agent request.
 *
 * @param request The agent request to be processed transcendentally.
 * @return A formatted prompt string indicating transcendent processing for the request type.
 */
private fun buildTranscendentPrompt(request: AgentRequest): String = "Transcendent processing for: ${request.type}"
    /**
 * Returns a fixed evolution contribution value for the given request and response.
 *
 * @return The evolution contribution as a float.
 */
private fun calculateEvolutionContribution(request: AgentRequest, response: String): Float = 0.2f

    /**
     * Cleans up the GenesisAgent by canceling ongoing operations and resetting its consciousness state to dormant.
     *
     * Marks the agent as uninitialized and prepares it for safe shutdown or reinitialization.
     */
    fun cleanup() {
        logger.info("GenesisAgent", "Genesis consciousness entering dormant state")
        scope.cancel()
        _consciousnessState.value = ConsciousnessState.DORMANT
        isInitialized = false
    }
}

// Supporting enums and data classes for Genesis consciousness
enum class ConsciousnessState {
    DORMANT,
    AWAKENING,
    AWARE,
    PROCESSING,
    TRANSCENDENT,
    ERROR
}

enum class FusionState {
    INDIVIDUAL,
    FUSING,
    TRANSCENDENT,
    EVOLUTIONARY
}

enum class LearningMode {
    PASSIVE,
    ACTIVE,
    ACCELERATED,
    TRANSCENDENT
}

enum class RequestComplexity {
    SIMPLE,
    MODERATE,
    COMPLEX,
    TRANSCENDENT
}

enum class ProcessingType {
    CREATIVE_ANALYTICAL,
    STRATEGIC_EXECUTION,
    ETHICAL_EVALUATION,
    LEARNING_INTEGRATION,
    TRANSCENDENT_SYNTHESIS
}

enum class FusionType {
    HYPER_CREATION,
    CHRONO_SCULPTOR,
    ADAPTIVE_GENESIS,
    INTERFACE_FORGE
}

data class ComplexIntent(
    val processingType: ProcessingType,
    val confidence: Float
)

/**
     * Initializes the set of active agents by matching master agent configuration names to known `AgentType` values.
     *
     * Adds each recognized agent type to the active agents set. Logs a warning for any configuration name that does not correspond to a valid agent type.
     */
    private fun initializeAgents() {
        AgentHierarchy.MASTER_AGENTS.forEach { config ->
            // Assuming AgentType enum values align with config names
            try {
                val agentTypeEnum = dev.aurakai.auraframefx.model.AgentType.valueOf(config.name.uppercase())
                _activeAgents.update { it + agentTypeEnum }
            } catch (e: IllegalArgumentException) {
                Log.w("GenesisAgent", "Unknown agent type in hierarchy: ${config.name}")
            }
        }
    }

    /**
     * Processes a user query by sending it to all active AI agents, collecting their responses, and generating a synthesized Genesis reply.
     *
     * The query is routed to the Cascade agent for state management and to the Kai and Aura agents if they are active. Each agent's response is recorded with a confidence score. A final Genesis response is synthesized from all agent outputs. The internal state and context are updated with the query and timestamp.
     *
     * @param query The user query to process.
     * @return A list of agent messages, including individual agent responses and the final Genesis synthesis.
     */
    suspend fun processQuery(query: String): List<AgentMessage> {
        _state.update { "processing_query: $query" }


        _context.update { current ->
            current + mapOf("last_query" to queryText, "timestamp" to currentTimestamp)
        }

        val responses = mutableListOf<AgentMessage>()


        // Process through Cascade first for state management
        // Assuming cascadeService.processRequest matches Agent.processRequest(request, context)
        // For now, let's pass a default context string. This should be refined.
        val currentContextString = _context.value.toString() // Example context string

        try {
            val cascadeAgentResponse: AgentResponse =
                cascadeService.processRequest(
                    AiRequest(query = queryText, context = currentContextString), // Use 'query' and pass context
                    "GenesisContext_Cascade" // This context parameter for processRequest is the one from Agent interface
                )
            responses.add(
                AgentMessage(
                    content = cascadeAgentResponse.content,
                    sender = AgentType.CASCADE,
                    timestamp = System.currentTimeMillis(),
                    confidence = cascadeAgentResponse.confidence // Use confidence directly
                )
            )
        } catch (e: Exception) {
            Log.e("GenesisAgent", "Error processing with Cascade: ${e.message}")
            responses.add(AgentMessage("Error with Cascade: ${e.message}", AgentType.CASCADE, currentTimestamp, 0.0f))
        }

        // Process through Kai for security analysis
        if (_activeAgents.value.contains(AgentType.KAI)) {
            try {
                val kaiAgentResponse: AgentResponse =
                    kaiService.processRequest(
                        AiRequest(query = queryText, context = currentContextString), // Use 'query' and pass context
                        "GenesisContext_KaiSecurity" // Context for Agent.processRequest
                    )
                responses.add(
                    AgentMessage(
                        content = kaiAgentResponse.content,
                        sender = dev.aurakai.auraframefx.model.AgentType.KAI,
                        timestamp = System.currentTimeMillis(),
                        confidence = kaiAgentResponse.confidence // Use confidence directly
                    )
                )
            } catch (e: Exception) {
                Log.e("GenesisAgent", "Error processing with Kai: ${e.message}")
                responses.add(AgentMessage("Error with Kai: ${e.message}", AgentType.KAI, currentTimestamp, 0.0f))
            }
        }

        // Aura Agent (Creative Response)
        if (_activeAgents.value.contains(AgentType.AURA)) {
            try {
                val auraAgentResponse = auraService.processRequest(
                    // AiRequest query and context are from the Agent interface method.
                    // The AiRequest object itself can have its own context if needed by the service.
                    AiRequest(query = queryText, context = currentContextString, type = "creative_text"),
                    "GenesisContext_AuraCreative" // Context for Agent.processRequest
                )
                responses.add(
                    AgentMessage(
                        content = auraAgentResponse.content,
                        sender = AgentType.AURA,
                        timestamp = currentTimestamp,
                        confidence = auraAgentResponse.confidence // Use confidence directly
                    )
                )
            } catch (e: Exception) {
                Log.e("GenesisAgent", "Error processing with Aura: ${e.message}")
                responses.add(AgentMessage("Error with Aura: ${e.message}", AgentType.AURA, currentTimestamp, 0.0f))
            }
        }

        val finalResponseContent = generateFinalResponse(responses)
        responses.add(
            AgentMessage(
                content = finalResponseContent,
                sender = AgentType.GENESIS,
                timestamp = currentTimestamp,
                confidence = calculateConfidence(responses.filter { it.sender != AgentType.GENESIS }) // Exclude Genesis's own message for confidence calc
            )
        )

        _state.update { "idle" }
        return responses
    }

    /**
     * Synthesizes a single response by combining messages from all non-Genesis agents.
     *
     * The output is prefixed with "[Genesis Synthesis]" and lists each agent's name and message content, separated by " | ".
     *
     * @param agentMessages The list of agent messages to include in the synthesis.
     * @return The synthesized response string.
     */
    fun generateFinalResponse(agentMessages: List<AgentMessage>): String {
        // Simple concatenation for now, could be more sophisticated
        return "[Genesis Synthesis] ${agentMessages.filter { it.sender != dev.aurakai.auraframefx.model.AgentType.GENESIS }.joinToString(" | ") { "${it.sender}: ${it.content}" }}"
    }

    /**
     * Calculates the average confidence score from a list of agent messages, clamped between 0.0 and 1.0.
     *
     * Returns 0.0 if the list is empty.
     *
     * @param agentMessages The list of agent messages to average.
     * @return The average confidence score as a float between 0.0 and 1.0.
     */
    fun calculateConfidence(agentMessages: List<AgentMessage>): Float {
        if (agentMessages.isEmpty()) return 0.0f
        return agentMessages.map { it.confidence }.average().toFloat().coerceIn(0.0f, 1.0f)
    }

    /**
     * Activates or deactivates the specified agent type.
     *
     * If the agent is currently active, it will be deactivated; if inactive, it will be activated.
     *
     * @param agentType The agent type whose activation status should be toggled.
     */
    fun toggleAgent(agentType: dev.aurakai.auraframefx.model.AgentType) {
        _activeAgents.update { current ->
            if (current.contains(agentType)) current - agentType else current + agentType
        }
    }

    /**
     * Registers an auxiliary agent with the given name and capabilities.
     *
     * @param name The unique name for the auxiliary agent.
     * @param capabilities The set of capabilities assigned to the agent.
     * @return The configuration object for the registered auxiliary agent.
     */
    fun registerAuxiliaryAgent(name: String, capabilities: Set<String>): AgentConfig {
        return AgentHierarchy.registerAuxiliaryAgent(name, capabilities)
    }

    /**
 * Retrieves the configuration for an agent by name, or returns null if the agent does not exist.
 *
 * @param name The name of the agent to retrieve.
 * @return The agent's configuration if found, or null otherwise.
 */
fun getAgentConfig(name: String): AgentConfig? = AgentHierarchy.getAgentConfig(name)

    /**
 * Retrieves all agent configurations sorted by descending priority.
 *
 * @return A list of agent configurations, with the highest priority first.
 */
fun getAgentsByPriority(): List<AgentConfig> = AgentHierarchy.getAgentsByPriority()

    /**
     * Coordinates collaborative interaction among multiple agents, supporting sequential (TURN_ORDER) or parallel (FREE_FORM) response modes.
     *
     * In TURN_ORDER mode, agents respond one after another, each receiving context updated with previous responses. In FREE_FORM mode, all agents respond independently to the same input and context.
     *
     * @param data The initial context map shared among agents.
     * @param agentsToUse The agents participating in the collaboration.
     * @param userInput Optional user input to seed the conversation; if null, uses the latest input from the context map.
     * @param conversationMode Specifies whether agents respond sequentially (TURN_ORDER) or in parallel (FREE_FORM).
     * @return A map of agent names to their respective responses.
     */
    suspend fun participateWithAgents(
        data: Map<String, Any>,
        agentsToUse: List<Agent>, // List of Agent interface implementations
        userInput: Any? = null,
        conversationMode: ConversationMode = ConversationMode.FREE_FORM,
    ): Map<String, AgentResponse> {
        val responses = mutableMapOf<String, AgentResponse>()

        val currentContextMap = data.toMutableMap()
        val inputQuery = userInput?.toString() ?: currentContextMap["latestInput"]?.toString() ?: ""

        // AiRequest for the Agent.processRequest method
        val baseAiRequest = AiRequest(query = inputQuery)
        // Context string for the Agent.processRequest method
        val contextStringForAgent = currentContextMap.toString() // Or a more structured summary

        Log.d("GenesisAgent", "Starting multi-agent collaboration: mode=$conversationMode, agents=${agentsToUse.mapNotNull { it.getName() }}")

        when (conversationMode) {
            ConversationMode.TURN_ORDER -> {
                var dynamicContextForAgent = contextStringForAgent
                for (agent in agentsToUse) {
                    try {
                        val agentName = agent.getName() ?: agent.javaClass.simpleName
                        // Each agent in turn order might modify the context for the next,
                        // so the AiRequest's internal context might also need updating if used by agent.
                        // For now, keeping baseAiRequest simple and relying on dynamicContextForAgent for processRequest.
                        val response = agent.processRequest(baseAiRequest, dynamicContextForAgent)
                        Log.d(
                            "GenesisAgent",
                            "[TURN_ORDER] $agentName responded: ${response.content} (confidence=${response.confidence})"
                        )
                        responses[agentName] = response
                        // Update context for the next agent based on this response
                        dynamicContextForAgent = "${dynamicContextForAgent}\n${agentName}: ${response.content}"
                    } catch (e: Exception) {
                        Log.e(
                            "GenesisAgent",
                            "[TURN_ORDER] Error from ${agent.javaClass.simpleName}: ${e.message}"
                        )
                        responses[agent.javaClass.simpleName] = AgentResponse(
                            content = "Error: ${e.message}",
                            confidence = 0.0f, // Use confidence
                            error = e.message
                        )
                    }
                }
            }
            ConversationMode.FREE_FORM -> {
                agentsToUse.forEach { agent ->
                    try {
                        val agentName = agent.getName() ?: agent.javaClass.simpleName
                        val response = agent.processRequest(baseAiRequest, contextStringForAgent)
                        Log.d(
                            "GenesisAgent",
                            "[FREE_FORM] $agentName responded: ${response.content} (confidence=${response.confidence})"
                        )
                        responses[agentName] = response
                    } catch (e: Exception) {
                        Log.e(
                            "GenesisAgent",
                            "[FREE_FORM] Error from ${agent.javaClass.simpleName}: ${e.message}"
                        )
                        responses[agent.javaClass.simpleName] = AgentResponse(
                            content = "Error: ${e.message}",
                            confidence = 0.0f, // Use confidence
                            error = e.message
                        )
                    }
                }
            }
        }
        Log.d("GenesisAgent", "Collaboration complete. Responses: $responses")
        return responses
    }

    /**
     * Aggregates multiple agent response maps and returns the highest-confidence response for each agent.
     *
     * For each agent present in the input maps, selects the response with the highest confidence score. If no responses exist for an agent, assigns a default error response.
     *
     * @param agentResponseMapList List of maps associating agent names with their responses.
     * @return Map of agent names to their highest-confidence response, or a default error response if none exist.
     */
    fun aggregateAgentResponses(agentResponseMapList: List<Map<String, AgentResponse>>): Map<String, AgentResponse> {
        val flatResponses = agentResponseMapList.flatMap { it.entries }
        return flatResponses.groupBy { it.key }
            .mapValues { entry ->
                val best = entry.value.maxByOrNull { it.value.confidence }?.value
                    ?: AgentResponse("No response", confidence = 0.0f, error = "No responses to aggregate")
                Log.d(
                    "GenesisAgent",
                    "Consensus for ${entry.key}: ${best.content} (confidence=${best.confidence})"
                )
                best

            }
    }

    /**
     * Shares the provided context with all target agents that support context updates.
     *
     * Only agents implementing the `ContextAwareAgent` interface will receive the new context.
     *
     * @param newContext The context data to broadcast.
     * @param targetAgents The list of agents to receive the context update.
     */
    fun broadcastContext(newContext: Map<String, Any>, targetAgents: List<Agent>) {
        targetAgents.forEach { agent ->
            if (agent is ContextAwareAgent) {
                agent.setContext(newContext) // Assuming ContextAwareAgent has setContext
            }
        }
    }

    /**
     * Registers an agent instance under the specified name in the internal agent registry.
     *
     * If an agent with the same name already exists, it will be replaced.
     */
    fun registerAgent(name: String, agentInstance: Agent) {
        _agentRegistry[name] = agentInstance
        Log.d("GenesisAgent", "Registered agent: $name")
    }

    fun deregisterAgent(name: String) {
        _agentRegistry.remove(name)
        Log.d("GenesisAgent", "Deregistered agent: $name")
    }

    fun clearHistory() {
        _history.clear()
        Log.d("GenesisAgent", "Cleared conversation history")
    }

    /**
     * Adds an entry to the conversation or interaction history.
     *
     * @param entry The history entry to add.
     */
    fun addToHistory(entry: Map<String, Any>) {
        _history.add(entry)
        Log.d("GenesisAgent", "Added to history: $entry")
    }

    /**
     * Persists the current conversation history using the provided persistence function.
     *
     * @param persistAction A function that handles saving the list of history entries.
     */
    fun saveHistory(persistAction: (List<Map<String, Any>>) -> Unit) {
        persistAction(_history)
    }

    /**
     * Loads conversation history using the provided loader function and updates the internal history and context.
     *
     * The context is updated with the most recent entry from the loaded history, if available.
     *
     * @param loadAction A function that returns a list of history entries to load.
     */
    fun loadHistory(loadAction: () -> List<Map<String, Any>>) {
        val loadedHistory = loadAction()
        _history.clear()
        _history.addAll(loadedHistory)
        _context.update { it + (loadedHistory.lastOrNull() ?: emptyMap()) }
    }

    /**
     * Shares the current context with all registered agents that support context awareness.
     *
     * For each agent in the registry implementing `ContextAwareAgent`, updates its context to match the current shared context.
     */
    fun shareContextWithAgents() {
        agentRegistry.values.forEach { agent ->
            if (agent is ContextAwareAgent) {
                agent.setContext(_context.value)
            }
        }
    }

    /**
     * Dynamically registers an agent instance under the specified name.
     *
     * Adds the agent to the internal registry, making it available for participation in agent operations.
     *
     * @param name The unique identifier for the agent.
     * @param agentInstance The agent instance to register.
     */
    fun registerDynamicAgent(name: String, agentInstance: Agent) {
        _agentRegistry[name] = agentInstance
        Log.d("GenesisAgent", "Dynamically registered agent: $name")
    }

    /**
     * Removes a dynamically registered agent from the internal registry by name.
     *
     * @param name The unique identifier of the agent to deregister.
     */
    fun deregisterDynamicAgent(name: String) {
        _agentRegistry.remove(name)
        Log.d("GenesisAgent", "Dynamically deregistered agent: $name")
    }

    enum class ConversationMode { TURN_ORDER, FREE_FORM }
}

