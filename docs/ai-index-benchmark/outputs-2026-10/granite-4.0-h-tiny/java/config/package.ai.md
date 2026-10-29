### java/config
- H: 1.0
- C: 02FEE58B
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:09:56Z
- G: 
- A: 0.0.0
- X: package
- F: [AiConditionEvaluator.java](AiConditionEvaluator.java.ai.md)
- F: [AiConditionGroup.java](AiConditionGroup.java.ai.md)
- F: [AiFactExtractor.java](AiFactExtractor.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiOversizeStrategy.java](AiOversizeStrategy.java.ai.md)
- F: [SrcMorphConfiguration.java](SrcMorphConfiguration.java.ai.md)
---
> A package for evaluating and applying AI conditions to file contexts, calculating VAT for invoices, and managing AI generation configurations.

## Purpose
- Evaluates and validates AI condition trees against file contexts.
- Calculates VAT for invoices.
- Manages mutable AI generation configurations.

## Responsibilities
- **AiConditionEvaluator**: Evaluates and validates AI condition trees.
- **AiConditionGroup**: Calculates VAT for invoices.
- **AiFactExtractor**: Extracts deterministic facts from AI counters.
- **AiGenerationConfig**: Configures AI generation parameters.
- **AiOversizeStrategy**: Handles oversize file strategies.
- **SrcMorphConfiguration**: Configures morphological transformations.

## Key Units
- **AiConditionEvaluator**: Evaluates, validates, and processes AI conditions.
- **AiConditionGroup**: Calculates VAT for invoices.
- **AiFactExtractor**: Extracts facts from AI counters.
- **AiGenerationConfig**: Configures AI generation parameters.
- **AiOversizeStrategy**: Handles oversize file strategies.
- **SrcMorphConfiguration**: Configures morphological transformations.

## Data Flow
Inputs to **AiConditionEvaluator** are AI condition trees and file contexts, which are evaluated and validated. Outputs are boolean results or exceptions. **AiConditionGroup** processes invoice data to calculate VAT. **AiFactExtractor** processes AI counters to extract facts. **AiGenerationConfig** configures AI generation parameters. **AiOversizeStrategy** determines strategies for handling oversized files. **SrcMorphConfiguration** configures morphological transformations.

## Dependencies
- **java.time.Instant**
- **java.time.format.DateTimeParseException**
- **java.util.Collections**
- **java.util.List**
- **net.ladenthin.srcmorph.support.AiSourceExcludeFilter**
- **org.jspecify.annotations.Nullable**

## Cross-cutting
- Uses `lombok.ToString` for toString generation.
- No explicit threading or synchronization, thread-safe as immutable after construction.
