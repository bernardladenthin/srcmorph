### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:43:36Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending library database with sample authors, books, members, and loan transactions to establish a baseline dataset for development and testing.

#### Purpose
- Populate the lending library schema with deterministic fixture data for testing and demos
- Establish referential baseline: catalog entries (authors/books), patrons (members), and circulation history (loans)

#### Type
- DML seed data script (PostgreSQL dialect)
- Single transaction block (`BEGIN;` … `COMMIT;`)

#### Writes
- `author` — columns `author_id`, `full_name`, `birth_year`, `country_code`; 15 rows (IDs 1–15)
- `book` — columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`; 110 rows (IDs 1–110); `copies_total` fixed at 2, `copies_free` ranges 0–2
- `member` — columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`; 50 rows (IDs 1–50); `joined_on` computed as `DATE '2020-01-01' + offset`; `is_blocked` TRUE for member IDs 13 and 15
- `loan` — columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`; 150 rows (IDs 1–150); dates computed as `DATE '2021-01-01' + offset`; `returned_on` NULL for open loans, populated for closed loans

#### Relationships
- `book.author_id` -> `author.author_id`
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`
- Generator script: `docs/ai-index-benchmark/tools/generate-seed-sql.py`
