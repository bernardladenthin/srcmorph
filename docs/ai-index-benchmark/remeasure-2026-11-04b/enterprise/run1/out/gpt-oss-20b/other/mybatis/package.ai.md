### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:27:40Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Provides MyBatis SQL mappings for customer persistence, enabling CRUD operations and dynamic queries on the `CUSTOMER` table.

#### Purpose
- Map SQL statements to Java methods for customer data access.
- Offer dynamic filtering and ordering in customer search.

#### Responsibilities
- **Data Access Layer**: CRUD operations for `shop.customer.Customer`.
- **Dynamic Query Builder**: Conditional `WHERE` clauses and ordering in search.
- **Batch Operations**: Promote multiple customers in a single statement.

#### Key units
- **Mapper** `shop.customer.CustomerMapper` – interface backed by `CustomerMapper.xml`.
- **ResultMap** `customerResult` – maps `CUSTOMER` columns to `shop.customer.Customer` fields.
- **SQL Fragment** `customerColumns` – reusable column list for selects/updates.
- **Select** `findByEmail` – fetch a customer by email.
- **Select** `search` – dynamic search with optional tier, creation date, and sort column.
- **Insert** `insert` – add a new customer, auto‑generating `CUSTOMER_ID`.
- **Update** `promote` – batch update loyalty tier for a list of IDs.
- **Delete** `deleteInactive` – remove customers inactive before a cutoff date.

#### Data flow
1. **Input**: Method parameters (`email`, `tier`, `createdAfter`, `sortColumn`, `id` list, `cutoff`).
2. **Mapper XML**: Translates parameters into SQL via placeholders (`#{}`) and fragments.
3. **Execution**: MyBatis runs the SQL against the `CUSTOMER` table.
4. **Output**: Result objects (`shop.customer.Customer`) or affected row counts.

#### Dependencies
- **Java**: `shop.customer.Customer` (result type).
- **Database**: `CUSTOMER` table with columns `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.
- **MyBatis**: XML mapper runtime, DTD `mybatis-3-mapper.dtd`.

#### Cross-cutting
- **Generated Keys**: `useGeneratedKeys="true"` and `keyProperty="id"` for inserts.
- **Dynamic SQL**: `<if>`, `<foreach>`, and `${}` for runtime query composition.
- **Result Mapping**: Single `ResultMap` reused across selects.
- **Fragment Inclusion**: `<include>` for column lists, promoting DRYness.
