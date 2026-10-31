### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:43:23Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that defines core data structures, reporting views, business logic for check‑outs and returns, and a migration to a branch‑based copy model.

#### Purpose
- Persist library data: authors, books, members, loans.
- Provide reporting on overdue loans, member activity, and popular books.
- Enforce checkout/return rules and audit changes.
- Upgrade schema to support branches and individual book copies.

#### Responsibilities
- **Schema definition** – tables, constraints, indexes for authors, books, members, loans.
- **Reporting** – views for overdue loans, member statistics, popular books.
- **Business logic** – functions and procedures for checkout, return, late‑fee calculation, and audit logging.
- **Migration** – adding branch hierarchy, book_copy table, and refactoring loan/member references.
- **Seeding** – initial data for authors, books, members, and loans.

#### Key units
- `author`, `book`, `member`, `loan` tables – core entities.
- `loan_audit` table – audit trail of checkout/return actions.
- `overdue_loans`, `member_loan_stats`, `popular_books` views – reporting layers.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` – core operations.
- `trg_loan_audit` trigger – logs audit rows on loan changes.
- `branch`, `book_copy` tables – new branch‑based copy model.

#### Data flow
1. **Checkout** (`fn_checkout_book`)  
   - Validate member not blocked and book has free copies.  
   - Decrement `book.copies_free`.  
   - Insert new `loan` row with `due_on`.  
   - Trigger `trg_loan_audit` logs `CHECKOUT`.

2. **Return** (`sp_return_book`)  
   - Update `loan.returned_on`.  
   - Increment `book.copies_free`.  
   - Trigger `trg_loan_audit` logs `RETURN`.

3. **Late fee** (`fn_late_fee`)  
   - Compute days overdue and apply rate/cap.

4. **Reporting**  
   - `overdue_loans` aggregates unreturned loans past due date.  
   - `member_loan_stats` aggregates loan counts and last activity per member.  
   - `popular_books` materialized view counts loans in the past year.

5. **Migration**  
   - Create `branch` and `book_copy`.  
   - Populate `book_copy` from existing `book` copy counts.  
   - Add `copy_id` to `loan`, `home_branch_id` to `member`.  
   - Remove legacy copy columns from `book`.

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- External: PostgreSQL built‑in functions (`now()`, `generate_series`), PL/pgSQL language.

#### Cross‑cutting
- **Integrity** – PK/FK constraints, check constraints, unique indexes, and cascade deletes.
- **Audit** – `loan_audit` table and `trg_loan_audit` trigger capture every checkout/return.
- **Performance** – indexes on foreign keys and open‑loan queries (`idx_loan_open`).
- **Configuration** – late‑fee rate (50 per day) and cap (2000) hard‑coded in `fn_late_fee`.
