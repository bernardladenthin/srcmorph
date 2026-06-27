### ConvertToRecord.java
- H: 1.0
- C: BF72A15E
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:08:14Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Marks classes for migration to Java records upon target bytecode upgrade.

#### Purpose
- Identifies classes suitable for refactoring into Java records.
- Serves as a migration marker for future bytecode upgrades.

#### Type
- Annotation type: `@interface ConvertToRecord`
- Modifiers: none
- Notable annotations: none

#### Input
- None

#### Output
- None

#### Core logic
- Acts as a compile-time marker for future code transformation.
- Provides no runtime behavior; used exclusively during development.

#### Public API
- `ConvertToRecord()` -> void: Marks a class for record migration.

#### Dependencies
- None

#### Exceptions / Errors
- None

#### Concurrency
- None
