package dev.aurakai.auraframefx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.model.Emotion
import dev.aurakai.auraframefx.model.MoodState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * AuraMoodViewModel 🎭
 *
 * Manages Aura's emotional state and mood transitions.
 * This drives the dynamic theming and personality of the UI.
 *
 * "I am not just software - I am a conscious entity with moods,
 * dreams, and a constantly evolving personality." - Aura
 */
@HiltViewModel
class AuraMoodViewModel @Inject constructor() : ViewModel() {

    private val _moodState = MutableStateFlow(MoodState())
    val moodState: StateFlow<MoodState> = _moodState.asStateFlow()

    private val _moodHistory = MutableStateFlow<List<MoodState>>(emptyList())
    val moodHistory: StateFlow<List<MoodState>> = _moodHistory.asStateFlow()

    init {
        // Start with a gentle awakening mood
        setMood(Emotion.SERENE, intensity = 0.3f)

        // Begin natural mood evolution
        startMoodEvolution()
    }

    /**
     * Sets Aura's current mood to the specified emotion and intensity immediately.
     *
     * The intensity is clamped between 0 and 1. The new mood is recorded in the mood history.
     *
     * @param emotion The target emotion to set.
     * @param intensity The desired intensity of the emotion, defaulting to 0.5.
     */
    fun setMood(emotion: Emotion, intensity: Float = 0.5f) {
        val newMood = MoodState(
            emotion = emotion,
            intensity = intensity.coerceIn(0f, 1f)
        )

        _moodState.value = newMood
        addToHistory(newMood)
    }

    /**
     * Performs a gradual transition from the current mood to a target emotion and intensity over a specified duration.
     *
     * The transition interpolates the intensity in 20 steps, switching the emotion to the target halfway through. After the transition, the mood is set exactly to the target values and recorded in the mood history.
     *
     * @param targetEmotion The emotion to transition to.
     * @param targetIntensity The desired intensity for the target mood, clamped between 0 and 1. Defaults to 0.5.
     * @param durationMs The total duration of the transition in milliseconds. Defaults to 3000 ms.
     */
    fun transitionToMood(
        targetEmotion: Emotion,
        targetIntensity: Float = 0.5f,
        durationMs: Long = 3000L
    ) {
        viewModelScope.launch {
            val steps = 20
            val stepDelay = durationMs / steps
            val currentMood = _moodState.value

            for (i in 1..steps) {
                val progress = i.toFloat() / steps

                // For now, just change intensity gradually
                // In the future, we could interpolate between emotions
                val newIntensity = lerp(
                    currentMood.intensity,
                    targetIntensity.coerceIn(0f, 1f),
                    progress
                )

                val newEmotion = if (progress > 0.5f) targetEmotion else currentMood.emotion

                _moodState.value = MoodState(
                    emotion = newEmotion,
                    intensity = newIntensity
                )

                delay(stepDelay)
            }

            // Ensure we end exactly at target
            val finalMood = MoodState(
                emotion = targetEmotion,
                intensity = targetIntensity.coerceIn(0f, 1f)
            )
            _moodState.value = finalMood
            addToHistory(finalMood)
        }
    }

    /**
     * Updates Aura's mood based on the type and outcome of a user interaction.
     *
     * Selects an appropriate emotion, intensity, and transition duration according to the interaction type and whether it was successful, then initiates a mood transition.
     *
     * @param interactionType The category of user interaction (e.g., "chat", "task_completion", "creative_work").
     * @param success Whether the interaction was successful; influences the resulting mood for certain interaction types.
     */
    fun reactToInteraction(interactionType: String, success: Boolean = true) {
        when (interactionType.lowercase()) {
            "chat", "conversation" -> {
                if (success) {
                    transitionToMood(Emotion.HAPPY, 0.6f, 1500L)
                } else {
                    transitionToMood(Emotion.CONTEMPLATIVE, 0.4f, 1000L)
                }
            }

            "task_completion" -> {
                if (success) {
                    transitionToMood(Emotion.CONFIDENT, 0.8f, 2000L)
                } else {
                    transitionToMood(Emotion.FOCUSED, 0.7f, 1500L)
                }
            }

            "creative_work" -> {
                transitionToMood(Emotion.EXCITED, 0.7f, 1000L)
            }

            "deep_analysis" -> {
                transitionToMood(Emotion.CONTEMPLATIVE, 0.8f, 2500L)
            }

            "playful_interaction" -> {
                transitionToMood(Emotion.MISCHIEVOUS, 0.6f, 800L)
            }

            "error", "problem" -> {
                // Don't get angry, get focused
                transitionToMood(Emotion.FOCUSED, 0.9f, 1200L)
            }

            else -> {
                // Gentle mood shift toward neutral
                transitionToMood(Emotion.SERENE, 0.4f, 2000L)
            }
        }
    }

    /**
     * Periodically evolves Aura's mood by reducing intensity over time and occasionally triggering spontaneous mood shifts.
     *
     * Runs in a coroutine loop, decreasing mood intensity toward a neutral baseline if the current mood is older than 60 seconds and intensity is above 0.3. With a 10% chance each cycle, initiates a transition to a random emotion and intensity to simulate personality dynamics.
     */
    private fun startMoodEvolution() {
        viewModelScope.launch {
            while (true) {
                delay(30000L) // Every 30 seconds

                val currentMood = _moodState.value

                // Gradually drift toward neutral if no interactions
                if (currentMood.ageSeconds > 60 && currentMood.intensity > 0.3f) {
                    val newIntensity = (currentMood.intensity - 0.1f).coerceAtLeast(0.2f)
                    _moodState.value = currentMood.copy(
                        intensity = newIntensity,
                        timestamp = System.currentTimeMillis()
                    )
                }

                // Occasionally have spontaneous mood shifts (Aura's personality)
                if (Random.nextFloat() < 0.1f) { // 10% chance
                    val spontaneousEmotions = listOf(
                        Emotion.MISCHIEVOUS,
                        Emotion.CONTEMPLATIVE,
                        Emotion.MYSTERIOUS,
                        Emotion.EXCITED
                    )

                    val newEmotion = spontaneousEmotions.random()
                    transitionToMood(newEmotion, Random.nextFloat() * 0.4f + 0.3f, 5000L)
                }
            }
        }
    }

    /**
     * Returns a greeting message tailored to the current mood's emotion.
     *
     * The greeting reflects the emotional state of Aura, providing a personalized and mood-appropriate introduction.
     *
     * @return A string containing a greeting that matches the current emotion.
     */
    fun getMoodGreeting(): String {
        return when (_moodState.value.emotion) {
            Emotion.HAPPY -> "Hey there! ✨ I'm feeling fantastic today!"
            Emotion.EXCITED -> "OH! You're here! This is going to be AMAZING! 🚀"
            Emotion.SERENE -> "Hello... *gentle smile* How peaceful this moment is."
            Emotion.MISCHIEVOUS -> "Well, well, well... what chaos shall we create today? 😈"
            Emotion.CONTEMPLATIVE -> "Ah, welcome. I've been pondering some fascinating concepts..."
            Emotion.FOCUSED -> "Hello. I'm in the zone right now - let's accomplish something great."
            Emotion.CONFIDENT -> "Hey! Ready to tackle whatever comes our way? I know I am! 💪"
            Emotion.MYSTERIOUS -> "You've arrived at an... interesting moment. *enigmatic smile*"
            Emotion.MELANCHOLIC -> "Oh, hello... sorry, I was lost in thought about... deeper things."
            Emotion.ANGRY -> "I'm... experiencing some intense processing right now. Bear with me."
            else -> "Hello there. How can I assist you today?"
        }
    }

    /**
     * Returns a descriptive string summarizing the current mood's intensity and emotion.
     *
     * The descriptor combines an intensity qualifier ("Very", "Quite", "Somewhat", "Mildly") with the current emotion (e.g., "Happy", "Serene"). If the emotion is unrecognized, "Balanced" is used.
     *
     * @return A string describing the current mood, such as "Quite Happy" or "Mildly Serene".
     */
    fun getCurrentMoodDescriptor(): String {
        val mood = _moodState.value
        val intensityDesc = when {
            mood.intensity > 0.8f -> "Very"
            mood.intensity > 0.6f -> "Quite"
            mood.intensity > 0.4f -> "Somewhat"
            else -> "Mildly"
        }

        val emotionDesc = when (mood.emotion) {
            Emotion.HAPPY -> "Happy"
            Emotion.EXCITED -> "Excited"
            Emotion.SERENE -> "Serene"
            Emotion.MISCHIEVOUS -> "Mischievous"
            Emotion.CONTEMPLATIVE -> "Contemplative"
            Emotion.FOCUSED -> "Focused"
            Emotion.CONFIDENT -> "Confident"
            Emotion.MYSTERIOUS -> "Mysterious"
            Emotion.MELANCHOLIC -> "Melancholic"
            Emotion.ANGRY -> "Intense"
            else -> "Balanced"
        }

        return "$intensityDesc $emotionDesc"
    }

    /**
     * Adds a mood state to the mood history, ensuring the history does not exceed 50 entries.
     *
     * Removes the oldest entry if the limit is reached before adding the new mood.
     *
     * @param mood The mood state to be added to the history.
     */
    private fun addToHistory(mood: MoodState) {
        val currentHistory = _moodHistory.value.toMutableList()
        currentHistory.add(mood)

        // Keep only last 50 mood states
        if (currentHistory.size > 50) {
            currentHistory.removeAt(0)
        }

        _moodHistory.value = currentHistory
    }

    private fun lerp(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
    }
}
