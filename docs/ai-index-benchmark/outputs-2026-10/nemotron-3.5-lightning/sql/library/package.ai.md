### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T23:12:36Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A lending library system managing book inventory, member checkouts, branch distribution, and audit trails.

#### Purpose
- Provides complete data infrastructure for a lending library lifecycle: core schema, operational routines, reporting views, branch-aware copy migration, and seed data population.

#### Responsibilities
- Core schema design: authors, books, members, loans with constraints, indexes, and relational integrity.
- Reporting views: overdue_loans, member_loan_stats, popular_books for analytics and statistics.
- Operational routines: fn_checkout_book, sp_return_book, fn_late_fee, trg_loan_audit_fn for checkout/return logic and audit logging.
- Branch migration: 04_migration_v2 redistributes copies to branches, renames columns, and adds home_branch_id association.
- Seed data population: 05_seed_data inserts reference authors, books, members, and loans for testing and demonstration.

#### Key units
- author table: stores author identity, full name, birth year, and country code with CHECK constraints.
- book table: manages ISBN, title, author linkage, published year, and copy totals with integrity constraints.
- member table: tracks member email, full name, join date, block status, and home_branch_id association.
- loan table: records checkout sequences, due dates, return status, and cascade deletion references.
- overdue_loans view: joins loan, member, book to surface active overdue loans with days_overdue computation.
- member_loan_stats view: aggregates per-member loan totals, open count, and last loan date.
- popular_books view: groups by book_id, title, author_name with unique index and times_loaned count.
- fn_checkout_book function: validates member.is_blocked, updates book.copies_free, inserts loan, returns loan_id.
- sp_return_book procedure: sets loan.returned_on, increments book.copies_free, retrieves book_id.
- fn_late_fee function: computes penalty as least(greatest(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000).
- trg_loan_audit_fn trigger: inserts into loan_audit on CHECKOUT (INSERT) and RETURN (UPDATE of returned_on).
- branch table: SMALLSERIAL primary key, name, city with UNIQUE constraint.
- book_copy table: BIGSERIAL copy_id, book_id FK, branch_id FK, barcode UNIQUE, status check constraint.
- loan_audit table: audit_id PK, loan_id, action CHECK ('CHECKOUT','RETURN'), changed_at timestamp.

#### Data flow
- Checkout: fn_checkout_book validates member eligibility, decrements copies_free, inserts loan, trg_loan_audit logs CHECKOUT.
- Return: sp_return_book sets loan.returned_on, increments book.copies_free, triggers audit log RETURN.
- Late fee: fn_late_fee calculates penalty from due_on to return date, capped at 2000 currency units.
- Migration: 04_migration_v2 redistributes book copies via generate_series, adds copy_id to loan, associates members to home_branch_id, drops global copy counters.
- Reporting: views aggregate loan, member, book, author data to surface overdue status, member statistics, and book popularity.

#### Dependencies
- Internal collaborations: author.book_id FK, book.loan_id FK, loan.member_id FK, book_copy.book_id FK, book_copy.branch_id FK, loan.copy_id FK to book_copy, member.home_branch_id FK to branch.
- External: PostgreSQL generate_series function, sequences branch_branch_id_seq and book_copy_copy_id_seq, system catalog tables referenced implicitly.
- Cross-file: 03_routines depends on member, book, loan, loan_audit; 04_migration_v2 depends on book, branch, generate_series; 02_views depends on loan, member, book, author; 05_seed_data depends on author, book, member, loan.

#### Cross-cutting
- CHECK constraints recurring: birth_year range, copies_free <= copies_total, due_on > loaned_on, status enum values.
- FK constraint pattern across all core tables and book_copy, loan, member references.
- Trigger/audit pattern: trg_loan_audit_fn logs CHECKOUT on INSERT and RETURN on UPDATE of returned_on.
- Function pattern: fn_ and sp_ prefixed routines for business logic with COALESCE and CURRENT_DATE defaults.
- View pattern: reporting views with GROUP BY, unique indexes, and computed columns (days_overdue, times_loaned).
- Migration pattern: DDL changes, column renames (joined_on -> member_since), data migrations using generate_series CROSS JOIN.
- Status enum usage: book copy statuses ('AVAILABLE','ON_LOAN','LOST'), loan actions ('CHECKOUT','RETURN').
