### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:53:19Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL lending library package that manages book inventory, member accounts, loan transactions, and reporting views for tracking overdue loans, member statistics, and popular books.

#### Purpose
- Provide data structures and operations for a lending library: tracking authors, books, members, and loans.
- Enable analysis of loan status, member activity, and book popularity via reporting views and audit trails.

#### Responsibilities
- Define core schema: author, book, member, loan tables and indexes.
- Create reporting views: overdue_loans, member_loan_stats, popular_books (materialized view).
- Implement PL/pgSQL routines for checkout, return, late fee calculation, and audit logging.
- Migrate schema to v2: branch entity, branch-specific book copies, member column rename, and retirement of copy counters.
- Seed the database with representative authors, books, members, and loan records.

#### Key units
- Tables: author, book, member, loan, loan_audit, branch, book_copy.
- Views: overdue_loans, member_loan_stats, popular_books (materialized view), idx_popular_books_id.
- Functions/Procedures/Triggers: fn_checkout_book, sp_return_book, fn_late_fee, trg_loan_audit_fn.
- Indexes: idx_book_author, idx_loan_member, idx_loan_open, idx_popular_books_id.

#### Data flow
- Seed data inserts initial rows into author, book, member, loan.
- Migration v2 creates branch and book_copy tables, renames member.joined_on to member_since, adds home_branch_id, and drops book copy counters.
- Routines execute on checkout/return: update copies_free, insert/update loan, and log audit.
- Views read base tables: overdue_loans joins loan, member, book; member_loan_stats joins member and loan; popular_books joins loan, book, author.
- Indexes accelerate lookups on author_id, member_id, due_on, and popular_books.book_id.

#### Dependencies
- Base tables: author, book, member, loan (01_schema.sql).
- Views depend on loan, member, book, author.
- Routines depend on loan, member, book, loan_audit.
- Migration depends on book, branch, book_copy.
- Seed data depends on all base tables.
- PL/pgSQL language and PostgreSQL runtime.

#### Cross-cutting
- Constraints: book.copies_free <= book.copies_total, loan.due_on > loaned_on, status enums, author birth_year range.
- Audit trail: loan_audit records CHECKOUT on loan insert and RETURN on loan update.
- Late fee formula: min(max(overdue_days*50,0),2000) cents.
- Foreign keys: author.author_id -> book.author_id, book.book_id -> loan.book_id, member.member_id -> loan.member_id (cascade), loan -> book_copy (v2).
- Materialized view popular_books is refreshed from loan/book/author.
