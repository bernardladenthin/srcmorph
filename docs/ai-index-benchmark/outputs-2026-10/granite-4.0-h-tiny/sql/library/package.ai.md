### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T00:10:27Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL-based lending library schema that provides relational models for authors, books, members, and loan transactions, enforces business rules (free‑copy limits, loan validity), and includes reporting views and audit logging.

#### Purpose
- Supplies relational tables (`author`, `book`, `member`, `loan`) with constraints and relationships for a library system.
- Offers reporting views (`overdue_loans`, `member_loan_stats`, `popular_books`) for overdue loans, member statistics, and book popularity.
- Includes audit logging (`loan_audit`) and migration scripts (`migration_v2.sql`) to evolve the schema.

#### Responsibilities
- **Author**: `author_id` auto‑generated, `full_name` required, optional birth year and country.
- **Book**: `isbn` unique, `copies_total` default 1, `copies_free` default 1, enforced `copies_free <= copies_total`.
- **Member**: `email` unique, `joined_on` defaults to today, `is_blocked` default FALSE.
- **Loan**: `loaned_on` defaults to today, `due_on` > `loaned_on`, `returned_on` nullable.
- **Loan Audit**: Tracks `CHECKOUT` and `RETURN` actions with timestamps.

#### Key Units
- **author**: `author_id` (PK), `full_name` (required), `birth_year`, `country_code`.
- **book**: `isbn` (PK), `copies_total`, `copies_free` (derived), `author_id` (FK).
- **member**: `member_id` (PK), `email` (PK), `joined_on`, `is_blocked`.
- **loan**: `loan_id` (PK), `book_id` (FK), `member_id` (FK, cascade delete), `loaned_on`, `due_on`, `returned_on`.
- **loan_audit**: `audit_id` (PK), `loan_id` (FK), `action` (`CHECKOUT`/`RETURN`), `changed_at`.

#### Data Flow
1. **INSERT** into `author`, `book`, `member` populates core entities.
2. **INSERT** into `loan` creates loan records linked to `book` and `member`.
3. **UPDATE** to `book` adjusts `copies_free` after loan/check‑out.
4. **INSERT** into `loan_audit` logs each `CHECKOUT`/`RETURN`.
5. **VIEW** queries aggregate data from `loan`, `member`, `book`.

#### Dependencies
- Relies on `author`, `book`, `member`, `loan` tables.
- Uses PostgreSQL-specific features (`SERIAL`, `GENERATE_SERIES`, `LATERAL`, `CREATE INDEX`).

#### Cross‑cutting
- Enforces business rules via `CHECK` constraints.
- Audit table provides immutable logging.
- Migration script (`migration_v2.sql`) updates existing schema without data loss.
