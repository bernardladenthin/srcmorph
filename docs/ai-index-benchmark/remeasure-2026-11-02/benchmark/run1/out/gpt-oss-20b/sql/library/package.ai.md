### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:33:12Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL schema and supporting objects for a multi‑branch lending library, including catalog, member, loan logic, reporting views, and migration to branch‑specific book copies.

#### Purpose
- Persist library catalog, member, and loan data.
- Provide business logic for checkout, return, and late fee calculation.
- Offer reporting views for overdue loans, member activity, and popular books.
- Support a migration to branch‑based copy tracking.

#### Responsibilities
- **Data definition** – tables `author`, `book`, `member`, `loan`, `branch`, `book_copy`, and audit table `loan_audit`.
- **Business logic** – functions `fn_checkout_book`, `fn_late_fee`; procedure `sp_return_book`; trigger `trg_loan_audit`.
- **Reporting** – views `overdue_loans`, `member_loan_stats`; materialized view `popular_books`.
- **Migration** – script `04_migration_v2.sql` adds branches and book copies, updates foreign keys.
- **Seeding** – `05_seed_data.sql` populates initial data for testing.

#### Key units
- **Tables**  
  - `author` – author catalog.  
  - `book` – book catalog (pre‑migration).  
  - `member` – library members.  
  - `loan` – loan records.  
  - `branch` – library locations.  
  - `book_copy` – individual copies per branch.  
  - `loan_audit` – audit trail of checkout/return actions.  
- **Functions / Procedures**  
  - `fn_checkout_book(member_id, book_id, days)` – creates loan, decrements copies.  
  - `sp_return_book(loan_id)` – marks loan returned, increments copies.  
  - `fn_late_fee(due_on, returned_on)` – calculates fee up to 2000.  
  - `trg_loan_audit_fn()` – trigger function logging audit rows.  
- **Views**  
  - `overdue_loans` – overdue loan details with days overdue.  
  - `member_loan_stats` – per‑member loan totals and last loan date.  
  - `popular_books` – books loaned most in the past year.  

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `book.copies_free`, inserts `loan`, logs audit.  
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, logs audit.  
3. **Late fee** – `fn_late_fee` computes fee based on `due_on` and `returned_on`.  
4. **Reporting** – views aggregate `loan`, `member`, `book`, and `author` data for overdue, stats, and popularity.  
5. **Migration** – `04_migration_v2.sql` creates `branch` and `book_copy`, moves copy counts, updates foreign keys.

#### Dependencies
- **Internal** – `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
- **External** – PostgreSQL built‑in types, `now()`, `CURRENT_DATE`.  
- **Indexes** – `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.

#### Cross-cutting
- **Constraints** – foreign keys, uniqueness on ISBN and barcode, status checks, copy count consistency.  
- **Error handling** – functions raise errors for blocked members or unavailable copies.  
- **Audit trail** – `loan_audit` records every checkout and return via trigger.  
- **Branch dimension** – migration introduces `branch` and `book_copy` tables, replacing book‑level copy counts.  
- **Materialized view** – `popular_books` requires periodic refresh to stay current.
