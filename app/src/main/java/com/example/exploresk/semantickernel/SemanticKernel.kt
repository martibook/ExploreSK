package com.example.exploresk.semantickernel

import com.azure.ai.openai.OpenAIAsyncClient
import com.microsoft.semantickernel.Kernel
import com.microsoft.semantickernel.SKBuilders
import com.microsoft.semantickernel.chatcompletion.ChatCompletion
import com.microsoft.semantickernel.chatcompletion.ChatHistory
import com.microsoft.semantickernel.connectors.ai.openai.util.ClientType
import com.microsoft.semantickernel.connectors.ai.openai.util.OpenAIClientProvider
import com.microsoft.semantickernel.semanticfunctions.PromptTemplateConfig
import com.microsoft.semantickernel.textcompletion.TextCompletion

class SemanticKernel {
    private val clientSupportedByTextDavinci003: OpenAIAsyncClient by lazy {
        val clientProvider = OpenAIClientProvider(
            mapOf(), // NOTE: add ai resource (key, endpoint, deployment name) as parameters
            ClientType.AZURE_OPEN_AI,
        )
        clientProvider.asyncClient
    }

    private val clientSupportedByGPT35Turbo: OpenAIAsyncClient by lazy {
        val clientProvider = OpenAIClientProvider(
            mapOf(), // NOTE: add ai resource (key, endpoint, deployment name) as parameters
            ClientType.AZURE_OPEN_AI,
        )
        clientProvider.asyncClient
    }

    private val textCompletion: TextCompletion by lazy {
        // NOTE: the ai resource (key, endpoint, deployment name) should correspond to model specified here.
        SKBuilders.textCompletionService()
            .setModelId("text-davinci-003")
            .withOpenAIClient(clientSupportedByTextDavinci003)
            .build()
    }

    private val chatCompletion: ChatCompletion<ChatHistory> by lazy {
        // NOTE: the ai resource (key, endpoint, deployment name) should correspond to model specified here.
        SKBuilders.chatCompletion()
            .setModelId("gpt-35-turbo")
            .withOpenAIClient(clientSupportedByGPT35Turbo)
            .build()
    }

    private val azureSupportedTextCompletionKernel: Kernel by lazy {
        SKBuilders.kernel()
            .withDefaultAIService(textCompletion)
            .build()
    }

    private val azureSupportedChatCompletionKernel: Kernel by lazy {
        SKBuilders.kernel()
            .withAIService(
                "gpt-35-turbo",
                chatCompletion,
                true,
                chatCompletion.javaClass,
            )
            .build()
    }

    private val azureSupportedTextAndChatCompletion: Kernel by lazy {
        SKBuilders.kernel()
            .withDefaultAIService(textCompletion)
            .withAIService(
                "gpt-35-turbo",
                chatCompletion,
                false,
                chatCompletion.javaClass,
            )
            .build()
    }

    val kernel: Kernel by lazy {
        azureSupportedChatCompletionKernel
    }

    fun inlineSemanticFunctionExample() {
        println("======== Inline Function Definition ========")

        // Function defined using few-shot design pattern
        val functionDefinition: String = """
                    Generate a creative reason or excuse for the given event.
                    Be creative and be funny. Let your imagination run wild.
                    
                    Event: I am running late.
                    Excuse: I was being held ransom by giraffe gangsters.
                    
                    Event: I haven't been to the gym for a year
                    Excuse: I've been too busy training my pet dragon.
                    
                    Event: {{${'$'}input}}
                
        """.trimIndent()

        // Create function via builder
        val excuseFunction = DefaultChatSKFunction.Builder()
            .withKernel(kernel)
            .setPromptTemplate(functionDefinition)
            .setCompletionConfig(
                PromptTemplateConfig.CompletionConfigBuilder()
                    .maxTokens(100)
                    .temperature(0.4)
                    .topP(1.0)
                    .build(),
            )
            .build()

        val result = excuseFunction.invokeAsync("I missed the F1 final race").block()
        println(result.result)
    }
}

fun main() {
    val sk = SemanticKernel()
    sk.inlineSemanticFunctionExample()
}
