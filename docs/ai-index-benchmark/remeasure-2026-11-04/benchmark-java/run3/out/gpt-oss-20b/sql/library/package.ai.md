### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:49:56Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL library management system that stores catalog, member, and loan data, enforces borrowing rules, tracks copy availability, and provides reporting views and audit trails.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce borrowing constraints and track copy status.
- Offer read‑only reporting and audit of loan actions.

#### Responsibilities
- **Data Modeling** – tables for authors, books, members, loans, branches, and book copies.
- **Business Logic** – checkout, return, late fee calculation, and copy availability updates.
- **Reporting** – overdue loans, member loan statistics, and popular books views.
- **Audit Trail** – trigger‑based logging of checkout and return events.
- **Schema Evolution** – migration adding branches and explicit book copies.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- **Views**: `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Functions**: `fn_checkout_book`, `fn_late_fee`.
- **Procedure**: `sp_return_book`.
- **Trigger**: `trg_loan_audit` with function `trg_loan_audit_fn`.

#### Data flow
1. **Checkout** (`fn_checkout_book`)  
   - Validate member not blocked.  
   - Decrement `book_copy.status` to `ON_LOAN`.  
   - Insert `loan` record with `due_on`.  
   - Log `CHECKOUT` in `loan_audit`.

2. **Return** (`sp_return_book`)  
   - Update `loan.returned_on`.  
   - Increment `book_copy.status` to `AVAILABLE`.  
   - Log `RETURN` in `loan_audit`.

3. **Late fee** (`fn_late_fee`)  
   - Compute days overdue from `due_on` and `returned_on`.  
   - Multiply by 50, cap at 2000.

4. **Reporting**  
   - Views join `loan`, `member`, `book`, `author`, `book_copy` to produce overdue lists, member stats, and popularity counts.

#### Dependencies
- **Internal**: `author`, `book`, `member`, `loan`, `book_copy`, `branch`, `loan_audit`.  
- **External**: PostgreSQL built‑ins (`CURRENT_DATE`, `now()`, interval arithmetic).  
- **DDL**: Migration adds `branch` and `book_copy`, removes per‑book copy counters.

#### Cross-cutting
- **Foreign keys** enforce referential integrity across all tables.  
- **Indexes**: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.  
- **Constraints**: `book_free_le_total`, status checks on `book_copy`, `loan_due_after_loan`.  
- **Audit**: `loan_audit` records every checkout/return with timestamp.  
- **Seed data** populates initial authors, books, members, and loans for development.
