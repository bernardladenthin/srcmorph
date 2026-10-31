### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:14:10Z
- G: 
- A: 0.0.0
- X: file
---
> Groups AI file-matching conditions for AND/OR combinator evaluation.
#### Purpose
- Holds child AiCondition list backing `<and>` / `<or>` combinators in AiCondition.
- Enables Maven XML binding as `<conditions><condition>…</condition></conditions>`.
#### Type
- public class AiConditionGroup.
- Annotations: Lombok @ToString, @SuppressWarnings NullAway.Init.
#### Input
- Collection<AiCondition> conditions via setConditions, nullable, defensively copied.
- AiCondition child elements from Maven plugin configuration.
#### Output
- List<AiCondition> via getConditions, nullable when unconfigured.
- Mutated field conditions in AiConditionGroup.
#### Core logic
- Wraps List<AiCondition> conditions to control Maven singularization of collection item element.
- Avoids direct List<AiCondition> in AiCondition where field names `and`/`or` would misbind items.
- Defensive copy on set with ArrayList, null passthrough preserves unconfigured state.
#### Public API
- AiConditionGroup() -> AiConditionGroup creates empty condition group
- getConditions() -> List<AiCondition> returns child conditions or null
- setConditions(Collection<AiCondition>) -> void defensively copies child conditions
#### Dependencies
- java.util.ArrayList, java.util.Collection, java.util.List
- lombok.ToString, org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.config.AiCondition
#### Exceptions / Errors
- Null-tolerant getConditions/setConditions, null means none configured.
- NullAway.Init suppressed for uninitialized nullable conditions field.
