### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:23:18Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports book lending logic, and provides reporting views for overdue items and popular titles.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce business rules for checkout, return, and late fees.
- Deliver read‑only reports for dashboards and alerts.

#### Responsibilities
- **Schema definition** – core tables (`author`, `book`, `member`, `loan`) and indexes.  
- **Reporting** – views (`overdue_loans`, `member_loan_stats`, `popular_books`) and materialized view refresh.  
- **Business logic** – functions and procedures for checkout, return, late fee calculation, and audit trail.  
- **Migration** – branch and per‑copy support via `branch`, `book_copy`, and altered `loan`/`member`.  
- **Seeding** – initial data population for development and demos.

#### Key units
- `author`, `book`, `member`, `loan` tables – core data storage.  
- `loan_audit` table – audit trail for checkout/return actions.  
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – checkout logic.  
- `sp_return_book(p_loan_id)` – return logic.  
- `fn_late_fee(p_due_on, p_returned_on)` – late fee calculation.  
- `trg_loan_audit_fn()` – trigger to insert audit rows.  
- `overdue_loans`, `member_loan_stats`, `popular_books` views – reporting surfaces.  
- `branch`, `book_copy` tables – branch and copy tracking introduced in migration v2.

#### Data flow
1. **Checkout** – `fn_checkout_book` validates member status, decrements `book.copies_free`, inserts a `loan` row, and triggers `loan_audit`.  
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and triggers `loan_audit`.  
3. **Late fee** – `fn_late_fee` is called during return or reporting to compute overdue charges.  
4. **Reporting** – Views join `loan`, `member`, `book`, and `author` to expose overdue counts, member statistics, and loan frequency.  
5. **Migration** – `04_migration_v2.sql` replaces copy counters with `book_copy` rows, adds branch references, and updates existing data accordingly.

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
- External: PostgreSQL core functions, PL/pgSQL runtime, and standard system catalogs for constraints and indexes.

#### Cross-cutting
- **Integrity constraints** – foreign keys, check constraints, and unique indexes enforce data validity.  
- **Audit trail** – `loan_audit` records every checkout and return with timestamp.  
- **Branch & copy abstraction** – migration introduces explicit copy tracking and branch association for future multi‑location support.  
- **Late fee policy** – fixed rate (50 ¢ per day) capped at 2000 ¢, encapsulated in `fn_late_fee`.
