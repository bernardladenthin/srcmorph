### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T05:20:38Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL library management system modeling authors, books, members, loans, branches, and audit trails.

#### Purpose
- Provide a complete data model and supporting logic for a lending library, including:
  - Core entities (authors, books, members, loans)
  - Reporting views (overdue loans, member statistics, popular books)
  - Transactional routines (checkout, return, late fee calculation)
  - Migration to a branch‑centric model with per‑copy tracking
  - Seed data for testing and demos

#### Responsibilities
- **Schema definition** – tables for authors, books, members, loans, branches, book copies, and audit logs.
- **Reporting** – views for overdue loans, member loan statistics, and popular books.
- **Transaction handling** – functions for checking out and returning books, calculating late fees, and maintaining an audit trail.
- **Migration** – script to introduce branches, move copies to a dedicated table, rename columns, and adjust loan tracking.
- **Seed data** – realistic sample data for authors, books, members, and loans.

#### Key units
- **Tables**
  - `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`
- **Views**
  - `overdue_loans`, `member_loan_stats`, `popular_books`
- **Functions**
  - `fn_checkout_book`, `sp_return_book`, `fn_late_fee`
- **Trigger**
  - `trg_loan_audit_fn`
- **Migration script**
  - `04_migration_v2.sql` (adds branches, copies, renames columns)
- **Seed script**
  - `05_seed_data.sql` (populates core tables)

#### Data flow
1. **Checkout** – `fn_checkout_book` validates member status, decrements `book.copies_free`, creates a `loan` record, and logs the action in `loan_audit`.
2. **Return** – `sp_return_book` marks the loan as returned, increments `book.copies_free`, and logs the return in `loan_audit`.
3. **Late fee** – `fn_late_fee` calculates overdue days and applies a per‑day penalty (capped).
4. **Reporting** – Views aggregate data from `loan`, `member`, `book`, and `author` to show overdue loans, member activity, and book popularity.
5. **Migration** – Moves existing copies to `book_copy`, assigns each copy to a branch, updates loans to reference a specific copy, renames `joined_on` to `member_since`, adds `home_branch_id` to members, and removes legacy copy counters.

#### Dependencies
- **Internal** – Tables reference each other via foreign keys (e.g., `book.author_id`, `loan.book_id`, `loan.member_id`, `book_copy.book_id`, `book_copy.branch_id`).
- **External** – Relies on PostgreSQL features: `SERIAL`/`BIGSERIAL` for IDs, `TIMESTAMPTZ`, `CHECK` constraints, `DEFAULT` values, `ON DELETE CASCADE`, and trigger-based audit logging.
- **Seed data** – Assumes the schema tables exist before running `05_seed_data.sql`.

#### Cross-cutting
- **Shared types** – `author_id`, `book_id`, `member_id`, `loan_id` are all `BIGSERIAL` primary keys.
- **Common checks** – Many tables enforce `NOT NULL`, `UNIQUE`, and range checks (e.g., `birth_year`, `copies_total`, `copies_free`).
- **Audit trail** – `loan_audit` captures every checkout and return with timestamps.
- **Branch dimension** – Introduced in migration v2; all copy‑related operations now reference a `branch`.
- **Configuration** – Late fee calculation uses fixed constants (50 cents per day, 2000 cents cap) embedded in `fn_late_fee`.
