### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:22:04Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Provides database access for customer records, including lookup, filtered search, loyalty-tier updates, insertion, and inactive-customer deletion.

#### Purpose
- Maps customer operations to SQL for the `CUSTOMER` table.
- Converts query results to `shop.customer.Customer`.

#### Responsibilities
- Retrieve customers by email or search with optional loyalty-tier and creation-time filters.
- Insert customers with generated IDs, promote customers to a specified loyalty tier, and delete customers based on last-order cutoff.

#### Key units
- `shop.customer.CustomerMapper` — MyBatis mapper namespace for customer data operations.
- `customerResult` — maps `CUSTOMER_ID`, `EMAIL`, and `LOYALTY_TIER` to `Customer` properties.
- `customerColumns` — shared SQL fragment selecting customer ID, email, loyalty tier, and creation time.
- `findByEmail`, `search`, `insert`, `promote`, `deleteInactive` — customer lookup, search, insert, tier update, and deletion statements.

#### Data flow
- Mapper inputs provide email, optional search filters and sort column, insert values, customer IDs and tier for promotion, or a deletion cutoff.
- SQL statements operate on `CUSTOMER`; select results use `customerResult`, and inserts return generated keys to `id`.

#### Dependencies
- `shop.customer.Customer` result class and `shop.customer.CustomerMapper` namespace.
- `CUSTOMER` table, including `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, and `LAST_ORDER_AT`.
- MyBatis Mapper 3.0 DTD.

#### Cross-cutting
- `search` uses dynamic SQL for optional filters and orders by `${sortColumn}`, which is spliced into SQL unescaped.
