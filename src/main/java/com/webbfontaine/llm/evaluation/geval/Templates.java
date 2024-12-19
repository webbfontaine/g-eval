package com.webbfontaine.llm.evaluation.geval;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * The {@code Templates} class contains static template strings for generating
 * JSON-based evaluation results.
 *
 * @see lombok.AllArgsConstructor
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Templates {

    /**
     * Template for generating evaluation results in JSON format based on a set of
     * evaluation steps.
     *
     * <p>The template expects the following placeholders to be replaced:
     * <ul>
     *     <li>{@code {{parameters}}} - Specific information referenced in the reason for the score.</li>
     *     <li>{@code {{evaluation_steps}}} - The steps used for evaluating the provided text.</li>
     *     <li>{@code {{text}}} - The content to be evaluated.</li>
     * </ul>
     *
     * <p>The output must strictly adhere to JSON format, containing a {@code score} key
     * (an integer between 0 and 10) and a {@code reason} key (a concise explanation for the score).
     * The reason should not explicitly quote the score value.
     *
     * <p>Example JSON output:
     * <pre>
     * {
     *     "score": 0,
     *     "reason": "The text does not follow the evaluation steps provided."
     * }
     * </pre>
     *
     * <p>IMPORTANT: The response should consist only of the JSON object with no additional text or explanation.
     */
    public static final String GENERATE_EVALUATION_RESULTS = """
        Given the evaluation steps, return a JSON with two keys: 1) a `score` key ranging from 0 - 10, with 10 being that it follows the criteria outlined in the steps and 0 being that it does not, and 2) a `reason` key, a reason for the given score, but DO NOT QUOTE THE SCORE in your reason. Please mention specific information from {{parameters}} in your reason, but be very concise with it!

        Evaluation Steps:
        {{evaluation_steps}}

        {{text}}

        **
        IMPORTANT: Please make sure to only return in JSON format, with the "score" and "reason" key. No words or explanation is needed.

        Example JSON:
        {{
            "score": 0,
            "reason": "The text does not follow the evaluation steps provided."
        }}
        **

        JSON:""";
}
