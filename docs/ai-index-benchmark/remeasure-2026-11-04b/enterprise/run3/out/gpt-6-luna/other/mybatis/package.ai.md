### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:40:13Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Supports customer lookup, filtering, creation, loyalty-tier promotion, and cleanup of inactive shop customers.

#### Purpose
- Provides MyBatis database operations for the shop’s `CUSTOMER` records.
- Maps query results to `shop.customer.Customer`.

#### Responsibilities
- Customer retrieval: find by email or search with optional loyalty-tier and creation-date filters.
- Customer lifecycle updates: insert customers, promote loyalty tiers for selected IDs, and delete customers with an old last-order date.

#### Key units
- `shop.customer.CustomerMapper` — mapper namespace for customer database operations.
- `customerResult` — maps `CUSTOMER_ID`, `EMAIL`, and `LOYALTY_TIER` to `Customer` properties.
- `customerColumns` — shared column list used by customer selects.
- `findByEmail`, `search`, `insert`, `promote`, `deleteInactive` — lookup, search, insertion, promotion, and deletion statements.

#### Data flow
- Selects read from `CUSTOMER`, reuse `customerColumns`, and map results through `customerResult`.
- `search` adds optional filters for tier and creation date, then orders by the supplied sort column.
- Writes insert customer details with `CREATED_AT` set to `now()` and generated keys assigned to `id`; promotion and deletion act on supplied IDs or cutoff dates.

#### Dependencies
- Java type `shop.customer.Customer`.
- Database table `CUSTOMER` and columns `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.
- MyBatis mapper DTD.

#### Cross-cutting
- Dynamic SQL supports optional search filters; `${sortColumn}` is inserted into SQL unescaped.
