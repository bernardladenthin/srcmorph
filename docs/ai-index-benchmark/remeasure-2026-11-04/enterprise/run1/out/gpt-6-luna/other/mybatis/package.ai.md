### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:09:21Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Supports customer record retrieval, creation, loyalty-tier promotion, and inactive-customer cleanup in the `CUSTOMER` table.

#### Purpose
- Maps customer database rows to `shop.customer.Customer` and provides customer queries and mutations.
- Supports email lookup, optional-filter search, insertion, bulk promotion, and deletion by last-order cutoff.

#### Responsibilities
- **Customer reads:** `findByEmail` retrieves by email; `search` applies optional loyalty-tier and creation-date filters and sorts results.
- **Customer writes and cleanup:** `insert` creates a customer with the current timestamp; `promote` updates loyalty tiers for a set of customer IDs; `deleteInactive` removes customers whose last order predates a cutoff.
- **Row mapping and shared SQL:** `customerResult` maps customer columns; `customerColumns` defines a reusable column list.

#### Key units
- `shop.customer.CustomerMapper` — MyBatis mapper namespace for customer operations.
- `customerResult` — Maps `CUSTOMER_ID`, `EMAIL`, and `LOYALTY_TIER` to `shop.customer.Customer`.
- `customerColumns` — SQL fragment listing `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, and `CREATED_AT`.
- `findByEmail`, `search`, `insert`, `promote`, `deleteInactive` — Customer select, insert, update, and delete statements.

#### Data flow
- Queries read from `CUSTOMER` and map results through `customerResult`.
- `search` conditionally filters on `LOYALTY_TIER` and `CREATED_AT`; its sort column is inserted using `${sortColumn}`.
- `insert` writes email and loyalty tier, sets `CREATED_AT` to `now()`, and returns the generated key to `id`.
- `promote` renders the supplied `ids` collection into an `IN` list; `deleteInactive` uses `cutoff` against `LAST_ORDER_AT`.

#### Dependencies
- Database table: `CUSTOMER`; referenced columns include `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, and `LAST_ORDER_AT`.
- Java type: `shop.customer.Customer`.
- MyBatis mapper DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.

#### Cross-cutting
- `search` uses dynamic SQL for optional filters.
- `${sortColumn}` is spliced into SQL unescaped.
- `insert` uses MyBatis generated keys with `keyProperty="id"`.
