### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:34:15Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A centralized enumeration that maps platform error numbers to descriptive messages and offers fast lookup by numeric ID.

#### Purpose
- Central repository of platform error codes for billing, storage, network, report, import, auth, and ledger lock failures.

#### Type
`public enum PlatformErrorCode` – enum with many constants, each defined as `CODE_NAME(int id, String description)`; implements a static lookup map.

#### Input
- Constructor parameters: `int id`, `String description`.
- No external dependencies or injected fields.

#### Output
- Public fields: `private final int id; private final String description;`.
- Public methods: `int getId()`, `String getDescription()`, `static PlatformErrorCode forId(int id)`, `static Map<Integer, PlatformErrorCode> getIdMap()`.

#### Core logic
- Builds a static `Map<Integer, PlatformErrorCode> ID_MAP` in a static block by iterating over `values()`.
- `forId(int)` retrieves a constant by its numeric ID, throwing `IllegalArgumentException` if not found.
- `getIdMap()` returns the immutable lookup map.

#### Public API
- `getId() -> error code integer` – returns the numeric ID of the error.
- `getDescription() -> human‑readable message` – returns the error description.
- `forId(int) -> PlatformErrorCode` – lookup by numeric ID, throws if unknown.
- `values() -> PlatformErrorCode[]` – auto‑generated array of all constants.
- `valueOf(String) -> PlatformErrorCode` – auto‑generated lookup by name.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Exceptions / Errors
- `forId` throws `IllegalArgumentException` when the ID is not present in the map.

#### Concurrency
- The static `ID_MAP` is immutable after construction; safe for concurrent reads.
