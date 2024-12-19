package com.webbfontaine.llm.evaluation.geval;

/**
 * Represents the result of a test case evaluation.
 * <p>
 * This record encapsulates details about the evaluation of a test case,
 * including whether it passed, the evaluation score, and a description of the score.
 *
 * @param passed      indicates if the test case passed or failed.
 * @param score       the numerical evaluation score of the test case.
 * @param description a detailed explanation of the evaluation score.
 */
public record GEvalMeasureResult(
    boolean passed,
    double score,
    String description
) {
}