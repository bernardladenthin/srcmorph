### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:13:51Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages book lending from catalog and member records through checkout, returns, overdue reporting, and branch-based physical inventory.

#### Purpose
- Defines and operates a PostgreSQL lending library, including circulation rules, audit history, and reporting.
- Evolves inventory from per-book copy counts to branch-assigned individual copies, with sample catalog and loan data.

#### Responsibilities
- **Core catalog and circulation schema:** `author`, `book`, `member`, and `loan` tables with keys, constraints, and lookup indexes.
- **Lending reports:** Views for overdue loans and member borrowing statistics, plus a materialized view of frequently loaned books.
- **Checkout, return, and fee rules:** Routines enforce member and availability checks, update loan and inventory records, and calculate capped overdue fees.
- **Audit tracking:** A loan trigger records checkout and return events in `loan_audit`.
- **Branch inventory migration:** Adds `branch` and `book_copy`, associates loans and members with branches, migrates existing book counts into copy records, and removes the old counters.
- **Sample data:** Seeds authors, books, members, and loan histories in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and lending records.
- `overdue_loans` — reports active loans past their due date with member and book details.
- `member_loan_stats` — aggregates total and open loans and the member’s most recent loan date.
- `popular_books` — materialized report of books loaned at least five times in the prior 365 days.
- `fn_checkout_book` — checks member eligibility and copy availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an active loan returned and restores inventory availability.
- `fn_late_fee` — calculates overdue charges at 50 cents per day, capped at 2000 cents.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and record checkout and return events.
- `branch`, `book_copy` — represent branches and individually identified copies with availability, loan, or lost status.

#### Data flow
- `fn_checkout_book` validates the member and available inventory, updates book availability, and inserts a `loan`; the audit trigger records checkout.
- `sp_return_book` validates an active loan, records its return date, and restores availability; the trigger records the return, while `fn_late_fee` calculates any overdue charge.
- Reporting views aggregate `loan`, `member`, `book`, and `author` data; `popular_books` summarizes recent lending activity.
- The migration creates a main branch and generates `book_copy` rows from existing book copy counts, then links loans and members to branch-aware inventory.

#### Dependencies
- Reporting and circulation routines read or write `author`, `book`, `member`, `loan`, and `loan_audit`.
- `book.author_id`, `loan.book_id`, and `loan.member_id` link catalog and lending records; member deletion cascades to loans.
- The migration uses `generate_series` and links `book_copy` to `book` and `branch`, `loan` to `book_copy`, and `member` to `branch`.

#### Cross-cutting
- PostgreSQL constraints enforce identifiers, uniqueness, valid inventory counts, loan date ordering, and allowed copy and audit statuses.
- Indexes support author, member, open-loan, book-copy, and materialized popular-book lookups.
- Checkout rejects blocked members and unavailable copies; return rejects unknown or already returned loans.
