### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:42:24Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL library management system that stores catalog, member, and loan data, enforces lending rules, and provides reporting and audit trails.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce lending business rules (availability, due dates, late fees).
- Supply read‑only reporting views and audit logs.

#### Responsibilities
- **Data Modeling** – core tables (`author`, `book`, `member`, `loan`) and constraints.
- **Reporting** – views (`overdue_loans`, `member_loan_stats`) and materialized view (`popular_books`).
- **Lending Logic** – checkout, return, late fee calculation, and audit logging.
- **Branch & Copy Management** – migration to per‑branch book copies (`branch`, `book_copy`) and updated loan/member references.
- **Seeding** – initial data population for testing.

#### Key units
- **Tables**  
  - `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **Views**  
  - `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Functions**  
  - `fn_checkout_book(member_id, book_id, days) → loan_id`  
  - `fn_late_fee(due_on, returned_on) → fee`  
  - `trg_loan_audit_fn()` (trigger function).
- **Procedure**  
  - `sp_return_book(loan_id)` – handles book return.
- **Triggers**  
  - `trg_loan_audit` on `loan` (audit INSERT/UPDATE).

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `book.copies_free`, inserts a `loan` row, and logs `CHECKOUT` in `loan_audit`.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and logs `RETURN` via trigger.
3. **Late fee** – `fn_late_fee` calculates fee based on `due_on` and `returned_on` (or current date).
4. **Reporting** – Views aggregate `loan`, `member`, `book`, and `author` data for overdue loans, member statistics, and popular books.
5. **Branch migration** – `04_migration_v2.sql` creates `branch` and `book_copy`, adds `copy_id` to `loan`, and sets `home_branch_id` for members.

#### Dependencies
- **Internal** – `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **External** – PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, interval arithmetic).
- **Indexes** – `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.

#### Cross-cutting
- **Constraints & Checks** – enforce data integrity (e.g., `copies_free <= copies_total`, `due_on > loaned_on`, status enums).
- **Audit Trail** – `loan_audit` records every checkout and return with timestamps.
- **Branch‑aware Lending** – after migration, loans reference specific `book_copy` records tied to branches.
- **Materialized View Refresh** – `popular_books` requires periodic refresh to stay current.
