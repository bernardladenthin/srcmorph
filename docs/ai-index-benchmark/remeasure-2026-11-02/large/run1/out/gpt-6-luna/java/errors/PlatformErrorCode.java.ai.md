### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:35:01Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides stable numeric error codes and messages for billing, storage, network, reporting, import, authentication, and ledger-lock failures.

#### Purpose
- `PlatformErrorCode` is the generated catalogue of named platform failures, each mapped to an integer code and message.
- Supports looking up a catalogue entry by numeric code.

#### Type
- `PlatformErrorCode` — public enum; package `example.errors`.
- No declared interfaces, generics, or annotations.

#### Input
- Enum constants supply an `int number` and `String message`.
- `byNumber(int number)` accepts a numeric error code.

#### Output
- Each constant exposes its number and message.
- `byNumber` returns the matching `PlatformErrorCode`, or `null` when no number matches.
- Static initialization builds a number-to-enum lookup map.

#### Core logic
- Defines constants for codes 1–6000, with sequential numeric values and messages formatted as `<domain> failure <number>`.
- Codes 1–2700 cycle through `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH`; codes 2701–3300 use `LEDGER_LOCK`; codes 3301–6000 resume the six-domain cycle.
- Builds `BY_NUMBER` from all enum values during class initialization.
- Looks up codes through `BY_NUMBER.get(number)`.

#### Public API
- `getNumber() -> int` — returns numeric error code.
- `getMessage() -> String` — returns error message.
- `byNumber(int) -> PlatformErrorCode` — finds code by number.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`
- `example.errors.PlatformErrorCode`

#### Exceptions / Errors
- Unknown numeric codes return `null`; no explicit exception handling or validation.

#### Concurrency
- Enum constants and their `number` and `message` fields are immutable.
- `BY_NUMBER` is a private static `HashMap`, populated during class initialization and read by lookup; it is not exposed for mutation.
