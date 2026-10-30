### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:38:24Z
- G: 
- A: 0.0.0
- X: file
---
Preparing a dense, structured markdown summary of AiConditionGroup.java.

The class is public and has no superclass or interfaces. It holds a private nullable List<AiCondition> named conditions, which the Maven plugin framework binds to XML elements. It uses @ToString and suppresses NullAway.Init and uninitialized field warnings.

The class has a no-op public constructor. It exposes getConditions returning the list and setConditions taking a nullable Collection, which defensively copies into an ArrayList or sets null. No exceptions are thrown.

The returned list is the internal ArrayList, so callers can mutate it directly. The class is not synchronized.> Groups nested child AI conditions for an AND/OR combinator so Maven binds them as a conditions list.

#### Purpose
- Configuration holder for the body of an `<and>` or `<or>` combinator in `AiCondition`.
- Field name `conditions` lets the Maven plugin framework bind items as `<conditions><condition>…</condition></conditions>` (singular of the field), instead of requiring `<and>`/`<or>` item elements.

#### Type
- class, public; no extends or implements.
- `@ToString`; `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- One field: `private @Nullable List<AiCondition> conditions`.

#### Input
- No-arg constructor; no injected dependencies.
- `setConditions` consumes a `@Nullable Collection<AiCondition>`.

#### Output
- `getConditions` returns the stored `@Nullable List<AiCondition>` (the internal list, not a copy).
- `setConditions` replaces `conditions` with a new `ArrayList` copy, or `null` when the argument is `null`.

#### Core logic
- Empty `AiConditionGroup` constructor is a no-op so the Maven plugin framework can instantiate the group.
- `setConditions` null-checks the incoming `Collection<AiCondition>` and, if non-null, defensively copies it into a new `ArrayList<AiCondition>`.
- `getConditions` returns that list, or `null` when no child conditions were configured.
- Used as the nested body of an `AiCondition` `<and>` / `<or>` combinator rather than a direct `List<AiCondition>` on those combinator fields.

#### Public API
- `AiConditionGroup() -> AiConditionGroup` — creates an empty group.
- `getConditions() -> @Nullable List<AiCondition>` — returns child conditions or null.
- `setConditions(@Nullable Collection<AiCondition>) -> void` — stores a defensive copy or null.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCondition` (`net.ladenthin.srcmorph.config`)

#### Exceptions / Errors
- No thrown or caught exceptions.
- Null collection is stored as null; `getConditions` may return null when none were configured.

#### Concurrency
- Not synchronized and not immutable.
- Setter copies on write; getter returns the live internal `ArrayList`, so callers can mutate the stored list.
