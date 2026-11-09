### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:36:51Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Provides SQL mapping for customer data persistence and querying in a shop application.

#### Purpose
- Exposes CRUD and search operations for `shop.customer.Customer` entities via MyBatis.

#### Responsibilities
- **Data Access Layer**: maps SQL statements to Java methods for customer CRUD.
- **Query Building**: supports dynamic filtering and ordering in search queries.
- **Result Mapping**: translates database rows to `shop.customer.Customer` objects.

#### Key units
- **Mapper XML** `shop.customer.CustomerMapper`  
  - Namespace for all customer SQL operations.
- **ResultMap** `customerResult`  
  - Maps `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER` to `shop.customer.Customer` fields.
- **SQL Fragment** `customerColumns`  
  - Reusable column list: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.
- **Select** `findByEmail`  
  - Retrieves a single customer by email.
- **Select** `search`  
  - Dynamic search with optional `tier`, `createdAfter`, and sortable column.
- **Insert** `insert`  
  - Persists a new customer, auto‑generates `CUSTOMER_ID`.
- **Update** `promote`  
  - Batch updates loyalty tier for given customer IDs.
- **Delete** `deleteInactive`  
  - Removes customers whose last order predates a cutoff date.

#### Data flow
1. **Input**: method parameters (`email`, `tier`, `createdAfter`, `sortColumn`, `id`, `cutoff`).
2. **Mapper**: MyBatis translates parameters into SQL via `<include>`, `<if>`, and `${}` placeholders.
3. **Execution**: SQL runs against `CUSTOMER` table.
4. **Output**: Result rows mapped to `shop.customer.Customer` objects or affected row counts.

#### Dependencies
- **Database**: `CUSTOMER` table with columns `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.
- **Java**: `shop.customer.Customer` class used in `customerResult`.
- **MyBatis**: XML mapper, DTD `mybatis-3-mapper.dtd`, dynamic SQL tags.

#### Cross-cutting
- **Dynamic SQL**: `<if>` conditions for optional filters; `${sortColumn}` for runtime ordering.
- **Key Generation**: `useGeneratedKeys="true"` with `keyProperty="id"` for inserts.
- **Fragment Reuse**: `customerColumns` included in multiple statements to avoid duplication.
