### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:27:29Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL library system that stores catalog, members, and loan data, and provides checkout, return, and reporting functionality.

#### Purpose
- Persist library catalog, member, and loan information.
- Enable book lending, returns, and late‑fee calculation.
- Offer read‑only reports on overdue items, member activity, and popular titles.

#### Responsibilities
- **Data modeling** – core tables (`author`, `book`, `member`, `loan`) and later branch‑aware tables (`branch`, `book_copy`).
- **Business logic** – checkout, return, and late‑fee routines (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`).
- **Audit trail** – `loan_audit` table and trigger `trg_loan_audit`.
- **Reporting** – views (`overdue_loans`, `member_loan_stats`) and materialized view (`popular_books`).
- **Seeding** – initial data population for testing.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- **Views**: `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Functions**: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- **Procedure**: `sp_return_book`.
- **Trigger**: `trg_loan_audit` on `loan`.

#### Data flow
1. **Checkout** – `fn_checkout_book` validates member and book availability, decrements `book.copies_free`, inserts a `loan` record, and logs `CHECKOUT` in `loan_audit`.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and logs `RETURN` via trigger.
3. **Late fee** – `fn_late_fee` calculates overdue days and caps the fee.
4. **Reporting** – Views aggregate `loan`, `member`, `book`, and `author` data to expose overdue counts, member statistics, and most‑loaned books.

#### Dependencies
- **Internal** – foreign keys between `loan`, `book`, `member`, `author`, `book_copy`, and `branch`.
- **External** – PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`) and standard SQL types.
- **Migration** – `04_migration_v2.sql` adds branch and copy tables, altering `loan` and `member` to reference them.

#### Cross‑cutting
- Consistent use of `BIGSERIAL` primary keys and `CHECK` constraints for data integrity.
- Centralized audit via `loan_audit` and trigger `trg_loan_audit`.
- Indexes (`idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`) support query performance across all views and routines.
