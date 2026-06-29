### main/java/net/ladenthin/maven/llamacpp/aiindex/prompt
- H: 1.0
- C: DF3A2A2D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:22:42Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiPreparedPrompt.java](AiPreparedPrompt.java.ai.md)
- F: [AiPromptDefinition.java](AiPromptDefinition.java.ai.md)
- F: [AiPromptPreparationSupport.java](AiPromptPreparationSupport.java.ai.md)
- F: [AiPromptSupport.java](AiPromptSupport.java.ai.md)
- F: [package-info.java](package-info.java.ai.md)
---
> Enables structured AI prompt generation and source text trimming for Maven-based code indexing workflows.

#### Purpose
- Manages AI prompt templates and their configurations
- Prepares source code inputs for AI processing by trimming to character limits

#### Responsibilities
- Prompt definition and configuration management
- Source text preparation and trimming logic
- Prompt rendering and template substitution
- Character limit enforcement and validation

#### Key units
AiPreparedPrompt represents the result of prompt preparation with trimming metrics  
AiPromptDefinition encapsulates key-template pairs for AI prompt configurations  
AiPromptPreparationSupport handles source text trimming and prompt construction  
AiPromptSupport renders prompts using registered templates and substitutions  

#### Data flow
Source code text is trimmed by AiPromptPreparationSupport to fit within character limits, then rendered into a final prompt string by AiPromptSupport using configured templates and substitutions

#### Dependencies
net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest  
net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord  
lombok.EqualsAndHashCode  
lombok.ToString  

#### Cross-cutting
Immutable design across AiPreparedPrompt ensures thread-safe usage  
Lombok annotations reduce boilerplate for value objects and diagnostics  
Null validation occurs at construction time in AiPromptSupport and AiPreparedPrompt constructors  
Prompt preparation logic consistently handles template rendering and character budgeting
