### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:20:36Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child AI file-matching conditions for AND/OR boolean combinators.
#### Purpose
- Holds child AiCondition list backing `<and>` / `<or>` combinators in AiCondition.
- Enables Maven plugin binding as `<conditions><condition>…</condition></conditions>`.
#### Type
- public class AiConditionGroup, Lombok @ToString, @SuppressWarnings NullAway.Init.
#### Input
- Collection<AiCondition> conditions via setConditions, nullable, defensively copied via ArrayList.
- Maven XML `<conditions>` element bound to conditions field.
#### Output
- List<AiCondition> conditions via getConditions, nullable when unconfigured.
- Mutated conditions field via setConditions.
#### Core logic
- AiConditionGroup wraps List<AiCondition> to avoid Maven singularization of `and`/`or` item names.
- getConditions exposes live nullable child AiCondition list.
- setConditions copies input Collection<AiCondition> into new ArrayList or null.
#### Public API
- AiConditionGroup() -> AiConditionGroup — creates empty condition group.
- getConditions() -> List<AiCondition> — returns child conditions or null.
- setConditions(Collection<AiCondition>) -> void — sets child conditions defensively.
#### Dependencies
- java.util.ArrayList, java.util.Collection, java.util.List.
- lombok.ToString, org.jspecify.annotations.Nullable.
- net.ladenthin.srcmorph.config.AiCondition.
#### Exceptions / Errors
- Null-tolerant: getConditions returns null when none configured, setConditions null clears field.
