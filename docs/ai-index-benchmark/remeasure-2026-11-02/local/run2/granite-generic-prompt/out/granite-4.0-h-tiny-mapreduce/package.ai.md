### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T01:33:32Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A Java library providing a structured, enum-based error code mapping system for platform error handling; A comprehensive inventory catalog generator for products in categories cat-039 and cat-040, providing SKU, title, price, and warehouse stock data in XML format; A SQL object that models financial ledger transfers between accounts, recording deposits and withdrawals with timestamps.

#### Purpose
- Maps integer error codes to human-readable descriptions for platform error handling.
- Generates a structured inventory catalog XML file from product and category data.
- Models financial ledger transfers between accounts, tracks account balances, and supports CRUD operations on transfers.

#### Responsibilities
- Defines a final class `PlatformErrorCode` that extends `Object` and implements `Serializable`.
- Provides a static block to populate a `Map<Integer, PlatformErrorCode>` for efficient lookup.
- Exposes methods to retrieve the error code identifier and human-readable description.
- Reads product and category XML data.
- Extracts SKU, title, price, and warehouse-specific stock information.
- Outputs XML preserving the hierarchical structure of categories and products.
- Stores individual transfers with `transfer_id`, `from_account`, `to_account`, `amount_cents`, and `booked_at`.
- Maintains account balances (`account_id`, `balance_cents`).
- Populates `ledger_transfer` with 6,500 rows, inserting transfers between accounts and recording amounts and dates.
- Validates `from_account` and `to_account` against `ledger_account`.

#### Key units
- **PlatformErrorCode**: A final class extending `Object` and implementing `Serializable`, serving as the base for error codes.
- **BY_NUMBER**: A static `Map<Integer, PlatformErrorCode>` that maps error numbers to `PlatformErrorCode` instances.
- **byNumber(int)**: A static method that retrieves a `PlatformErrorCode` instance from the `BY_NUMBER` map.
- **ledger_transfer** (`transfer_id` PK, `from_account` FK, `to_account` FK, `amount_cents`, `booked_at`): stores transfer records.
- **ledger_account** (`account_id` PK, `balance_cents`): stores account balances.
- **ledger_category** (`category_id` PK, `name`): categorizes transfers (unused).

#### Data flow
Error codes are defined as enum values within `PlatformErrorCode`. During static initialization, a `Map<Integer, PlatformErrorCode>` named `BY_NUMBER` is populated with all enum values. The `byNumber(int)` method allows retrieval of a `PlatformErrorCode` instance using the error number, enabling efficient error handling across the platform.

Error XML parsing involves reading `<category>` elements, then iterating over `<product>` children within each category to extract SKU, title, price, and stock levels. The processed data is then used to generate structured XML representing the inventory catalog.

The insert routine reads `ledger_account` to validate `from_account` and `to_account`. It then inserts 6,500 rows into `ledger_transfer`, linking accounts and recording amounts and dates. Balance checks are performed during inserts by reading from `ledger_account`.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`
- XML parsing library (e.g., DOM, SAX) for reading and processing XML data.
- PostgreSQL for SQL operations.

#### Cross-cutting
- The `BY_NUMBER` map is immutable after static initialization, ensuring thread safety.
- Assumes well-formed XML input; no error handling for malformed data.
- Uses PostgreSQL syntax and conventions.
- No error handling or concurrency notes present.
