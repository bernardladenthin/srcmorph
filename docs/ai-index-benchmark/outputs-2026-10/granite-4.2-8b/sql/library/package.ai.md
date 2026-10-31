### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T11:17:02Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A lending library database system that manages books, authors, members, loans, branches, and audit trails with constraints on copy availability and loan periods.

#### Purpose
- Defines the core data model for books, authors, members, and loans.
- Provides reporting views for overdue loans, member statistics, and popular books.
- Implements checkout/return logic, late fee calculation, and audit logging.
- Migrates from per-book copy counters to branch-based copy tracking.
- Supplies seed data for testing and demonstration.

#### Responsibilities
- Schema design and constraint enforcement (`01_schema.sql`).
- Reporting and analytics via views and a materialized view (`02_views.sql`).
- Business logic for loan operations, copy tracking, and audit (`03_routines.sql`).
- Database migration to introduce branches and remove per-book copy columns (`04_migration_v2.sql`).
- Initial data population for demo use (`05_seed_data.sql`).

#### Key units
- `author` table – stores author identifiers, full name, birth year, country.
- `book` table – stores book identifiers, ISBN, title, author reference, total and free copy counts (removed in migration).
- `member` table – stores member identifiers, email, full name, join date (renamed to `member_since`), home branch reference.
- `loan` table – records loan transactions with book, member, loan date, due date, return date; gains `copy_id` after migration.
- `branch` table – introduced in migration; holds branch ID, name, city.
- `book_copy` table – introduced in migration; links copies to books and branches via barcode and status.
- `loan_audit` table – logs CHECKOUT/RETURN actions with timestamps.
- `overdue_loans` view – shows loans past due without return.
- `member_loan_stats` view – provides per‑member loan totals and open loan count.
- `popular_books` materialized view – lists books loaned frequently (≥5 times) in the last 365 days.
- `idx_popular_books_id` unique index – ensures single row per book in `popular_books`.
- `fn_checkout_book` function – checks member block status, decrements free copies, inserts loan row.
- `sp_return_book` procedure – updates loan return date and increments free copy count.
- `fn_late_fee` function – calculates late fee in cents as `LEAST(GREATEST(days_overdue,0)*50,2000)`.
- `trg_loan_audit_fn` function – triggers audit insert on loan INSERT or UPDATE when `returned_on` becomes non‑null.
- `trg_loan_audit` trigger – fires AFTER INSERT OR UPDATE OF `returned_on` on `loan` and calls `trg_loan_audit_fn`.

#### Data flow
- Seed data (`05_seed_data.sql`) inserts authors, books, members, and loans to initialize the database.
- Routine functions (`03_routines.sql`) are called during checkout (`fn_checkout_book`) and return (`sp_return_book`) to adjust `copies_free` and log audits.
- Views (`02_views.sql`) query existing tables (`loan`, `member`, `book`, `author`) to produce overdue, stats, and popular book reports.
- Migration (`04_migration_v2.sql`) transforms the schema: drops `copies_free`/`copies_total` from `book`, adds `branch`, `book_copy`, and `copy_id` to `loan`; updates member column names and adds `home_branch_id`.
- After migration, `book` no longer tracks copy counts; availability is tracked via `book_copy` rows with status (`AVAILABLE`, `ON_LOAN`, `LOST`).

#### Dependencies
- Internal: `member`, `book`, `loan`, `author` tables are referenced across all files.
- `loan_audit` table depends on `loan` via `loan_id`.
- `branch` and `book_copy` tables depend on `book` and `branch` respectively (migration only).
- `popular_books` view depends on `loan`, `book`, `author`.
- `trg_loan_audit_fn` depends on `loan` row state (`returned_on` becoming non‑null).
- External: PostgreSQL built‑ins (`CURRENT_DATE`, `now()`, `generate_series`, interval arithmetic `'365 days'`).

#### Cross-cutting
- Constraints enforce data consistency: `book_free_le_total`, `loan_due_after_loan`, unique ISBN, email uniqueness, CHECK on `status`, `birth_year` range.
- Indexes (`idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`, `idx_book_copy_book`) support read performance for foreign‑key lookups and filtering.
- Audit pattern: `trg_loan_audit_fn` inserts `CHECKOUT` on loan INSERT and `RETURN` on UPDATE when `returned_on` changes from NULL to a date.
- Copy tracking evolved from per‑book `copies_free`/`copies_total` (schema) to branch‑based `book_copy` rows with `status` (migration).
- Time calculations use `CURRENT_DATE` and `INTERVAL '365 days'` for popular‑book filtering and late‑fee computation (`fn_late_fee`).
- All procedural logic (`fn_checkout_book`, `sp_return_book`, `trg_loan_audit_fn`) operates within `PL/pgSQL` and interacts only with `book`, `member`, `loan`, and `loan_audit`.
- Seed data is self‑contained; no queries or dependencies on runtime logic.
