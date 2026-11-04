### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:34:49Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports checkout/return logic, tracks late fees, and provides reporting views for overdue loans, member activity, and popular titles.  

#### Purpose  
- Persist library catalog, member, and loan information.  
- Enforce business rules for borrowing, returning, and late fee calculation.  
- Offer read‑only reporting views for operational insight.  

#### Responsibilities  
- **Data definition** – core tables (`author`, `book`, `member`, `loan`) and later branch‑specific tables (`branch`, `book_copy`).  
- **Business logic** – checkout, return, late‑fee calculation, and audit trail via functions, procedures, and triggers.  
- **Reporting** – views (`overdue_loans`, `member_loan_stats`, `popular_books`) and materialized view for popular books.  
- **Migration & seeding** – schema evolution to branch‑based copies and initial data population.  

#### Key units  
- **Tables**  
  - `author`, `book`, `member`, `loan` – core library entities.  
  - `branch`, `book_copy` – branch hierarchy and per‑copy tracking.  
  - `loan_audit` – audit trail of checkout/return actions.  
- **Functions**  
  - `fn_checkout_book(member_id, book_id, days)` – validates, updates inventory, inserts loan.  
  - `fn_late_fee(due_on, returned_on)` – computes overdue fee capped at 2000 cents.  
- **Procedure**  
  - `sp_return_book(loan_id)` – marks loan returned, restores copy count.  
- **Trigger**  
  - `trg_loan_audit` → `trg_loan_audit_fn()` – logs audit rows on INSERT/UPDATE of `loan`.  
- **Views**  
  - `overdue_loans` – unreturned loans past due.  
  - `member_loan_stats` – per‑member loan totals and last activity.  
  - `popular_books` – books loaned ≥5 times in the past year.  

#### Data flow  
1. **Checkout**: `fn_checkout_book` → checks `member.is_blocked`, decrements `book.copies_free`, inserts `loan`, logs audit.  
2. **Return**: `sp_return_book` → updates `loan.returned_on`, increments `book.copies_free`, logs audit.  
3. **Late fee**: `fn_late_fee` called by application or reporting to calculate fees.  
4. **Reporting**: Views aggregate `loan`, `member`, `book`, `author` data for dashboards.  

#### Dependencies  
- **Internal** – `member`, `book`, `loan`, `author`, `branch`, `book_copy`, `loan_audit`.  
- **External** – PostgreSQL built‑in functions (`now()`, `CURRENT_DATE`, `COALESCE`).  
- **Indexes** – `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.  

#### Cross-cutting  
- **Constraints & checks** – enforce data integrity (`copies_free <= copies_total`, `due_on > loaned_on`, status enum).  
- **Audit pattern** – single audit table with action enum and timestamp, populated by a trigger.  
- **Branch‑aware migration** – `04_migration_v2.sql` moves copy counts into `book_copy` rows, updates foreign keys to reference copies instead of book totals.  
- **Seeding** – `05_seed_data.sql` populates all core tables for demo/testing.
