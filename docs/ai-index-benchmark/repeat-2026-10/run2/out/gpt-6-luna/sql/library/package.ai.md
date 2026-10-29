### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:52:06Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages library catalog, membership, book circulation, availability, loan reporting, and the migration from aggregate inventory counts to branch-assigned copies.

#### Purpose
- Define and operate a PostgreSQL lending-library database for authors, books, members, inventory, and loans.
- Support checkout and return processing, circulation reporting, audit records, sample data, and branch-aware inventory migration.

#### Responsibilities
- **Core schema and sample data:** Store authors, books, members, and loans with keys, constraints, relationships, and indexes; seed records for catalog, membership, and circulation scenarios.
- **Circulation operations and audit:** Enforce member blocking and copy availability during checkout, process returns, calculate capped late fees, and record checkout/return actions.
- **Reports:** Provide overdue-loan and member-activity views, plus a materialized view of popular books loaned at least five times in the past 365 days.
- **Branch inventory migration:** Add branches and individually barcoded `book_copy` records, assign existing inventory to a main branch, link loans and members to branch-aware records, and remove book-level copy counters.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, inventory-count, and circulation tables.
- `loan_audit` — stores checkout and return audit events.
- `overdue_loans` — reports unreturned loans past their due dates with member and book details.
- `member_loan_stats` — summarizes total and open loans and most recent loan date for each member, including members without loans.
- `popular_books` — materialized report of books loaned at least five times in the preceding 365 days.
- `fn_checkout_book` — rejects blocked members or unavailable books; creates a loan and decrements free copies.
- `sp_return_book` — marks an open loan returned and restores a free copy; errors for unknown or already-returned loans.
- `fn_late_fee` — calculates overdue charges at 50 cents per day, capped at 2000 cents.
- `trg_loan_audit_fn` / `trg_loan_audit` — record checkout inserts and transitions from open to returned.
- `branch`, `book_copy` — migration-added branch and individually identified, status-tracked inventory tables.

#### Data flow
- Seed data populates `author`, `book`, `member`, and `loan` within a transaction.
- Checkout validates member and book availability, updates `book.copies_free`, inserts a `loan`, then the audit trigger records `CHECKOUT`.
- Return updates `loan.returned_on`, restores a free copy, and the audit trigger records `RETURN`; late fees are calculated from due and return dates or the current date.
- Views and materialized views aggregate base-table loan, member, book, and author data into overdue, member-activity, and popular-book reports.
- Migration creates a main branch and generates `book_copy` rows from existing book totals and free-copy counts, then adds copy and home-branch references and drops book-level copy counters.

#### Dependencies
- Reporting and circulation routines use `loan`, `member`, `book`, and `author`; auditing also uses `loan_audit`.
- `book.author_id`, `loan.book_id`, and `loan.member_id` link core entities; the member-to-loan relationship cascades on member deletion.
- The migration links `book_copy` to `book` and `branch`, `loan.copy_id` to `book_copy`, and `member.home_branch_id` to `branch`; it uses PostgreSQL `generate_series`.
- `idx_book_author`, `idx_loan_member`, partial `idx_loan_open`, `idx_popular_books_id`, and `idx_book_copy_book` support lookup or materialized-view access.

#### Cross-cutting
- PostgreSQL primary keys, foreign keys, uniqueness, checks, defaults, and indexes enforce data integrity and support common queries.
- Checkout and return raise exceptions for invalid circulation conditions; `loan_audit` records operational transitions.
- The migration changes the inventory model from `book.copies_total` / `book.copies_free` to branch-assigned `book_copy` records.
