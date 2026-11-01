### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:10:55Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides stable numeric error codes and messages for billing, storage, network, reporting, import, authentication, and ledger-lock failures.

#### Purpose
- `PlatformErrorCode` is a generated catalogue mapping each platform error identifier to its numeric code and message.
- Supports lookup of an error-code enum constant by its numeric value.

#### Type
- `PlatformErrorCode` — public enum; package `example.errors`.
- Defines 6,000 constants, each initialized with an `int` number and a `String` message.

#### Input
- Enum constant initialization supplies each code’s number and message.
- `byNumber(int number)` accepts a numeric code for lookup.

#### Output
- Each enum constant exposes its number and message through accessors.
- `byNumber` returns the matching `PlatformErrorCode`, or `null` when no number matches.
- Static initialization builds `BY_NUMBER`, a number-to-enum lookup map.

#### Core logic
- Error constants numbered 1–2,700 and 3,301–6,000 follow a repeating six-category sequence: billing, storage, network, report, import, and auth.
- Numbers 2,701–3,300 are `LEDGER_LOCK` errors.
- A static initializer iterates over `values()` and maps each constant’s `number` to that constant.
- Each constant stores its supplied `number` and `message` in final instance fields.

#### Public API
- `getNumber()` -> `int` — return numeric error code.
- `getMessage()` -> `String` — return error message.
- `byNumber(int number)` -> `PlatformErrorCode` — find code by number.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Exceptions / Errors
- `byNumber` returns `null` for an unregistered number; no explicit exception handling.

#### Concurrency
- Enum constants and their `number` and `message` fields are immutable.
- `BY_NUMBER` is populated during class initialization and has no further mutations in this file.
