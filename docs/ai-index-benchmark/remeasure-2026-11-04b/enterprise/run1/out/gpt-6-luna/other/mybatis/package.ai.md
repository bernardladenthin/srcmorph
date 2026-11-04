### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:25:02Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Provides database-backed customer lookup, filtering, creation, loyalty-tier promotion, and inactive-customer cleanup.

#### Purpose
- Maps customer operations to SQL on the `CUSTOMER` table.
- Converts query results to `shop.customer.Customer` through `customerResult`.

#### Responsibilities
- **Customer retrieval:** find by email and search with optional loyalty-tier and creation-date filters.
- **Customer maintenance:** insert customers with generated IDs, promote selected customers, and delete customers whose last order predates a cutoff.

#### Key units
- `shop.customer.CustomerMapper` — MyBatis namespace for customer SQL operations.
- `customerResult` — maps `CUSTOMER_ID`, `EMAIL`, and `LOYALTY_TIER` to `Customer` properties.
- `customerColumns` — shared SQL fragment listing customer columns.
- `findByEmail`, `search`, `insert`, `promote`, `deleteInactive` — customer query and mutation statements.

#### Data flow
- Mapper inputs provide lookup criteria, optional search filters and sort column, inserted customer values, promotion IDs, or deletion cutoff.
- SQL operates on `CUSTOMER`; query results use `customerResult`, and `insert` writes the generated key to `id`.

#### Dependencies
- Java type `shop.customer.Customer`.
- Database table `CUSTOMER`, including `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, and `LAST_ORDER_AT`.
- MyBatis mapper DTD.

#### Cross-cutting
- `search` conditionally adds filters when `tier` or `createdAfter` is non-null; `promote` expands `ids` into an `IN` list.
- `search` injects `sortColumn` through unescaped `${}` SQL substitution.
