package com.webbfontaine.llm.evaluation.geval;

import static java.lang.String.format;

/**
 * {@code EvaluationMessageParsingRuntimeException} is a custom runtime exception
 * that is thrown when an AI evaluation message fails to parse.
 *
 * <p>This exception provides a detailed message including the problematic AI
 * response message and the underlying cause of the failure.
 *
 * @see RuntimeException
 */
public class EvaluationMessageParsingRuntimeException extends RuntimeException {

    /**
     * Constructs a new {@code EvaluationMessageParsingRuntimeException} with the
     * specified AI response message and cause.
     *
     * @param aiResponseMessage the AI response message that failed to parse
     * @param cause             the underlying cause of the parsing failure
     */
    public EvaluationMessageParsingRuntimeException(final String aiResponseMessage, final Throwable cause) {
        super(format("Failed to parse evaluation ai message for %s", aiResponseMessage), cause);
    }
}
