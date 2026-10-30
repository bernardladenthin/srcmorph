### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:48:51Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> This package manages a PostgreSQL lending-library system, covering catalog data, borrowing flows, overdue reporting, and migration from book-level inventory to branch-aware copy tracking.

#### Purpose
- Maintain the schema and data model for authors, books, members, and loans in a lending library.
- Support operational workflows for checkout, return, overdue detection, and reporting across the library lifecycle.

#### Responsibilities
- Core data model and constraints for `author`, `book`, `member`, and `loan`.
- Reporting views for loan delinquency and borrowing trends: `overdue_loans`, `member_loan_stats`, and `popular_books`.
- Business routines for checkout, return, late-fee calculation, and audit logging via `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, and `trg_loan_audit`.
- Schema migration from book-copy counters to branch-based inventory with `branch` and `book_copy`.
- Seed data for realistic catalog and circulation workloads.

#### Key units
- `author` — library author record with `author_id`, `full_name`, `birth_year`, and `country_code`.
- `book` — catalog edition with `isbn`, `title`, `author_id`, `published_year`, and copy inventory.
- `member` — borrower account with `email`, `full_name`, `member_since`, and `is_blocked`.
- `loan` — borrowing transaction with `book_id`, `member_id`, `loaned_on`, `due_on`, and `returned_on`.
- `overdue_loans` — view listing open loans past due with member email, book title, and `days_overdue`.
- `member_loan_stats` — view aggregating per-member totals, open loans, and `last_loan_on`.
- `popular_books` — materialized view ranking books borrowed in the last 365 days by `times_loaned`.
- `loan_audit` — audit log table for `CHECKOUT` and `RETURN` lifecycle actions.
- `fn_checkout_book` — PostgreSQL function validating member status and available `book.copies_free` before creating a `loan`.
- `sp_return_book` — procedure marking a loan returned and restoring available copies.
- `fn_late_fee` — function computing overdue charges in cents with zero-floor and cap logic.
- `trg_loan_audit` / `trg_loan_audit_fn` — trigger-based audit writes on `loan` insert/update.
- `branch` — library location for branch-based inventory.
- `book_copy` — specific copy record with `copy_id`, `book_id`, `branch_id`, `barcode`, and `status`.
- `member.home_branch_id` — member home library assignment added by migration.
- `loan.copy_id` — branch copy linkage introduced in the migration.

#### Data flow
- Schema setup defines the base domain model and relationships: `book.author_id -> author.author_id`, `loan.book_id -> book.book_id`, and `loan.member_id -> member.member_id`.
- `loan` records are created by checkout routines; `book.copies_free` decreases when a copy is checked out and increases on return.
- Reporting reads from `loan`, `member`, `book`, and `author` to derive operational and trend data through `overdue_loans`, `member_loan_stats`, and `popular_books`.
- `trg_loan_audit` records `CHECKOUT` and `RETURN` actions in `loan_audit` after `loan` insert or update.
- The migration re-models inventory from per-book counters to `branch` and `book_copy`, then updates existing loans to a `copy_id` reference and sets `member.home_branch_id`.
- Seed data populates authors, books, members, and loans for initial operational testing and reporting.

#### Dependencies
- Internal:
  - `author`, `book`, `member`, `loan`
  - `branch`, `book_copy`, `loan_audit`
  - `overdue_loans`, `member_loan_stats`, `popular_books`
  - `book_free_le_total` constraint removed in migration
- External / platform:
  - PostgreSQL schema DDL and PL/pgSQL routines
  - `BIGSERIAL`, `SMALLSERIAL`, `CURRENT_DATE`, `now()`, `generate_series`
  - `COUNT`, `MAX`, `FILTER`, `COALESCE`, `INTERVAL`
  - Trigger context variables `TG_OP`, `NEW`, and `OLD`
- Migration-specific:
  - `member.joined_on` renamed to `member_since`
  - `member.home_branch_id` set default to `1` where null
  - `book.copies_total` and `book.copies_free` dropped after copy migration

#### Cross-cutting
- Shared pattern: all core entities use PostgreSQL identity columns (`BIGSERIAL`, `SMALLSERIAL`) and foreign-key-driven relationships.
- Inventory correctness: early schema enforces `book.copies_free <= book.copies_total`; later migration replaces this with per-copy `book_copy.status` and explicit branch inventory.
- State tracking: `loan.returned_on`, `loan.due_on`, and `loan.loaned_on` drive overdue checks, late fees, and open-loan reporting.
- Auditability: `loan_audit` captures `CHECKOUT` and `RETURN` events via a trigger on `loan`.
- Constraints and validation: member blocking prevents checkout, loan return requires active unpaid loan, and due-date logic clamps fees with a cap.
- Reporting consistency: views aggregate from the same relational graph used by business workflows, with materialized `popular_books` keyed by `book_id`.
- Data seeding: initial dataset includes 15 authors, 110 books, 50 members, and 150 loans to exercise catalog and circulation behavior.
