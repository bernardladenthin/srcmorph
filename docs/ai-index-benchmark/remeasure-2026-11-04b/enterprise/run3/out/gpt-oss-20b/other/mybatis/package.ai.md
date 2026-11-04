### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:42:58Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Provides SQL mapping for customer entities, enabling CRUD and search operations via MyBatis.

#### Purpose
- Exposes database operations for `shop.customer.Customer` objects.
- Supports creation, retrieval, update, deletion, and filtered search.

#### Responsibilities
- **Data persistence**: Insert, update, delete customer records.
- **Querying**: Retrieve by email, search with optional filters and dynamic ordering.
- **Batch operations**: Promote multiple customers to a higher loyalty tier.

#### Key units
- **Mapper namespace** `shop.customer.CustomerMapper` – central MyBatis configuration.
- **ResultMap** `customerResult` – maps `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER` to `shop.customer.Customer` fields.
- **SQL fragment** `customerColumns` – reusable column list for SELECT/INSERT.
- **Select** `findByEmail` – single‑row lookup by email.
- **Select** `search` – multi‑row lookup with optional `tier` and `createdAfter` filters, dynamic `order by`.
- **Insert** `insert` – creates a customer, returns generated `id`.
- **Update** `promote` – batch updates `LOYALTY_TIER` for a list of IDs.
- **Delete** `deleteInactive` – removes customers whose last order predates a cutoff.

#### Data flow
1. **Input**: Method parameters (`email`, filter criteria, list of IDs, cutoff date, sort column).
2. **Processing**: MyBatis builds SQL using the defined fragments, dynamic `<if>` and `<foreach>` tags.
3. **Execution**: SQL runs against the database; results mapped via `customerResult`.
4. **Output**: Java objects (`Customer`) or affected row counts.

#### Dependencies
- **External**: MyBatis runtime, database driver.
- **Internal**: Java entity `shop.customer.Customer`.
- **DTD**: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.

#### Cross-cutting
- Dynamic SQL: `<if>` for optional predicates, `${}` for column ordering, `<foreach>` for batch IDs.
- Generated keys: `useGeneratedKeys="true"`, `keyProperty="id"` on insert.
- Reusable column list via `customerColumns` fragment.
