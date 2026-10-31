### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:34:42Z
- G: 
- A: 0.0.0
- X: file
---
> Binds a list of child AiCondition objects as the body of `<and>`/`<or>` combinators in srcmorph Maven plugin XML configuration via `<conditions><condition>` elements.

#### Purpose
- Container for child AiCondition items inside AiCondition AND/OR combinators
- Field named `conditions` so Maven plugin binds XML items as `<conditions><condition>…</condition></conditions>`

#### Type
- Class (public, mutable, non-final, non-abstract)
- Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- Field: `private @Nullable List<AiCondition> conditions`

#### Input
- `setConditions(final @Nullable Collection<AiCondition> conditions)`: nullable collection of child conditions injected by Maven plugin framework

#### Output
- `getConditions()` -> `@Nullable List<AiCondition>`: internal list reference or null
- `setConditions` mutates `conditions` field (defensive copy to `ArrayList`, or null)
- Lombok `@ToString` generates string representation of the group

#### Core logic
- Stores child AiCondition references in nullable `List` field
- Setter null-checks input: null stays null, non-null `Collection` copied into new `ArrayList` (defensive copy)
- Getter returns field directly without copy (nullable, mutable exposure)
- No validation or transformation of AiCondition items

#### Public API
- `AiConditionGroup()` -> AiConditionGroup: constructs empty condition group
- `getConditions()` -> `@Nullable List<AiCondition>`: returns child conditions or null
- `setConditions(@Nullable Collection<AiCondition>)` -> void: sets children via defensive copy

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable
- AiCondition (net.ladenthin.srcmorph.config)

#### Exceptions / Errors
- Null-tolerant: accepts null in setter, returns null from getter when unset
- No explicit exceptions thrown

#### Concurrency
- Not thread-safe; unsynchronized mutable field
- Defensive copy on write; getter exposes mutable internal list reference
