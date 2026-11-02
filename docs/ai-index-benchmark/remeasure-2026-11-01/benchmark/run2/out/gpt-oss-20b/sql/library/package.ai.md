### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:48:29Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL library management system that stores catalog, member, and loan data, exposes reporting views, and provides stored routines for checkout, return, and late‑fee calculation.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce business rules (copy availability, due dates, blocking).
- Provide reporting and audit capabilities.

#### Responsibilities
- **Data definition** – tables `author`, `book`, `member`, `loan`; indexes for performance.
- **Reporting** – views `overdue_loans`, `member_loan_stats`, materialized view `popular_books`.
- **Business logic** – functions `fn_checkout_book`, `fn_late_fee`; procedure `sp_return_book`; audit table `loan_audit` and trigger `trg_loan_audit`.
- **Schema evolution** – migration `04_migration_v2.sql` adds `branch`, `book_copy`, and updates foreign‑key relationships.
- **Seeding** – `05_seed_data.sql` populates initial data for testing.

#### Key units
- **Tables**  
  - `author(author_id, full_name, birth_year, country_code)` – author metadata.  
  - `book(book_id, isbn, title, author_id, published_year, copies_total, copies_free)` – book catalog with copy counts.  
  - `member(member_id, email, full_name, joined_on, is_blocked)` – patron records.  
  - `loan(loan_id, book_id, member_id, loaned_on, due_on, returned_on)` – loan transactions.  
  - `loan_audit(audit_id, loan_id, action, changed_at)` – audit trail for checkout/return.  
  - `branch(branch_id, name, city)` – library locations.  
  - `book_copy(copy_id, book_id, branch_id, barcode, status)` – individual copies after migration.
- **Views**  
  - `overdue_loans` – list of overdue loans with days overdue.  
  - `member_loan_stats` – per‑member loan totals and last activity.  
  - `popular_books` – books ordered by loan frequency.  
- **Functions / Procedures**  
  - `fn_checkout_book(p_member_id, p_book_id, p_days)` – validates, decrements copies, inserts loan.  
  - `sp_return_book(p_loan_id)` – marks loan returned, increments copies.  
  - `fn_late_fee(p_due_on, p_returned_on)` – calculates fee up to 2000.  
  - `trg_loan_audit_fn()` – trigger function logging checkout/return actions.

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `book.copies_free`, inserts into `loan`, trigger logs `CHECKOUT`.  
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, trigger logs `RETURN`.  
3. **Late fee** – `fn_late_fee` called by application or reports to compute fees.  
4. **Reporting** – views aggregate `loan`, `member`, `book`, `author` data for overdue, member activity, and popularity.  
5. **Migration** – `04_migration_v2.sql` replaces copy counters with `book_copy` rows, updates `loan` to reference `copy_id`, and adds `branch` linkage.

#### Dependencies
- **Internal** – `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
- **External** – PostgreSQL built‑in functions (`CURRENT_DATE`, `INTERVAL`, `now()`), standard SQL types.  
- **Indexes** – `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.

#### Cross-cutting
- **Foreign‑key integrity** enforced across all tables.  
- **Constraints**: `book_free_le_total`, `author.birth_year` range, `book_copy.status` enum, `loan_due_after_loan`.  
- **Audit trail** via `loan_audit` and trigger `trg_loan_audit`.  
- **Materialized view** `popular_books` refreshed manually or by schedule.  
- **Branch dimension** added in migration to support multi‑location copy tracking.
