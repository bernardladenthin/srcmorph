### java/errors
- H: 1.0
- C: BBE675B9
- D: 2026-10-31T21:24:26Z
- T: 2026-10-31T22:05:48Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A Java package providing a platform error code enumeration for calculating VAT on invoices

#### Purpose
Calculates VAT for invoices

#### Responsibilities
- Enum `PlatformErrorCode` defines error codes for VAT calculations
- Provides a lookup map `BY_NUMBER` for quick error code retrieval

#### Key units
- `PlatformErrorCode`: Enum representing different error codes for VAT calculations
- `BY_NUMBER`: Map for fast lookup of error codes by their numeric value

#### Data flow
- Enum values are used to represent specific error scenarios
- `BY_NUMBER` map allows retrieving error codes based on numeric identifiers

#### Dependencies
- No external dependencies

#### Cross-cutting
- Immutable enum ensures thread-safety
- No specific configuration or external modules required
