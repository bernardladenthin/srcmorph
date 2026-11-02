### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:44:39Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL data model and procedural toolkit for managing a multi‑branch lending library, including schema, reporting views, business logic for check‑outs and returns, and migration to a copy‑based inventory system.  

#### Purpose  
- Define core library entities (authors, books, members, loans).  
- Provide reporting views for overdue loans, member activity, and popular books.  
- Implement checkout, return, late‑fee calculation, and audit logging.  
- Migrate to a branch‑aware copy inventory model.  

#### Responsibilities  
- **Schema definition** – tables, constraints, indexes for authors, books, members, loans.  
- **Reporting** – views (`overdue_loans`, `member_loan_stats`) and materialized view (`popular_books`).  
- **Business logic** – functions (`fn_checkout_book`, `fn_late_fee`), procedure (`sp_return_book`), audit trigger (`trg_loan_audit`).  
- **Migration** – new tables (`branch`, `book_copy`), altered columns, and index (`idx_book_copy_book`).  
- **Seeding** – bulk inserts for authors, books, members, and loans.  

#### Key units  
- **Tables**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
- **Views**: `overdue_loans`, `member_loan_stats`, `popular_books`.  
- **Functions**: `fn_checkout_book(member_id, book_id, days) → loan_id`; `fn_late_fee(due_on, returned_on) → cents`.  
- **Procedure**: `sp_return_book(loan_id)`.  
- **Trigger**: `trg_loan_audit` on `loan` invoking `trg_loan_audit_fn`.  

#### Data flow  
1. **Checkout** – `fn_checkout_book` validates member, decrements `book.copies_free`, inserts `loan`, logs audit.  
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, logs audit.  
3. **Late fee** – `fn_late_fee` calculates fee based on `due_on` and `returned_on`.  
4. **Reporting** – Views aggregate `loan`, `member`, `book`, `author` data for overdue status, member stats, and popularity.  
5. **Migration** – `04_migration_v2.sql` replaces book‑level copy counts with `book_copy` rows linked to `branch`; updates `loan` and `member` to reference copies and home branches.  

#### Dependencies  
- **Internal**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
- **External**: PostgreSQL built‑in types, sequences, and standard functions (`now()`, `CURRENT_DATE`).  
- **Indexes**: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`, `idx_book_copy_book`.  

#### Cross‑cutting  
- Consistent use of foreign keys and `ON DELETE CASCADE` for loan/member relationships.  
- Central audit trail via `loan_audit` and trigger logic.  
- Constraint checks enforce data integrity (e.g., `copies_free ≤ copies_total`, `due_on > loaned_on`).  
- Migration removes legacy copy counters, introducing a normalized copy table with status tracking.  
- Seed script provides baseline data for development and testing.
