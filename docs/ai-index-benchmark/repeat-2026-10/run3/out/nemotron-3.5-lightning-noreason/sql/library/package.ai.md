### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:56Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A lending library system managing authors, books, members, and loans with full checkout, return, and reporting capabilities.

#### Purpose
- Provides the relational structure, business logic, and reporting interfaces for a lending library database.

#### Responsibilities
- **Core schema**: Defines authors, books, members, and loans with integrity constraints and indexes.
- **Checkout/return logic**: PL/pgSQL functions and triggers that enforce member status, book availability, late fees, and audit trails.
- **Migration**: Adds branch support, redistributes copy tracking, renames columns, and retires legacy per-book counters.
- **Reporting**: Views for overdue loans, member statistics, and popular books.

#### Key units
- **author / book / member / loan** (01_schema.sql): Core relational tables with PK/FK constraints and copy‑availability checks.
- **loan_audit** (03_routines.sql): Audit table tracking CHECKOUT and RETURN actions.
- **fn_checkout_book / sp_return_book / fn_late_fee** (03_routines.sql): PL/pgSQL routines for lending workflow and fee calculation.
- **trg_loan_audit** (03_routines.sql): Trigger that writes audit entries on loan insert or returned_on update.
- **overdue_loans / member_loan_stats / popular_books** (02_views.sql): Reporting views with aggregates and materialized view.
- **branch / book_copy** (04_migration_v2.sql): Migration objects for multi‑branch support and copy‑id tracking.
- **member_since / home_branch_id** (04_migration_v2.sql): Renamed/ added member columns.

#### Data flow
1. **Checkout**: `fn_checkout_book` validates `member.is_blocked = FALSE` and `book.copies_free > 0`, inserts a `loan` row, decrements `book.copies_free`, and fires `trg_loan_audit` to record CHECKOUT.
2. **Return**: `sp_return_book` sets `loan.returned_on = CURRENT_DATE`, increments `book.copies_free`, and fires the trigger to record RETURN.
3. **Late fee**: `fn_late_fee` computes 50 cents per day overdue, capped at 2000 cents, using due_on and returned_on.
4. **Migration**: Branch and book_copy tables are populated from legacy book data; loan rows gain a `copy_id` FK; member column `joined_on` is renamed to `member_since` and a `home_branch_id` is added.
5. **Reporting**: Views join loan, member, book, and author to surface overdue status, per‑member stats, and books with ≥5 loans in the last year.

#### Dependencies
- **Tables**: author, book, member, loan, loan_audit, branch, book_copy.
- **Functions**: fn_checkout_book, fn_late_fee, sp_return_book.
- **Triggers**: trg_loan_audit.
- **Sequences**: loan_audit_audit_id_seq (implied by BIGSERIAL).
- **External**: PostgreSQL generate_series for migration data population.

#### Cross-cutting
- **Check constraints**: book.copies_free ≤ copies_total; loan.due_on > loaned_on; loan_audit.action IN ('CHECKOUT','RETURN').
- **Error handling**: Routines raise exceptions when member is blocked, no free copy exists, or return is attempted on an already‑returned loan.
- **Trigger logic**: Single trigger handles both INSERT (CHECKOUT) and UPDATE of returned_on (RETURN) via NEW/OLD distinction.
- **Naming conventions**: Consistent use of BIGSERIAL PKs, VARCHAR lengths, and FK naming (idx_*, book_copy, member_since).
