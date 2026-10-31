### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:57:49Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL lending library system that manages authors, books, members, and loans, tracks physical copies per branch, enforces checkout and return rules, calculates late fees, and reports overdue and popular-book analytics.

#### Purpose
- Defines, migrates, and seeds a relational lending library schema in PostgreSQL.
- Implements checkout/return procedures, late-fee calculation, audit logging, and reporting views for overdue loans, member statistics, and popular books.

#### Responsibilities
- Core DDL: author, book, member, and loan tables with constraints and indexes.
- Reporting: overdue_loans view, member_loan_stats view, popular_books materialized view with unique index.
- Lending routines: fn_checkout_book, sp_return_book, fn_late_fee, and trg_loan_audit_fn/trg_loan_audit trigger on loan_audit.
- Physical copy migration: branch and book_copy tables, loan.copy_id and member.home_branch_id, removal of book aggregate copy counters.
- Seed data: 15 authors, 110 books, 50 members, 150 loans in a transaction.

#### Key units
- author — table storing author_id, full_name, birth_year, country_code.
- book — table storing ISBN, title, author_id, published_year, and copy counters (retired in v2).
- member — table storing email, joined_on/member_since, is_blocked, and home_branch_id (v2).
- loan — table storing loan dates, due_on, returned_on, and copy_id (v2), with partial open-loan index.
- loan_audit — table logging CHECKOUT and RETURN actions with timestamps.
- branch — table storing branch_id, name, city.
- book_copy — table storing physical copies with barcode and status (AVAILABLE, ON_LOAN, LOST).
- overdue_loans — view of active loans past due with computed days_overdue.
- member_loan_stats — view aggregating total, open, and last loan dates per member.
- popular_books — materialized view of books loaned at least five times in 365 days, unique index idx_popular_books_id.
- fn_checkout_book — PL/pgSQL function that lends a copy to a non-blocked member and returns loan_id.
- sp_return_book — PL/pgSQL procedure marking a loan returned and restoring the copy.
- fn_late_fee — immutable SQL function computing overdue cents capped at 2000.
- trg_loan_audit_fn / trg_loan_audit — trigger function and trigger auditing loan inserts and returns.

#### Data flow
- Seed data inserts into author, book, member, and loan.
- fn_checkout_book validates member.is_blocked, decrements availability, inserts into loan, and the trigger writes a CHECKOUT row to loan_audit.
- sp_return_book sets loan.returned_on, increments availability, and the trigger writes a RETURN row to loan_audit.
- fn_late_fee accepts due_on and returned_on to compute capped overdue fees.
- Views read loan, member, book, and author to surface overdue loans, per-member stats, and popular books.
- Migration v2 converts book.copies_total/copies_free into book_copy rows per branch, adds loan.copy_id and member.home_branch_id, then drops the aggregate counters.

#### Dependencies
- Internal tables: author, book, member, loan, loan_audit, branch, book_copy.
- 02_views.sql depends on loan, member, book, author.
- 03_routines.sql depends on member, book, loan and creates loan_audit plus trg_loan_audit.
- 04_migration_v2.sql depends on book, loan, member and creates/alters branch and book_copy.
- 05_seed_data.sql requires author, book, member, and loan to exist before execution.
- External: PostgreSQL with PL/pgSQL, materialized views, partial indexes, and TIMESTAMPTZ.

#### Cross-cutting
- CHECK constraints enforce copies_free <= copies_total (pre-v2), due_on > loaned_on, audit action values, copy status values, and author birth_year range.
- Unique constraints on book.isbn, member.email, branch.name, book_copy.barcode, and popular_books.book_id.
- Partial index idx_loan_open on loan(due_on) WHERE returned_on IS NULL.
- ON DELETE CASCADE from member to loan.
- Audit trigger fires on loan INSERT and on loan.returned_on transitioning from null to non-null.
- Seed script wrapped in BEGIN / COMMIT transaction.
- fn_late_fee is IMMUTABLE.
