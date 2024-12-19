# G-Eval

A Java library implementation based on the [GEval Framework](https://arxiv.org/pdf/2303.16634.pdf).
---

## Features

- **Test case evaluation**: Evaluates the given test case for the provided evaluation steps and determines whether the test case **passed** or **failed**. Additionally, it provides an evaluation score and a detailed explanation of the score.

---
## Example Usage of 'g-eval' Library

### 1. Create a GEval Instance
```java
    final GEval gEval = GEval.builder()
    .name("Test Evaluation")
    .threshold(0.8)
    .evaluationSteps(
        List.of(
            "Check for alias differences.",
            "Penalize incorrect WHERE conditions.",
            "Penalize unnecessary joins."
        )
    )
    .withGEvalLlmParams(new GEvalLlmParams(chatLanguageModel, objectMapper))
    .build();

    final GEvalMeasureResult result = gEval.measure(new LLMTestCase(
        "Get means of payment for receipt id 352 with all fields in the table",
        "SELECT * FROM payment_means WHERE receipt = 352",
        "SELECT * FROM payment_means means WHERE means.receipt = 352"
    ));
```

### Data Representation

The results of the test case evaluation are encapsulated in the `GEvalMeasureResult` record:

```java
public record GEvalMeasureResult(
    boolean passed,        // Indicates if the test case passed or failed
    double score,          // Numerical evaluation score
    String description     // Detailed explanation of the evaluation score
) {
}
```

## References

- [GEval Framework Paper](https://arxiv.org/pdf/2303.16634.pdf)