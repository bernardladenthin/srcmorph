### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:15:54Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Provides customer persistence operations for looking up and filtering customers, inserting records, promoting loyalty tiers, and deleting inactive customers.

#### Purpose
- Defines MyBatis queries and mutations for the `CUSTOMER` table.

#### Responsibilities
- Retrieve customers by email or optional loyalty-tier and creation-date filters.
- Insert customers with generated IDs, promote selected customers, and delete customers with orders before a cutoff date.

#### Key units
- `shop.customer.CustomerMapper` — MyBatis mapper namespace for customer operations.
- `customerResult` — maps `CUSTOMER_ID`, `EMAIL`, and `LOYALTY_TIER` to `shop.customer.Customer`.
- `customerColumns` — shared selection fragment including customer ID, email, loyalty tier, and creation timestamp.
- `findByEmail`, `search` — retrieve customers; `search` supports optional filters and sorts by `${sortColumn}`.
- `insert`, `promote`, `deleteInactive` — insert a customer, update loyalty tiers for a list of IDs, and delete customers by last-order cutoff.

#### Data flow
- Mapper inputs supply lookup values, optional search filters, sort column, insert fields, customer IDs and tier, or an inactivity cutoff.
- Queries use `customerResult` to map rows to `shop.customer.Customer`; mutations write to `CUSTOMER`, with `insert` assigning its generated key to `id`.

#### Dependencies
- `shop.customer.Customer` — result-map target.
- MyBatis mapper DTD — mapper configuration format.

#### Cross-cutting
- `search` conditionally adds tier and creation-date predicates when their parameters are non-null.
- `promote` expands the `ids` collection into a parenthesized, comma-separated list.
- `${sortColumn}` is spliced into SQL unescaped.
