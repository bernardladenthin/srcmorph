### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:36:32Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> Provides a stable numeric catalogue of platform failure codes and messages for billing, storage, network, reporting, import, authentication, and ledger-lock domains.

#### Purpose
- Defines named platform errors with numeric identifiers and descriptive messages.
- Supports resolving an error code number to its catalogue entry.

#### Responsibilities
- Catalogue sequential error codes 1–6000, assigning messages across the listed platform domains.
- Provide access to each entry’s number and message and lookup by number.

#### Key units
- `PlatformErrorCode` — enum of named errors, with `getNumber()`, `getMessage()`, and `byNumber(int)`.

#### Data flow
- Enum constants provide numeric codes and messages; class initialization builds a number-to-enum map.
- `byNumber(int)` looks up the map and returns the matching entry, or `null` for an unknown code.

#### Dependencies
- Uses `java.util.HashMap` and `java.util.Map` for the lookup index.

#### Cross-cutting
- Enum entries and their fields are immutable; the private static lookup map is populated during class initialization and read by lookups.
