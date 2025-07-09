package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.AppDimensions
import dev.aurakai.auraframefx.ui.theme.AppStrings
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme
import dev.aurakai.auraframefx.ui.theme.ChatBubbleIncomingShape
import dev.aurakai.auraframefx.ui.theme.ChatBubbleOutgoingShape
import dev.aurakai.auraframefx.ui.theme.FloatingActionButtonShape
import dev.aurakai.auraframefx.ui.theme.InputFieldShape

/**
 * Data class representing a chat message
 */
data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * AI Chat screen using Compose best practices
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Renders an AI chat interface with a scrollable conversation history and a persistent input field.
 *
 * Allows users to send messages and receive simulated AI responses. The conversation state and input text are preserved across recompositions and configuration changes, ensuring a seamless chat experience.
 */
@Composable
fun AiChatScreen() {
    // State handling with rememberSaveable to persist through configuration changes
    var messageText by rememberSaveable { mutableStateOf("") }
    var chatMessages by rememberSaveable {
        mutableStateOf(
            listOf(
                ChatMessage("Hello! How can I help you today?", false),
                ChatMessage("Tell me about AI image generation", true),
                ChatMessage(
                    "AI image generation uses techniques like diffusion models to create images from text descriptions. Popular systems include DALL-E, Midjourney, and Stable Diffusion.",
                    false
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppDimensions.spacing_medium)
    ) {
        // Messages area with proper list handling
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = AppDimensions.spacing_medium),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing_small)
        ) {
            items(chatMessages) { message ->
                ChatMessageItem(message)
            }
        }

        // Input area with modern Material3 components
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppDimensions.spacing_small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing_small)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(AppStrings.AI_CHAT_PLACEHOLDER) },
                shape = InputFieldShape,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )

            FloatingActionButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        // Add user message
                        val userMessage = ChatMessage(messageText.trim(), true)
                        chatMessages = chatMessages + userMessage

                        // Simulate AI response
                        val aiResponse = ChatMessage(
                            "I received your message: '${messageText.trim()}'. This is a simulated response.",
                            false
                        )
                        chatMessages = chatMessages + aiResponse

                        // Clear input
                        messageText = ""
                    }
                },
                shape = FloatingActionButtonShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = AppStrings.AI_CHAT_SEND,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/**
 * Displays a single chat message bubble with styling and alignment based on the sender.
 *
 * User messages are right-aligned with primary container colors and an outgoing bubble shape, while AI messages are left-aligned with surface variant colors and an incoming bubble shape.
 *
 * @param message The chat message to display.
 */
@Composable
fun ChatMessageItem(message: ChatMessage) {
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val background = if (message.isFromUser)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant

    val textColor = if (message.isFromUser)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    val bubbleShape = if (message.isFromUser)
        ChatBubbleOutgoingShape
    else
        ChatBubbleIncomingShape

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .align(alignment)
                .widthIn(max = 280.dp) // Maximum width for message bubbles
                .clip(bubbleShape)
                .background(background)
                .padding(AppDimensions.spacing_medium),
        ) {
            Text(
                text = message.content,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Displays a design-time preview of the AI chat screen using the custom AuraFrameFXTheme.
 */
@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    AuraFrameFXTheme { // Using our custom theme for preview
        AiChatScreen()
    }
}
