### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:16:09Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Manages core business operations across a lending library, e-commerce order processing, and warehouse inventory synchronization, ensuring data consistency, real-time validation, and auditability through domain-specific workflows and schema-driven logic.

#### Purpose  
- Models and manages library operations including book availability, member loans, and reporting.  
- Enables real-time product addition to orders with stock validation and automatic total updates.  
- Synchronizes supplier stock feeds with central warehouse inventory using transactional merge operations.

#### Responsibilities  
- Library: Book and member lifecycle, loan management, availability enforcement, and reporting.  
- Shop: Order item addition with stock checks, reservations, and real-time total updates.  
- Warehouse: Supplier data ingestion, stock synchronization, and audit logging.

#### Key units  
- `author`: Tracks author metadata including name, birth year, and country.  
- `book`: Core book data including title, ISBN, author, and copy counts.  
- `member`: Member profile with email, name, join date, and blocking status.  
- `loan`: Tracks borrowing events with loaned, due, and returned dates.  
- `loan_audit`: Logs checkout and return actions for compliance.  
- `branch`: Models physical library branches with location and name.  
- `book_copy`: Branch-specific book copies with barcode and status (AVAILABLE, ON_LOAN, LOST).  
- `overdue_loans`, `member_loan_stats`, `popular_books`: Reporting views.  
- `product`: Stores product details including price and stock levels.  
- `order_item`: Tracks items in an order with quantity and unit price.  
- `customer_order`: Holds the total order value in cents.  
- `dbo.StockLevel`: Central table for warehouse stock levels by warehouse and SKU.  
- `dbo.SupplierFeed`: Source table for supplier-provided stock data.  
- `dbo.SyncLog`: Audit table recording sync metrics and errors.  

#### Data flow  
- Library: Seed data → validate availability → checkout reduces `copies_free` → return increases it → audit logs all actions → reports derive from views.  
- Shop: Order ID + product SKU/quantity → validate stock → lock row → check availability → insert order item → deduct stock → update order total.  
- Warehouse: Supplier feed → filter by `@Since` → group by (WarehouseId, Sku) → MERGE into `StockLevel` → log insert/update counts in `SyncLog` within transaction.

#### Dependencies  
- Library: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`, `generate_series`, `current_date`, `now()`  
- Shop: `product`, `order_item`, `customer_order`  
- Warehouse: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `SYSUTCDATETIME()`  

#### Cross-cutting  
- Business rules enforced via constraints (e.g., `copies_free <= copies_total`, due date logic).  
- Real-time validation and data locking (e.g., `FOR UPDATE` in shop).  
- Monetary values stored in cents to avoid floating-point errors.  
- Audit logging via triggers, procedures, and dedicated log tables.  
- Transactional integrity maintained across all operations (library seed scripts, warehouse syncs).  
- Schema evolution handled via migration scripts (library) and incremental syncs (warehouse).  
- Shared data consistency through primary keys and composite constraints (e.g., (WarehouseId, Sku)).
