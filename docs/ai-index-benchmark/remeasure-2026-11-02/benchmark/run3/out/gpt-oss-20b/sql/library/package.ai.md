### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:03:54Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library lending system that stores catalog, member, and loan data, enforces borrowing rules, tracks audit events, and provides reporting views.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce business rules for borrowing and returning books.
- Offer read‑only reporting on overdue loans, member activity, and popular titles.

#### Responsibilities
- **Data definition** – tables `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **Business logic** – functions `fn_checkout_book`, `fn_late_fee`; procedure `sp_return_book`; trigger `trg_loan_audit`.
- **Reporting** – views `overdue_loans`, `member_loan_stats`; materialized view `popular_books`.
- **Migration** – script `04_migration_v2.sql` adds branch hierarchy and book copy tracking.
- **Seeding** – `05_seed_data.sql` populates sample authors, books, members, and loans.

#### Key units
- **Tables**
  - `author` – author metadata.
  - `book` – book catalog with copy counts.
  - `member` – patron records, blocked status, home branch.
  - `loan` – loan transactions, due/return dates.
  - `loan_audit` – audit trail of checkout/return actions.
  - `branch` – library branch locations.
  - `book_copy` – individual physical copies with status and barcode.
- **Functions / Procedures**
  - `fn_checkout_book(member_id, book_id, days)` – validates member, decrements `copies_free`, inserts loan.
  - `sp_return_book(loan_id)` – marks loan returned, increments `copies_free`.
  - `fn_late_fee(due_on, returned_on)` – calculates overdue fee up to 2000 cents.
  - `trg_loan_audit_fn()` – trigger function logging checkout/return events.
- **Views**
  - `overdue_loans` – list of loans past due with days overdue.
  - `member_loan_stats` – per‑member loan totals and last activity.
  - `popular_books` – books ranked by loan count (materialized).

#### Data flow
1. **Checkout** – `fn_checkout_book` checks `member.is_blocked`, verifies `book.copies_free > 0`, decrements `copies_free`, inserts into `loan`, and the trigger logs a `CHECKOUT` in `loan_audit`.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and the trigger logs a `RETURN`.
3. **Reporting** – Views join `loan`, `member`, `book`, `author` (and `book_copy`/`branch` after migration) to compute overdue status, member statistics, and popularity metrics.
4. **Migration** – `04_migration_v2.sql` creates `branch` and `book_copy`, moves copy counts into `book_copy`, and updates `member.home_branch_id` and `loan.copy_id` to reference the new structure.

#### Dependencies
- **Internal** – foreign keys among `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`; triggers on `loan`; views referencing these tables.
- **External** – PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, date arithmetic, `FILTER`, `HAVING`).

#### Cross-cutting
- **Constraints & Checks** – `copies_free <= copies_total`, `loan.due_on > loan.loaned_on`, `book_copy.status` enum, `author.birth_year` range.
- **Audit Trail** – `loan_audit` records every checkout/return with timestamp.
- **Materialized View Refresh** – `popular_books` requires periodic refresh to stay current.
- **Branch & Copy Model** – after migration, all loan operations reference `book_copy` and `branch` for location tracking.
