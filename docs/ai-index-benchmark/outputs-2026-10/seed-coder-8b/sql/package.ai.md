### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T00:28:07Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> This package manages lending library operations, e-commerce transactions, and warehouse stock management, providing a comprehensive suite of tools for library, shop, and warehouse functionalities.

#### Purpose
- Manages lending library data and operations
- Facilitates e-commerce transactions
- Maintains and updates warehouse stock levels

#### Responsibilities
- Library data management and lending operations
- Product inventory and sales transaction handling
- Stock management and supplier data integration

#### Key units
- **PostgreSQL schema (tables, indexes)**
- **Views (overdue_loans, member_loan_stats, popular_books)**
- **Functions (fn_checkout_book, fn_late_fee)**
- **Procedures (sp_return_book)**
- **Triggers (trg_loan_audit)**
- **Audit table (loan_audit)**
- **Branch management (branch, book_copy)**
- `product` table
- `customer_order` table
- `order_item` table
- `add_order_item` procedure
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`

#### Data flow
Library operations flow through the PostgreSQL schema, with views aggregating data for reporting and audit tables logging activities. E-commerce transactions are processed through MySQL tables and procedures, updating both order and product information. Warehouse stock levels are updated based on supplier feed data, synchronized nightly.

#### Dependencies
- Core tables: author, book, member, loan
- Supporting tables: branch, book_copy
- Views depend on loan, member, book, author tables
- Functions and procedures interact with loan, book, member tables
- Audit trigger depends on loan table
- Collaborates with `dbo.StockLevel` and `dbo.SupplierFeed` for data management
- Uses `dbo.SyncLog` for activity tracking

#### Cross-cutting
- **Shared base types**: No explicit shared types
- **Common error handling**: No specific error handling mentioned
- **Concurrency**: No explicit concurrency handling
- **Configuration**: No specific configuration details provided
- SQL schema for product, order, and item management
- Stock management logic
- Order status tracking
- Implements error handling and logging
- Uses T-SQL for database operations
- Follows a consistent schema for warehouse and supplier data
