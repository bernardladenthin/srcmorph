### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T22:54:25Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, enforces lending rules, tracks late fees, and provides reporting views.

#### Purpose
- Persist library catalog, members, and loan transactions.
- Enforce checkout/return business rules and audit trail.
- Offer reporting on overdue loans, member activity, and popular books.

#### Responsibilities
- **Data Model** – tables for authors, books, members, loans, and audit logs.
- **Business Logic** – functions and procedures for checkout, return, late‑fee calculation, and audit triggers.
- **Reporting** – read‑only views for overdue loans, member statistics, and popular books.
- **Migration & Extension** – script to add branches, per‑copy tracking, and adjust schema.

#### Key units
- `author`, `book`, `member`, `loan` tables – core entities.
- `loan_audit` table – audit trail for checkout/return actions.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – validates member, decrements copies, inserts loan.
- `sp_return_book(p_loan_id)` – updates loan, increments copies.
- `fn_late_fee(p_due_on, p_returned_on)` – computes overdue fee (50 per day, capped 2000).
- `trg_loan_audit_fn()` – trigger function logging `CHECKOUT` and `RETURN`.
- `overdue_loans`, `member_loan_stats`, `popular_books` views – reporting layers.
- `branch`, `book_copy` tables – added by migration for branch and copy management.

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, book availability, updates `book.copies_free`, inserts `loan`, and logs audit.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and logs audit.
3. **Late fee** – `fn_late_fee` is called by application logic or reporting to calculate fees.
4. **Reporting** – Views join `loan`, `member`, `book`, `author` to produce overdue lists, member stats, and popularity metrics.

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- External: PostgreSQL built‑in functions (`now()`, `CURRENT_DATE`), standard SQL constraints and indexes.
- Migration script relies on existing `book`, `member`, `loan` tables before adding `branch` and `book_copy`.

#### Cross-cutting
- **Constraints & Indexes** – enforce data integrity (e.g., `copies_free <= copies_total`, `due_on > loaned_on`) and performance (`idx_book_author`, `idx_loan_member`, `idx_loan_open`).
- **Audit Trail** – centralized via `loan_audit` and trigger `trg_loan_audit`.
- **Error Handling** – functions raise errors on blocked members or no available copies.
- **Configuration** – default values for dates, copies, and status (`AVAILABLE`).
- **Migration** – preserves existing data while restructuring for branch and copy granularity.
