### AiConditionGroup.java
- H: 1.0
- C: B4457D08
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T21:41:47Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 2; constructors: 1; field declarations (w/ modifier): 0

> Holds a list of child conditions for an and/or combinator in the AI index configuration.

#### Purpose
- Represents the body of an `<and>` or `<or>` combinator in an `AiCondition`.
- Provides a container for child `AiCondition` instances.

#### Type
- Class `AiConditionGroup`  
- Modifiers: public, final (implicit), Lombok `@ToString`.  
- No inheritance or interfaces.

#### Input
- Constructor: no parameters.  
- `setConditions(Collection<AiCondition>)`: accepts a collection of child conditions, may be `null`.

#### Output
- `getConditions()`: returns the list of child conditions or `null`.  
- Internal state: private `@Nullable List<AiCondition> conditions`.

#### Core logic
- `setConditions`: if argument not null, copies into a new `ArrayList`; otherwise sets field to `null`.  
- `getConditions`: simply returns the stored list.

#### Public API
- `AiConditionGroup() -> void` – default constructor.  
- `getConditions() -> @Nullable List<AiCondition>` – retrieve child conditions.  
- `setConditions(@Nullable Collection<AiCondition>) -> void` – set child conditions, defensive copy.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`.  
- `lombok.ToString`.  
- `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCondition`.

#### Exceptions / Errors
- No checked exceptions thrown.  
- Accepts `null` for the collection parameter; results in `null` field.

#### Concurrency
- No synchronization; class is not thread‑safe.  

---
