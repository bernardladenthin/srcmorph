### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:05:03Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Models a lending library's core domain: authors, books, members, book loans with availability tracking, physical copies per branch, and reporting views for overdue loans and popular books.

#### Purpose
- Defines and seeds a PostgreSQL lending library schema covering authors, books, members, loans, physical copies, branches, and audit trails.
- Provides checkout/return routines, late fee calculation, loan auditing, and reporting views for overdue loans, member statistics, and popular books.

#### Responsibilities
- Core domain tables: author, book, member, loan (01_schema.sql)
- Reporting: overdue_loans view, member_loan_stats view, popular_books materialized view (02_views.sql)
- Business logic: fn_checkout_book, sp_return_book, fn_late_fee, loan_audit trigger (03_routines.sql)
- Schema migration: branch and book_copy tables, per-copy tracking, member home branch (04_migration_v2.sql)
- Seed data: 15 authors, 110 books, 50 members, 150 loans (05_seed_data.sql)

#### Key units
- author: author_id, full_name, birth_year, country_code
- book: book_id, isbn, title, author_id, published_year (copies_total/copies_free dropped in migration)
- member: member_id, email, full_name, member_since, is_blocked, home_branch_id
- loan: loan_id, book_id, member_id, copy_id, loaned_on, due_on, returned_on
- branch: branch_id, name, city
- book_copy: copy_id, book_id, branch_id, barcode, status
- loan_audit: audit_id, loan_id, action, changed_at
- fn_checkout_book: lends a free copy to a member, returns loan_id
- sp_return_book: marks a loan returned, restores copy availability
- fn_late_fee: computes overdue fee in cents capped at 2000
- trg_loan_audit_fn / trg_loan_audit: audits CHECKOUT and RETURN actions
- overdue_loans: view of loans past due with days_overdue
- member_loan_stats: per-member loan aggregates
- popular_books: materialized view of frequently loaned books with unique index on book_id

#### Data flow
- Seed data populates author, book, member, and loan tables (05_seed_data.sql)
- Migration v2 adds branch and book_copy, populates one copy per existing book at the Main branch, assigns members to the Main branch, and adds copy_id to loan (04_migration_v2.sql)
- fn_checkout_book validates member and book availability, decrements copies_free, inserts a loan row, and the audit trigger records CHECKOUT (03_routines.sql)
- sp_return_book sets returned_on, increments copies_free, and the audit trigger records RETURN (03_routines.sql)
- fn_late_fee calculates cents owed based on due date and return date (03_routines.sql)
- Reporting views query loan, member, book, and author for overdue loans, member stats, and popular books (02_views.sql)

#### Dependencies
- Internal: author, book, member, loan, branch, book_copy, loan_audit tables; fn_checkout_book, sp_return_book, fn_late_fee routines; trg_loan_audit_fn trigger
- External: PostgreSQL (BIGSERIAL, SMALLSERIAL, PL/pgSQL, generate_series, materialized views, partial indexes)

#### Cross-cutting
- Integrity constraints: copies_free <= copies_total, due_on > loaned_on, status CHECK on book_copy, action CHECK on loan_audit
- Indexes: idx_book_author, idx_loan_member, idx_loan_open (partial on open loans), idx_book_copy_book, idx_popular_books_id (unique)
- Transactional: seed script and migration wrapped in BEGIN/COMMIT
- Migration retires per-book copy counters (copies_total, copies_free) in favor of physical book_copy rows per branch
