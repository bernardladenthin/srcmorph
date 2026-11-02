### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:51:37Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A comprehensive inventory catalog generator for products in categories cat-039 and cat-040, providing SKU, title, price, and warehouse stock data in XML format.

## Purpose
- Generates a static inventory catalog XML file from product and category data.

## Responsibilities
- Parses XML structure containing product SKUs, titles, prices, and warehouse-specific stock counts.
- Iterates over `<category>` elements and their `<product>` children.
- Extracts SKU, title, price, and `<inv:stock>` values for each product.
- Outputs structured XML preserving the inventory hierarchy.

## Key units
- **XML parsing**: Reads and processes the `<product>` and `<category>` elements to extract inventory details.
- **Inventory data extraction**: Retrieves SKU, title, price, and stock levels per warehouse from the XML input.

## Data flow
1. **Input**: XML file (`catalog.xml`) containing product and category data.
2. **Parsing**: XML parser reads the structure and extracts relevant inventory details.
3. **Processing**: Iterates over categories and products, collecting SKU, title, price, and stock information.
4. **Output**: Generates structured XML output representing the inventory catalog.

## Dependencies
- XML parsing library (e.g., DOM, SAX) for reading and processing the XML file.

## Cross-cutting
- Assumes well-formed XML input; no error handling for malformed XML.

## SQL Components
- **ledger_transfer** (`ledger_transfer.sql`):  
  - Columns: `transfer_id` (PK), `from_account` (FK), `to_account` (FK), `amount_cents` (NOT NULL), `booked_at` (NOT NULL).  
  - Primary key: `pk_ledger_transfer`.  
  - Unique constraint: `uk_ledger_transfer` (unique combination of `from_account`, `to_account`, `booked_at`).  
  - Index: `idx_ledger_transfer_booked_at` on `booked_at`.  
- **ledger_account** (`ledger_account.sql`):  
  - Columns: `account_id` (PK), `balance_cents`.  
- **ledger_category** (`ledger_category.sql`):  
  - Columns: `category_id` (PK), `name` (NOT NULL).  

## Data flow
1. **INSERT** statements in `ledger_transfer.sql` populate `ledger_transfer` with 6,500 rows, each inserting a transfer between two accounts and recording the amount and booking date.  
2. Corresponding rows are inserted into `ledger_account` to ensure referenced accounts exist.  
3. `ledger_category` remains unused in this file.

## Dependencies
- **ledger_account**: referenced by foreign keys (`from_account`, `to_account`).  
- **ledger_category**: referenced but not used in this file.

## Cross-cutting
- Uses PostgreSQL syntax and conventions.  
- No procedures, functions, or triggers defined.  
- No external modules or packages beyond standard SQL.
