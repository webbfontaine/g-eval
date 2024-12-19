package com.webbfontaine.llm.evaluation.geval;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

/**
 * GEval is a framework for evaluating Language Model (LLM) metrics based on the GEval methodology,
 * as described in the research paper: <a href="https://arxiv.org/pdf/2303.16634.pdf">GEval Framework</a>.
 *
 * <p>This class provides functionality to evaluate LLM test cases by:
 * <ul>
 *   <li>Accepting test case inputs, actual outputs, and expected outputs.</li>
 *   <li>Utilizing a chat language model to generate evaluation results.</li>
 *   <li>Comparing results against a defined threshold to determine success.</li>
 * </ul>
 *
 * <p>Usage Example:</p>
 * <pre><code>
 * final GEval gEval = GEval.builder()
 *     .name("Test Evaluation")
 *     .threshold(0.8)
 *     .evaluationSteps(
 *         List.of(
 *             "Check whether the query 'actual output' contradicts any query in 'expected output'. Do not penalize if there is alias difference that do not affect the query result",
 *             "You should also heavily penalize if where condition is incorrect. if where condition is on another column, check if it is used during join operation(s) and is the same as expected column. If so then do not penalize. If join type is not the expected check if it is logically the same and apply 0.1 penalty.",
 *             "You should also heavily penalize if unnecessary joins are made"
 *         )
 *     )
 *     .withGEvalLlmParams(new GEvalLlmParams(chatLanguageModel, objectMapper))
 *     .build();
 *
 * final GEvalMeasureResult result = gEval.measure(new LLMTestCase(
 *     "Get means of payment for receipt id 352 with all fields in the table",
 *     "SELECT * FROM payment_means WHERE receipt = 352",
 *     "select * from payment_means means where means.receipt = 352"
 * ));
 *
 * System.out.println("Evaluation successful: " + result.isSuccessful());
 * System.out.println("Score: " + result.getScore());
 * System.out.println("Reason: " + result.getReason());
 * </code></pre>
 */
@Slf4j
public class GEval {

    private static final String EVALUATION_PARAMS = "Input, Actual Output, and Expected Output";

    private final String name;
    private final double threshold;
    private final List<String> evaluationSteps;
    private final ChatLanguageModel chatLanguageModel;
    private final ObjectMapper objectMapper;

    /**
     * Returns a builder instance to create a {@code GEval} object.
     *
     * @return a {@link GEvalBuilder} instance.
     */
    public static GEvalBuilder builder() {
        return new GEvalBuilder();
    }

    /**
     * Constructs a GEval object with the specified parameters.
     *
     * @param name              the name of the evaluation framework instance.
     * @param threshold         the minimum score threshold for successful evaluation.
     * @param evaluationSteps   a list of steps to guide the evaluation process.
     * @param chatLanguageModel the chat language model used for evaluation.
     * @param objectMapper      the JSON object mapper for parsing AI responses.
     * @throws IllegalArgumentException if {@code name} is null or empty, {@code threshold} is not between 0 and 1,
     *                                  or {@code evaluationSteps} is null or empty.
     */
    private GEval(
        final String name,
        final double threshold,
        final List<String> evaluationSteps,
        final ChatLanguageModel chatLanguageModel,
        final ObjectMapper objectMapper
    ) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }

        if (threshold < 0 || threshold > 1.0) {
            throw new IllegalArgumentException("Threshold must be between 0 and 1.");
        }

        if (ObjectUtils.isEmpty(evaluationSteps)) {
            throw new IllegalArgumentException("Evaluation steps cannot be null or empty.");
        }

        this.name = name;
        this.threshold = threshold;
        this.evaluationSteps = evaluationSteps;
        this.chatLanguageModel = chatLanguageModel;
        this.objectMapper = objectMapper;
    }

    /**
     * Measures the performance of a test case using the defined evaluation framework.
     *
     * @param llmTestCase the test case to evaluate.
     * @return a {@link GEvalMeasureResult} containing the success status, score, and reason.
     */
    public GEvalMeasureResult measure(final LLMTestCase llmTestCase) {
        log.debug("Measuring test case - {} via - {}", llmTestCase, name);

        final Prompt prompt = new PromptTemplate(
            Templates.GENERATE_EVALUATION_RESULTS
        ).apply(
            Map.of(
                "parameters", EVALUATION_PARAMS,
                "evaluation_steps", numberEvaluationSteps(),
                "text", llmTestCase.generateText()
            )
        );

        final var aiMessageResponse = chatLanguageModel.generate(SystemMessage.systemMessage(prompt.text()));
        final var text = aiMessageResponse.content().text();
        final var evaluationResponse = parseAIResponseMessage(text);
        final double score = evaluationResponse.score() / 10.0;

        final var gEvalMeasureResult = new GEvalMeasureResult(
            score >= threshold,
            score,
            evaluationResponse.reason()
        );

        log.debug("Successfully measured test case - {} via - {}, result - {}", llmTestCase, name, gEvalMeasureResult);
        return gEvalMeasureResult;
    }

    /**
     * Generates a numbered list of evaluation steps.
     *
     * @return a formatted string of evaluation steps.
     */
    private String numberEvaluationSteps() {
        final var stringBuilder = new StringBuilder();
        for (int i = 0; i < evaluationSteps.size(); i++) {
            stringBuilder.append(i).append(". ").append(evaluationSteps.get(i)).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Parses the AI response message into an {@link EvaluationResponse} object.
     *
     * @param message the response message in JSON format.
     * @return an {@link EvaluationResponse} object.
     * @throws EvaluationMessageParsingRuntimeException if the message cannot be parsed.
     */
    private EvaluationResponse parseAIResponseMessage(final String message) {
        try {
            return objectMapper.readValue(message, EvaluationResponse.class);
        } catch (JsonProcessingException e) {
            throw new EvaluationMessageParsingRuntimeException(message, e);
        }
    }

    /**
     * Builder class for creating instances of {@code GEval}.
     */
    public static final class GEvalBuilder {
        private String name;
        private double threshold;
        private List<String> evaluationSteps;
        private GEvalLlmParams gEvalLlmParams;

        /**
         * Builds and returns a {@code GEval} instance.
         *
         * @return a new {@link GEval} instance.
         * @throws IllegalArgumentException if {@code gEvalLlmParams} is null.
         */
        public GEval build() {
            if (gEvalLlmParams == null) {
                throw new IllegalArgumentException("gEvalLlmParams cannot be null");
            }
            return new GEval(name, threshold, evaluationSteps, gEvalLlmParams.chatLanguageModel(), gEvalLlmParams.objectMapper());
        }

        /**
         * Sets the name for the {@code GEval} instance.
         *
         * @param name the name to set.
         * @return the current {@code GEvalBuilder} instance.
         */
        public GEvalBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the threshold for evaluation success.
         *
         * @param threshold the threshold value to set.
         * @return the current {@code GEvalBuilder} instance.
         */
        public GEvalBuilder threshold(final double threshold) {
            this.threshold = threshold;
            return this;
        }

        /**
         * Sets the evaluation steps for the {@code GEval} instance.
         *
         * @param evaluationSteps the list of steps to set.
         * @return the current {@code GEvalBuilder} instance.
         */
        public GEvalBuilder evaluationSteps(final List<String> evaluationSteps) {
            this.evaluationSteps = evaluationSteps;
            return this;
        }

        /**
         * Sets the {@code GEvalLlmParams} containing the chat language model and object mapper.
         *
         * @param gEvalLlmParams the parameters to set.
         * @return the current {@code GEvalBuilder} instance.
         */
        public GEvalBuilder withGEvalLlmParams(final GEvalLlmParams gEvalLlmParams) {
            this.gEvalLlmParams = gEvalLlmParams;
            return this;
        }
    }

}
