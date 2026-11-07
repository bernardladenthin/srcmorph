### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:31:33Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Provides database-backed customer lookup, search, creation, promotion, and inactive-customer cleanup for the shop.

#### Purpose
- Maps customer repository operations to SQL against the `CUSTOMER` table.
- Returns customer records and supports customer lifecycle updates.

#### Responsibilities
- Customer retrieval and search: find by email or search with optional loyalty-tier and creation-date filters.
- Customer maintenance: insert customers with generated IDs, promote selected customers, and delete customers with stale orders.

#### Key units
- `shop.customer.CustomerMapper` — MyBatis mapper namespace for customer operations.
- `customerResult` — maps `CUSTOMER_ID`, `EMAIL`, and `LOYALTY_TIER` to `shop.customer.Customer`.
- `customerColumns` — shared projection of customer ID, email, loyalty tier, and creation timestamp.
- `findByEmail`, `search`, `insert`, `promote`, `deleteInactive` — customer lookup, filtered search, insertion, tier update, and inactive-customer deletion statements.

#### Data flow
- Mapper parameters drive SQL predicates and writes; query results use `customerResult` to populate `shop.customer.Customer`.
- `search` applies non-null `tier` and `createdAfter` filters, then orders by the supplied sort column.
- `insert` writes customer details and returns the generated key to `id`; `promote` binds IDs in an `IN` list.

#### Dependencies
- Java type `shop.customer.Customer`.
- Database table `CUSTOMER` and columns `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.
- MyBatis mapper DTD.

#### Cross-cutting
- Reuses the `customerColumns` SQL fragment in both select statements.
- `search` interpolates `${sortColumn}` directly into SQL without escaping.
