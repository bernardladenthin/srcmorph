### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:37:33Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, members, branch inventory, book loans, returns, and borrowing reports.

#### Purpose
- Define and populate library catalog, member, inventory, and loan data.
- Support circulation operations, audit events, and lending reports.

#### Responsibilities
- **Core schema and migration:** Model authors, books, members, loans, branches, and individual book copies; migrate copy counts to branch-specific inventory.
- **Circulation and audit:** Check out and return books, update availability, calculate late fees, and record loan events.
- **Reporting:** Provide overdue-loan and member-activity views, plus a materialized view of popular books.
- **Sample data:** Insert deterministic authors, books, members, and loan histories within a transaction.

#### Key units
- `author`, `book`, `member`, `loan`: Core catalog, membership, and circulation tables.
- `branch`, `book_copy`: Branch locations and individually tracked copies introduced by the v2 migration.
- `loan_audit`: Records `CHECKOUT` and `RETURN` events.
- `fn_checkout_book`, `sp_return_book`: Perform checkout and return operations.
- `fn_late_fee`: Calculates capped late fees.
- `trg_loan_audit_fn`, `trg_loan_audit`: Record checkout and return changes on loans.
- `overdue_loans`, `member_loan_stats`, `popular_books`: Lending and usage reports.

#### Data flow
- Checkout validates member eligibility and copy availability, creates a loan, and reduces availability; the audit trigger records the checkout.
- Return marks a loan returned, restores availability, and triggers a return audit event; `fn_late_fee` calculates overdue charges.
- Reporting views derive overdue loans and member activity from loan records; `popular_books` aggregates recent borrowing.
- The v2 migration creates a main branch and branch-assigned copies from existing book counts, links loans and members to branch data, then removes book-level copy counters.

#### Dependencies
- Views and routines use `loan`, `member`, `book`, and, for popular-book reporting, `author`.
- `book_copy` references `book` and `branch`; migrated `loan` references `book_copy`; `member` references its home `branch`.
- The migration uses PostgreSQL `generate_series`; seed data targets `author`, `book`, `member`, and `loan`.

#### Cross-cutting
- Primary keys, unique constraints, foreign keys, checks, and indexes enforce identity, relationships, and data rules.
- Checkout and return routines reject blocked members, unavailable inventory, unknown loans, or already-returned loans.
- Seed inserts run in one transaction; `popular_books` is materialized and has a unique index on `book_id`.
