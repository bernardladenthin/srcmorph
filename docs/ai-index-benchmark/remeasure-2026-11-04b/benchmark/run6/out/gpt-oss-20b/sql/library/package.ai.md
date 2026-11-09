### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:58:20Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that defines catalog, lending, and reporting objects, and provides stored routines for checkout, return, and fee calculation.

#### Purpose
- Persist library catalog, members, and loan data.
- Offer reporting views for overdue loans, member activity, and popular books.
- Encapsulate business logic for lending operations and audit logging.

#### Responsibilities
- **Schema definition** – tables `author`, `book`, `member`, `loan`, plus migration tables `branch`, `book_copy`.
- **Reporting** – views `overdue_loans`, `member_loan_stats`, materialized view `popular_books`.
- **Business logic** – functions `fn_checkout_book`, `fn_late_fee`; procedure `sp_return_book`; audit table `loan_audit` and trigger `trg_loan_audit`.
- **Data seeding** – initial data for authors, books, members, and loans.

#### Key units
- **Tables**  
  - `author` – author metadata.  
  - `book` – book catalog with copy counters.  
  - `member` – library patrons.  
  - `loan` – active and historical loan records.  
  - `branch` – library locations.  
  - `book_copy` – individual copy tracking per branch.  
  - `loan_audit` – audit trail of checkout/return actions.
- **Views**  
  - `overdue_loans` – list of loans past due.  
  - `member_loan_stats` – per‑member loan counts and last activity.  
  - `popular_books` – top‑loaned books in the past year.
- **Functions / Procedures**  
  - `fn_checkout_book(member_id, book_id, days)` – validates, decrements copy, inserts loan.  
  - `sp_return_book(loan_id)` – marks loan returned, increments copy.  
  - `fn_late_fee(due_on, returned_on)` – calculates fee up to a cap.  
  - `trg_loan_audit_fn()` – trigger function logging checkout/return events.

#### Data flow
1. **Checkout**  
   - `fn_checkout_book` checks member status and book availability.  
   - Decrements `book.copies_free`.  
   - Inserts a new `loan` row with `due_on`.  
   - Trigger logs `CHECKOUT` in `loan_audit`.
2. **Return**  
   - `sp_return_book` sets `loan.returned_on`.  
   - Increments `book.copies_free`.  
   - Trigger logs `RETURN` in `loan_audit`.
3. **Reporting**  
   - Views join `loan`, `member`, `book`, `author` to expose aggregated data.  
   - `popular_books` materialized view aggregates loan counts per book.

#### Dependencies
- **Internal** – tables `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; views depend on these tables.  
- **External** – PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, `INTERVAL`).  
- **Migration** – `04_migration_v2.sql` adds `branch` and `book_copy`, altering `loan` and `member` to reference copies and branches.

#### Cross-cutting
- **Integrity** – foreign keys, CHECK constraints, and unique indexes enforce data validity.  
- **Audit** – `loan_audit` and trigger `trg_loan_audit` provide immutable action history.  
- **Branch & Copy Tracking** – migration replaces aggregate copy counters with per‑copy status, enabling branch‑level inventory.  
- **Seeding** – `05_seed_data.sql` populates all core tables for demo or test environments.
