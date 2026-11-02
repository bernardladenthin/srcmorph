### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T01:12:46Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A Java package providing a platform error code enumeration with integer identifiers and human-readable messages for robust error handling.

## Purpose
- Maps integer error codes to human-readable descriptions for platform error handling.

## Responsibilities
- Defines a final class `PlatformErrorCode` for error code representation.
- Provides static initialization to populate a map of error codes by their integer identifiers.
- Offers getters for retrieving the error code number and its human-readable message.
- Ensures thread-safe access to the error code mappings.

## Key units
- **PlatformErrorCode**: final class extending `Object` and implementing `Serializable`, representing an error code with a number and message.
- **BY_NUMBER**: static `Map<Integer, PlatformErrorCode>` mapping error codes to their instances.
- **byNumber(int)**: static method returning a `PlatformErrorCode` instance by its integer identifier.

## Data flow
1. The static block in `PlatformErrorCode` populates the `BY_NUMBER` map with all enum values.
2. The constructor initializes each `PlatformErrorCode` instance with a number and message.
3. `byNumber(int)` retrieves the corresponding `PlatformErrorCode` from the `BY_NUMBER` map.

## Dependencies
- `java.util.HashMap`
- `java.util.Map`

## Cross-cutting
- Uses immutable static maps for error code mappings, ensuring thread safety.
- No shared mutable state, making the class safe for concurrent access.
- No recurring patterns beyond the core error code functionality.

## SQL Object for Financial Ledger Transfers

#### Purpose
- Models financial ledger transfers between accounts, tracks account balances, and supports CRUD operations on transfers.

#### Responsibilities
- **ledger_transfer** table: stores individual transfers with `transfer_id`, `from_account`, `to_account`, `amount_cents`, and `booked_at`.
- **ledger_account** table: maintains account balances (`balance_cents`).
- **ledger_category** table: (not used in this file) would categorize transfers.
- **Routine logic**: 6,500 `INSERT` statements populate `ledger_transfer` with transfers between accounts, inserting corresponding rows into `ledger_account` (via foreign keys).

#### Key units
- **ledger_transfer** (`ledger_transfer.sql`):  
  - Columns: `transfer_id` PK, `from_account` FK, `to_account` FK, `amount_cents` NOT NULL, `booked_at` NOT NULL.  
  - Primary key: `pk_ledger_transfer`.  
  - Unique constraint: `uk_ledger_transfer` on (`from_account`, `to_account`, `booked_at`).  
  - Index: `idx_ledger_transfer_booked_at` on `booked_at`.  
- **ledger_account** (`ledger_account.sql`):  
  - Columns: `account_id` PK, `balance_cents`.  
- **ledger_category** (`ledger_category.sql`):  
  - Columns: `category_id` PK, `name` NOT NULL.  

#### Data flow
1. **INSERT** statements in `ledger_transfer.sql` populate `ledger_transfer` with 6,500 rows, each inserting a transfer between two accounts and recording the amount and booking date.  
2. Corresponding rows are inserted into `ledger_account` via foreign key constraints.  
3. `ledger_category` remains unused in this file.

#### Dependencies
- **Internal**: `ledger_account` (referenced by foreign keys), `ledger_category` (referenced but not used).  
- **External**: PostgreSQL (PostgreSQL-specific syntax and functions).
