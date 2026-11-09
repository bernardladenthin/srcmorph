### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:53:00Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, provides reporting views, and enforces business rules through stored routines and audit logging.  

#### Purpose
- Persist library catalog, member, and loan information.  
- Enforce borrowing rules and track late fees.  
- Offer aggregated reporting for overdue loans, member activity, and popular titles.  

#### Responsibilities
- **Data Definition** – Tables, indexes, and constraints for authors, books, members, loans, and audit trails.  
- **Reporting** – Views and a materialized view summarizing overdue loans, member statistics, and popular books.  
- **Business Logic** – Functions and procedures for checking out, returning, and calculating late fees, plus triggers for audit logging.  
- **Schema Evolution** – Migration script adding branch support and explicit book copy tracking.  
- **Seeding** – Sample data for authors, books, members, and loans.  

#### Key units
- `author`, `book`, `member`, `loan` tables – core data entities.  
- `loan_audit` table – audit trail of checkout/return actions.  
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – checkout logic.  
- `sp_return_book(p_loan_id)` – return logic.  
- `fn_late_fee(p_due_on, p_returned_on)` – late fee calculation.  
- `trg_loan_audit_fn()` – trigger function logging audit rows.  
- `overdue_loans`, `member_loan_stats`, `popular_books` views – reporting surfaces.  
- `branch`, `book_copy` tables – added by migration for multi‑branch support.  

#### Data flow
1. **Checkout**: `fn_checkout_book` validates member, decrements `book.copies_free`, inserts a `loan` record, and triggers `loan_audit` with action `CHECKOUT`.  
2. **Return**: `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and triggers `loan_audit` with action `RETURN`.  
3. **Late fee**: `fn_late_fee` is called by application logic (or could be used in reports) to compute fees based on `due_on` and `returned_on`.  
4. **Reporting**: Views join `loan`, `member`, `book`, and `author` to expose overdue details, member loan summaries, and popular book counts.  

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
- External: PostgreSQL built‑in functions, `now()`, `CURRENT_DATE`.  
- Migration: relies on existing `book`, `loan`, `member` tables before adding `branch` and `book_copy`.  

#### Cross‑cutting
- **Constraints & Checks**: `copies_free <= copies_total`, `due_on > loaned_on`, `status` enum for `book_copy`.  
- **Indexes**: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.  
- **Audit Pattern**: `loan_audit` table and trigger `trg_loan_audit` capture all state changes to loans.  
- **Error Handling**: Functions raise exceptions when members are blocked or no copies are available.  
- **Concurrency**: Decrement/increment of `copies_free` occurs within the checkout/return functions to maintain consistency.
