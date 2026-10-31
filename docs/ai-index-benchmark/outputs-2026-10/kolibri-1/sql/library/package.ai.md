### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:41:42Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A lending library system managing book copies, member loans, and checkout operations across branches.

#### Purpose
- Core schema for a lending library: authors, books, members, loans.
- Tracks book copies, loan status, and member blocking.

#### Responsibilities
- Schema and data modeling: authors, books, members, loans, branches, book copies.
- Reporting views: overdue loans, member loan statistics, popular books.
- Checkout and return operations: checkout, return, late fee calculation, audit trail.
- Schema migration: branch and book_copy tables, copy counters, member home branch.
- Seed data: authors, books, members, loans.

#### Key units
- book: book_id, isbn, title, author_id, copies_total, copies_free.
- member: member_id, email, full_name, joined_on, is_blocked.
- loan: loan_id, book_id, member_id, loaned_on, due_on, returned_on.
- author: author_id, full_name, birth_year, country_code.
- branch: branch_id, name, city.
- book_copy: copy_id, book_id, branch_id, barcode, status.
- loan_audit: audit_id, loan_id, action, changed_at.
- overdue_loans: loan_id, member_id, email, title, due_on, days_overdue.
- member_loan_stats: member_id, full_name, loans_total, loans_open, last_loan_on.
- popular_books: book_id, title, author_name, times_loaned.
- fn_checkout_book: checks member.is_blocked, decrements book.copies_free, inserts loan with due_on, returns loan_id.
- sp_return_book: sets loan.returned_on, increments book.copies_free.
- fn_late_fee: calculates days overdue, multiplies by 50, caps at 2000.

#### Data flow
- Checkout: fn_checkout_book checks member.is_blocked, decrements book.copies_free, inserts loan with due_on, returns loan_id.
- Return: sp_return_book sets loan.returned_on, increments book.copies_free.
- Late fee: fn_late_fee calculates days overdue, multiplies by 50, caps at 2000.
- Audit: trg_loan_audit_fn inserts audit row on loan INSERT or returned_on update.

#### Dependencies
- PostgreSQL DDL: tables, indexes, views, materialized views, functions, procedures, triggers.
- Internal collaborations: book.author_id -> author.author_id, loan.book_id -> book.book_id, loan.member_id -> member.member_id ON DELETE CASCADE, loan.copy_id -> book_copy.copy_id, member.home_branch_id -> branch.branch_id.
- External modules: generate_series, current_date, interval.

#### Cross-cutting
- PostgreSQL DDL: tables, indexes, views, materialized views, functions, procedures, triggers.
- CHECK constraints: birth_year CHECK (1000-2100), copies_free <= copies_total, status CHECK (IN ('AVAILABLE', 'ON_LOAN', 'LOST')), action CHECK (IN ('CHECKOUT', 'RETURN')).
- Indexes: idx_book_author on book(author_id), idx_loan_member on loan(member_id), idx_loan_open on loan(due_on) WHERE returned_on IS NULL, idx_popular_books_id unique on popular_books(book_id).
- Materialized view: popular_books with unique book_id.
- Trigger: trg_loan_audit_fn on loan INSERT or returned_on update.
- Routine logic: overdue_loans filter loans where returned_on is null and due_on before current date; member_loan_stats left join loans to members; popular_books filter loans within last 365 days.
