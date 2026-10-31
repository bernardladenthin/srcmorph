### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:13:50Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child AI file-matching conditions forming AND/OR combinator bodies for config evaluation.
#### Purpose
- Holds child AiCondition list backing `<and>` / `<or>` combinators in AiCondition.
- Enables Maven plugin binding of `<conditions><condition>` XML elements.
#### Type
- class public AiConditionGroup, no extends/implements.
- Annotations Lombok @ToString, @SuppressWarnings NullAway.Init.
#### Input
- setConditions Collection<AiCondition> nullable child conditions, defensively copied via ArrayList.
- Maven XML `<conditions><condition>` items bound to conditions field.
#### Output
- getConditions() -> nullable List<AiCondition> child conditions or null.
- Mutated field conditions List<AiCondition> nullable internal state.
#### Core logic
- Stores nullable conditions list representing AiConditionGroup combinator body.
- setConditions null clears conditions, non-null copies into new ArrayList.
- Field named conditions forces Maven singularization to `<condition>` item elements.
- Avoids direct List<AiCondition> in AiCondition and/or fields requiring `<and>`/`<or>` items.
#### Public API
- AiConditionGroup() -> AiConditionGroup creates empty group.
- getConditions() -> List<AiCondition> returns child conditions or null.
- setConditions(Collection<AiCondition>) -> void sets child conditions defensively.
#### Dependencies
- java.util.ArrayList, java.util.Collection, java.util.List.
- lombok.ToString, org.jspecify.annotations.Nullable.
- net.ladenthin.srcmorph.config.AiCondition.
#### Exceptions / Errors
- Null-tolerant: null input clears conditions, null return means unconfigured.
