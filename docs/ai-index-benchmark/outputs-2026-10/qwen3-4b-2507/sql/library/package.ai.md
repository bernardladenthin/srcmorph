### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:15:52Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages the full lifecycle of a lending library system, from data modeling and availability tracking to loan operations, reporting, and schema evolution with support for branch-specific book copies.

#### Purpose  
- Models core library entities: books, authors, members, and loans, with availability and borrowing constraints.  
- Supports loan checkout, return, late fee calculation, and audit logging.  
- Enables reporting on overdue loans, member activity, and popular books.  
- Evolves schema to support branch-based book copies and centralized availability tracking.

#### Responsibilities  
- Data modeling and schema definition  
- Loan lifecycle management (checkout, return, late fees)  
- Business rule enforcement (availability, due dates, blocking)  
- Reporting and analytics (overdue status, member stats, popularity)  
- Schema migration and data seeding  

#### Key units  
- `author`: Tracks author metadata including name, birth year, and country.  
- `book`: Core book data including title, ISBN, author, and copy counts (pre-migration).  
- `member`: Member profile with email, name, join date, and blocking status.  
- `loan`: Tracks borrowing events with loaned, due, and returned dates.  
- `loan_audit`: Logs checkout and return actions for compliance and traceability.  
- `branch`: Models physical library branches with location and name.  
- `book_copy`: Branch-specific book copies with barcode and status (AVAILABLE, ON_LOAN, LOST).  
- `overdue_loans`, `member_loan_stats`, `popular_books`: Views for reporting and analytics.  

#### Data flow  
- Seed data is inserted into `author`, `book`, `member`, and `loan` tables.  
- Loans are validated for availability and member status before checkout.  
- On checkout, `copies_free` is reduced and a new loan is created with due date.  
- On return, `copies_free` is increased and the loan is marked as returned.  
- Audit logs record all checkout and return events.  
- Overdue loans are calculated by comparing due date to today.  
- Popular books are identified by loan frequency over the past year.  
- Schema migration introduces `branch` and `book_copy`, replacing per-book copy counts with branch-specific tracking.  

#### Dependencies  
- `author`, `book`, `member`, `loan` (core entities)  
- `branch` (introduced in migration)  
- `loan_audit` (for audit logging)  
- `generate_series` (used in barcode/status generation during migration)  
- `current_date`, `now()` (for timestamps and due date logic)  

#### Cross-cutting  
- Business rules enforced via constraints: `copies_free <= copies_total`, `due_on > loaned_on`.  
- Audit logging via triggers and functions for compliance.  
- Data consistency maintained through transactions in seed scripts.  
- Shared views for reporting with consistent joins across `loan`, `member`, `book`, and `author`.  
- Schema evolution handled via migration scripts that alter table structure and relationships.
