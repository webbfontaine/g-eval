package com.webbfontaine.llm.evaluation.geval;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@code EvaluationResponse} is an immutable data structure representing
 * the response of an evaluation, including a score and a reason for the score.
 *
 * <p>This record is designed to be used with JSON serialization and deserialization
 * using the Jackson library. The {@link JsonProperty} annotations map the JSON
 * fields to the record components.
 *
 * @param score  the evaluation score, a {@link Double} value ranging from 0 to 10
 * @param reason a concise explanation for the given score
 * @see com.fasterxml.jackson.annotation.JsonProperty
 */
public record EvaluationResponse(
    @JsonProperty("score") Double score,
    @JsonProperty("reason") String reason
) {
}
