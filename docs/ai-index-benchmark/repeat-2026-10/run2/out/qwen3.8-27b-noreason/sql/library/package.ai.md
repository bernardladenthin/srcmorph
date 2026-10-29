### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:10:29Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> PostgreSQL database schema and logic for a multi-branch lending library system, managing book inventory, member accounts, loan circulation, and reporting.

#### Purpose
- Defines the complete relational data model, business logic routines, and reporting views for a library management system.
- Handles the evolution of the schema from a simple copy-count model to a granular per-copy, branch-aware model via migration scripts.

#### Responsibilities
- **Core Schema Definition**: Establishes tables for `author`, `book`, `member`, and `loan` with integrity constraints and indexes.
- **Reporting & Analytics**: Provides read-only views (`overdue_loans`, `member_loan_stats`) and a materialized view (`popular_books`) for operational insights.
- **Circulation Logic**: Implements PL/pgSQL routines for checking out books, processing returns, calculating late fees, and auditing loan state changes.
- **Schema Migration**: Executes a v2 migration to introduce `branch` and `book_copy` entities, backfilling data and removing legacy aggregate copy counters.
- **Data Seeding**: Populates the database with initial test data for all core entities.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan` (core); `loan_audit` (logging); `branch`, `book_copy` (v2 inventory).
- **Views**: `overdue_loans` (current overdue items), `member_loan_stats` (per-member activity), `popular_books` (materialized ranking).
- **Routines**:
    - `fn_checkout_book`: Validates member status and inventory, decrements free copies, inserts loan record.
    - `sp_return_book`: Updates loan return date, increments free copies, handles errors for invalid loans.
    - `fn_late_fee`: Calculates penalty based on days overdue with a capped rate.
    - `trg_loan_audit`: Trigger function logging `CHECKOUT` and `RETURN` actions to `loan_audit`.
- **Indexes**: `idx_book_author`, `idx_loan_member`, `idx_loan_open` (partial index for active loans), `idx_book_copy_book`, `idx_popular_books_id`.

#### Data flow
- **Checkout**: Input member/book IDs → `fn_checkout_book` validates `member.is_blocked` and `book.copies_free` → Updates `book.copies_free` → Inserts into `loan` → Trigger `trg_loan_audit` logs event.
- **Return**: Input loan ID → `sp_return_book` validates loan status → Updates `loan.returned_on` → Updates `book.copies_free` → Trigger `trg_loan_audit` logs event.
- **Reporting**: Views aggregate data from `loan`, `member`, `book`, and `author` to surface overdue items, member statistics, and book popularity.
- **Migration**: `04_migration_v2.sql` reads legacy `book` copy counts → Creates `branch` and `book_copy` → Backfills `book_copy` using `generate_series` → Alters `loan` and `member` to reference new structures → Drops legacy columns.

#### Dependencies
- **Internal**: All routines and views depend on the core tables (`author`, `book`, `member`, `loan`). Migration scripts depend on the initial schema defined in `01_schema.sql`.
- **External**: PostgreSQL dialect features (`BIGSERIAL`, `PL/pgSQL`, `generate_series`, partial indexes, materialized views).

#### Cross-cutting
- **Integrity Constraints**: Extensive use of `CHECK` constraints for data validity (e.g., `copies_free <= copies_total`, `due_on > loaned_on`, valid `status` values).
- **Referential Integrity**: Foreign keys enforce relationships between books/authors, loans/members, and (in v2) loans/copies/branches.
- **Audit Trail**: The `loan_audit` table and associated trigger provide a persistent log of all circulation events.
- **Schema Evolution**: The package includes a clear migration path (`04_migration_v2.sql`) that transforms the data model from aggregate counts to granular physical copy tracking.
