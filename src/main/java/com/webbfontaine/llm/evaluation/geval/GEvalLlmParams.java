package com.webbfontaine.llm.evaluation.geval;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;

/**
 * {@code GEvalLlmParams} is an immutable data structure representing the parameters
 * required for configuring a language model evaluation process.
 *
 * <p>This record ensures that both parameters, {@code chatLanguageModel} and {@code objectMapper},
 * are non-null through validation in its compact constructor.
 *
 * @param chatLanguageModel the chat language model to be used in evaluations; cannot be null
 * @param objectMapper      the Jackson {@link ObjectMapper} instance for JSON processing; cannot be null
 */
public record GEvalLlmParams(
    ChatLanguageModel chatLanguageModel,
    ObjectMapper objectMapper
) {

    /**
     * Constructs a new {@code GEvalLlmParams} instance, ensuring non-null values
     * for both parameters.
     *
     * @param chatLanguageModel the chat language model to be used in evaluations
     * @param objectMapper      the Jackson {@link ObjectMapper} instance for JSON processing
     * @throws IllegalArgumentException if {@code chatLanguageModel} or {@code objectMapper} is null
     */
    public GEvalLlmParams {
        if (chatLanguageModel == null) {
            throw new IllegalArgumentException("The chatLanguageModel cannot be null");
        }
        if (objectMapper == null) {
            throw new IllegalArgumentException("The objectMapper cannot be null");
        }
    }

}
