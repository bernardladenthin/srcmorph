### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:55:28Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports checkout/return operations, and provides reporting views for overdue loans and popular books.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce lending rules and audit changes.
- Offer read‑only reporting on loan status and book popularity.

#### Responsibilities
- **Data Modeling** – tables for authors, books, members, loans, and audit logs; indexes for performance.
- **Business Logic** – checkout, return, late‑fee calculation, and audit triggers.
- **Reporting** – views for overdue loans, member loan statistics, and popular books.
- **Schema Evolution** – migration to version 2 adding branches and per‑copy tracking.

#### Key units
- `author`, `book`, `member`, `loan` tables – core entities.
- `loan_audit` table – audit trail for checkout/return actions.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` – core routines.
- `trg_loan_audit` trigger – logs audit rows on loan changes.
- `overdue_loans`, `member_loan_stats`, `popular_books` views – reporting layers.
- `branch`, `book_copy` tables – added in migration v2 for branch and copy granularity.

#### Data flow
1. **Checkout** (`fn_checkout_book`)  
   - Validates member and book availability.  
   - Decrements `copies_free` (or reserves a `book_copy`).  
   - Inserts a new `loan` row and triggers `loan_audit` with `CHECKOUT`.

2. **Return** (`sp_return_book`)  
   - Updates `loan.returned_on`.  
   - Increments `copies_free` (or marks `book_copy` AVAILABLE).  
   - Trigger logs `RETURN` in `loan_audit`.

3. **Late fee** (`fn_late_fee`)  
   - Computes overdue days and caps fee; used by application logic.

4. **Reporting**  
   - Views join `loan`, `member`, `book`, and `author` to expose overdue counts, member loan summaries, and most‑loaned books.

#### Dependencies
- Internal: `member`, `book`, `loan`, `author`, `loan_audit`, `branch`, `book_copy`.  
- External: PostgreSQL built‑in functions (`now()`, `CURRENT_DATE`), PL/pgSQL, SQL.  
- Migration script relies on existing `book`, `loan`, `member` tables before adding `branch` and `book_copy`.

#### Cross-cutting
- **Constraints & Checks** – enforce data integrity (e.g., `copies_free <= copies_total`, `due_on > loaned_on`, status enums).  
- **Indexes** – `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.  
- **Audit Trail** – `loan_audit` and trigger provide immutable change history for loans.  
- **Branch & Copy Model** – migration replaces per‑book copy counters with a dedicated `book_copy` table, enabling per‑branch inventory tracking.
