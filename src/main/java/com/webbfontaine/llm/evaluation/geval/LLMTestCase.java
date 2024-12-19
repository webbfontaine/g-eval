package com.webbfontaine.llm.evaluation.geval;

/**
 * Represents a test case for a Language Model (LLM) that contains an input,
 * the actual output produced by the model, and the expected output.
 * This class is designed to validate and format test cases for evaluation.
 *
 * <p>Usage Example:</p>
 * <pre><code>
 * final var llmTestCase = new LLMTestCase(
 *     "Get means of payment for receipt id 352 with all fields in the table",
 *     "SELECT * FROM payment_means WHERE receipt = 352",
 *     "select * from payment_means means where means.receipt = 352"
 * );
 *
 * </code></pre>
 */
public class LLMTestCase {
    private final String input;
    private final String actualOutput;
    private final String expectedOutput;

    /**
     * Constructs an {@code LLMTestCase} instance.
     *
     * @param input          the input string provided to the language model.
     * @param actualOutput   the actual output produced by the language model.
     * @param expectedOutput the expected output that is considered correct or desired.
     * @throws IllegalArgumentException if any of the provided arguments are null or empty.
     */
    public LLMTestCase(final String input, final String actualOutput, final String expectedOutput) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("The input cannot be null or empty");
        }

        if (actualOutput == null || actualOutput.isBlank()) {
            throw new IllegalArgumentException("The actualOutput cannot be null or empty");
        }

        if (expectedOutput == null || expectedOutput.isBlank()) {
            throw new IllegalArgumentException("The actualOutput cannot be null or empty");
        }

        this.input = input;
        this.actualOutput = actualOutput;
        this.expectedOutput = expectedOutput;
    }

    /**
     * Generates a formatted string representation of the test case.
     *
     * <p>The output includes the input, the actual output, and the expected output,
     * formatted in a human-readable way.</p>
     *
     * @return a formatted string containing the input, actual output, and expected output.
     */
    public String generateText() {
        return String.format("Input:\n%s\n\nActual Output:\n%s\n\nExpected Output:\n%s\n\n", input, actualOutput, expectedOutput);
    }

}
