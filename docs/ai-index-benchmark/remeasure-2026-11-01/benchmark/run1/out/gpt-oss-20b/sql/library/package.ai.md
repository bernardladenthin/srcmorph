### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:29:39Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, enforces lending rules, tracks overdue items, and supports branch‑level inventory.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce lending rules (copy availability, due dates, late fees).
- Provide reporting on overdue loans, member activity, and book popularity.

#### Responsibilities
- **Data Modeling** – tables for authors, books, members, loans, and later branches and book copies.
- **Business Logic** – checkout, return, late‑fee calculation, and audit trail via functions, procedures, and triggers.
- **Reporting** – read‑only views and a materialized view for overdue loans, member statistics, and popular books.
- **Migration** – schema evolution to add branches and book copies, updating foreign‑key relationships.

#### Key units
- `author`, `book`, `member`, `loan` tables – core catalog and transaction storage.
- `branch`, `book_copy` tables – branch hierarchy and individual copy tracking (migration v2).
- `loan_audit` table – audit trail of checkout/return actions.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` – core loan procedures.
- `trg_loan_audit` trigger – logs audit records on loan changes.
- Views: `overdue_loans`, `member_loan_stats`; materialized view: `popular_books`.

#### Data flow
1. **Checkout** (`fn_checkout_book`)  
   - Verify member not blocked.  
   - Decrement `book.copies_free` (or `book_copy.status`).  
   - Insert new `loan` record with due date.  
   - Trigger logs `CHECKOUT` in `loan_audit`.

2. **Return** (`sp_return_book`)  
   - Update `loan.returned_on`.  
   - Increment `book.copies_free` (or set `book_copy.status` to `AVAILABLE`).  
   - Trigger logs `RETURN` in `loan_audit`.

3. **Late fee** (`fn_late_fee`)  
   - Compute days overdue; multiply by 50, capped at 2000.

4. **Reporting**  
   - Views aggregate loan data to expose overdue counts, member loan totals, and book loan frequencies.

#### Dependencies
- **Internal** – `author`, `book`, `member`, `loan`, `loan_audit`, `book_copy`, `branch`.  
- **External** – PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`).  
- **Indexes** – `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.

#### Cross-cutting
- Consistent use of foreign keys to enforce referential integrity.  
- Business rules enforced via constraints (`copies_free <= copies_total`, `due_on > loaned_on`).  
- Audit trail via trigger on `loan`.  
- Late‑fee logic centralized in `fn_late_fee`.  
- Migration script preserves existing data while adding branch and copy granularity.
