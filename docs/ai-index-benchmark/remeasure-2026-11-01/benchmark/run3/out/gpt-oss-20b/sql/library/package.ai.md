### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:08:46Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL library system that stores catalog, member, and loan data, tracks book availability, and provides reporting and audit trails for check‑outs and returns.

#### Purpose
- Persist core library entities: authors, books, members, loans.
- Manage book copy inventory and branch locations.
- Provide reporting views and audit logging for loan activity.

#### Responsibilities
- **Data definition** – tables, indexes, constraints for authors, books, members, loans, branches, and book copies.
- **Business logic** – stored procedures and functions for checking out, returning, and late‑fee calculation.
- **Reporting** – views and materialized view for overdue loans, member statistics, and popular books.
- **Data migration** – schema evolution to introduce branches and per‑copy tracking.
- **Seeding** – initial data population for testing.

#### Key units
- `author`, `book`, `member`, `loan` tables – core catalog and loan tracking.
- `branch`, `book_copy` tables – branch hierarchy and per‑copy status.
- `loan_audit` table – audit trail of checkout/return actions.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – checkout logic.
- `sp_return_book(p_loan_id)` – return logic.
- `fn_late_fee(p_due_on, p_returned_on)` – late‑fee calculation.
- `trg_loan_audit` trigger – logs audit rows on loan changes.
- Views: `overdue_loans`, `member_loan_stats`, materialized view `popular_books`.

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `book_copy.status` to `ON_LOAN`, inserts a `loan` record, and logs `CHECKOUT` in `loan_audit`.
2. **Return** – `sp_return_book` updates `loan.returned_on`, sets `book_copy.status` back to `AVAILABLE`, and logs `RETURN` in `loan_audit`.
3. **Late fee** – `fn_late_fee` is invoked by application logic or reporting to compute fees based on `due_on` and `returned_on`.
4. **Reporting** – Views join `loan`, `member`, `book`, `author`, and `book_copy` to expose overdue loans, member activity, and popular books.

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- External: PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`), standard SQL types and constraints.
- Migration: `04_migration_v2.sql` assumes pre‑existing `author`, `book`, `member`, `loan` tables.

#### Cross-cutting
- Consistent use of foreign keys for referential integrity.
- Constraints enforce business rules (e.g., `copies_free <= copies_total`, `due_on > loaned_on`).
- Audit trail via `loan_audit` and trigger `trg_loan_audit`.
- Indexes on foreign keys and frequently queried columns (`idx_book_author`, `idx_loan_member`, `idx_popular_books_id`).
- Error handling in functions (e.g., member blocked, no free copies).
