### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T10:01:09Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL-based lending library system managing book inventory, member accounts, loan transactions, and multi-branch operations with automated fee calculation and audit logging.

#### Purpose
- Implements the core data model and business logic for a library management system, covering book cataloging, member registration, and loan lifecycle management.
- Provides reporting capabilities for overdue loans, member statistics, and popular titles, alongside a migration path to multi-branch inventory tracking.

#### Responsibilities
- **Schema Definition**: Defines core tables (`author`, `book`, `member`, `loan`) and enforces data integrity via constraints (e.g., `book_free_le_total`, `loan_due_after_loan`).
- **Reporting & Analytics**: Creates views (`overdue_loans`, `member_loan_stats`) and a materialized view (`popular_books`) for operational insights.
- **Loan Operations**: Implements PL/pgSQL routines for checkout (`fn_checkout_book`), return (`sp_return_book`), and late fee calculation (`fn_late_fee`).
- **Audit & Compliance**: Maintains an immutable `loan_audit` table via triggers (`trg_loan_audit`) to log state changes.
- **Schema Evolution**: Executes a v2 migration to introduce `branch` and `book_copy` tables, replacing aggregate copy counters with granular, branch-specific inventory.
- **Data Seeding**: Populates initial test data for authors, books, members, and loans.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan` (core entities); `branch`, `book_copy` (v2 inventory); `loan_audit` (audit log).
- **Views**: `overdue_loans` (detects past-due items), `member_loan_stats` (aggregates borrowing history), `popular_books` (materialized view for top-loaned titles).
- **Routines**: `fn_checkout_book` (validates member/book availability, decrements inventory), `sp_return_book` (updates loan status, increments inventory), `fn_late_fee` (calculates capped overdue charges), `trg_loan_audit_fn` (logs CHECKOUT/RETURN actions).
- **Constraints**: `book_free_le_total` (inventory consistency), `loan_due_after_loan` (date validity).

#### Data flow
- **Checkout**: `fn_checkout_book` validates `member.is_blocked` and `book.copies_free`, decrements `book.copies_free`, inserts into `loan`, and triggers `trg_loan_audit` to log the action.
- **Return**: `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and triggers `trg_loan_audit` to log the return.
- **Reporting**: Views join `loan`, `member`, `book`, and `author` to compute `days_overdue`, `loans_open`, and `times_loaned`.
- **Migration**: `04_migration_v2.sql` migrates data from `book.copies_total/free` to `book_copy` rows linked to `branch`, updates `loan` to reference `copy_id`, and alters `member` to include `home_branch_id`.

#### Dependencies
- **Internal**: All routines and views depend on the core tables (`author`, `book`, `member`, `loan`). The v2 migration depends on the initial schema and introduces dependencies on `branch` and `book_copy`.
- **External**: PostgreSQL built-in functions (`generate_series`, `CURRENT_DATE`, `now()`, `COALESCE`).

#### Cross-cutting
- **Inventory Management**: Initially uses aggregate columns (`copies_total`, `copies_free`) on `book`; migrated to granular `book_copy` rows with `status` (`AVAILABLE`, `ON_LOAN`, `LOST`) in v2.
- **Audit Trail**: Consistent logging of loan state changes via `loan_audit` table, triggered by `INSERT` and `UPDATE` on `loan`.
- **Constraint Enforcement**: Strict validation of inventory levels and date logic via CHECK constraints and routine-level exceptions.
- **Multi-Branch Support**: Introduced in v2 via `branch` table and `home_branch_id` on `member`, enabling location-specific inventory tracking.
