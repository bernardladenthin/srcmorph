### AiPromptSupport.java
- H: 1.0
- C: 5839F364
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:58:45Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Renders AI prompt templates for generation requests using registered key-value definitions.

#### Purpose
- Provides prompt string rendering for AI generation tasks.
- Validates and enforces required template definitions at construction time.

#### Type
Final class implementing no interfaces. Extends no type. Uses generics: Map<String, String>. Notable annotations: @ToString.

#### Input
Constructor accepts List<AiPromptDefinition> which must contain non-null key and template fields. buildPrompt methods consume AiGenerationRequest or individual promptKey, sourceFile, sourceText parameters.

#### Output
Returns rendered prompt strings from templates using provided file name and source text substitutions. Throws IllegalArgumentException for missing prompt keys.

#### Core logic
- Initializes internal HashMap with capacity based on input list size.
- Validates each AiPromptDefinition entry for required key and template fields.
- Substitutes filename and source text into registered template strings.
- Throws exception when no matching template is found for a given prompt key.

#### Public API
buildPrompt(request) -> String: Renders prompt from request data.  
buildPrompt(promptKey, sourceFile, sourceText) -> String: Renders prompt from components.

#### Dependencies
Imports: java.util.HashMap, java.util.List, java.util.Map, java.util.Objects, lombok.ToString, net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest, net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper. References: AiPromptDefinition, AiGenerationRequest.

#### Exceptions / Errors
Throws NullPointerException during construction if any definition has null key or template. Throws IllegalArgumentException in buildPrompt if no template exists for given promptKey.

#### Concurrency
No explicit concurrency control; class is immutable after construction. Assumes thread-safe usage of injected dependencies.
