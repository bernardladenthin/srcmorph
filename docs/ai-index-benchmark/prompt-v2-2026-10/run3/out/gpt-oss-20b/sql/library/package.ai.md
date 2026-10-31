### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:12:44Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, tracks book availability, and provides reporting and audit trails for lending operations.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce borrowing rules and track book availability.
- Offer reporting views and audit logging for compliance.

#### Responsibilities
- **Data Modeling** – Tables for authors, books, members, loans, and later branches and book copies.
- **Business Logic** – Stored procedures/functions for checkout, return, and late‑fee calculation.
- **Audit Trail** – Trigger‑based logging of loan actions.
- **Reporting** – Views for overdue loans, member statistics, and popular books.
- **Migration & Seeding** – Scripts to evolve schema (branches, copies) and populate test data.

#### Key units
- `author`, `book`, `member`, `loan` tables – core entities.
- `branch`, `book_copy` tables – added in migration v2 for multi‑branch support.
- `loan_audit` table – audit log of checkout/return events.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – checkout logic.
- `sp_return_book(p_loan_id)` – return logic.
- `fn_late_fee(p_due_on, p_returned_on)` – late‑fee calculation.
- `trg_loan_audit` trigger – logs loan inserts/updates.
- Views: `overdue_loans`, `member_loan_stats`, materialized `popular_books`.

#### Data flow
1. **Checkout**: `fn_checkout_book` validates member, decrements `book.copies_free`, inserts a `loan`, and the trigger logs the action.
2. **Return**: `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and the trigger logs the return.
3. **Reporting**: Views join `loan`, `member`, `book`, and `author` to surface overdue status, member activity, and popularity metrics.
4. **Migration**: `04_migration_v2.sql` replaces per‑book copy counters with `book_copy` rows linked to `branch`, updating foreign keys in `loan` and `member`.

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `book_copy`, `branch`, `loan_audit`.
- External: PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, `INTERVAL`), standard SQL types.
- Indexes: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.

#### Cross-cutting
- **Constraints & Checks**: Enforce data integrity (e.g., `copies_free <= copies_total`, `due_on > loaned_on`, status enum for copies).
- **Triggers**: Central audit mechanism via `trg_loan_audit`.
- **Error handling**: Checkout rejects blocked members or unavailable copies; late‑fee capped at 2000.
- **Configuration**: Default branch (`branch_id = 1`) and home branch for members set during migration.
