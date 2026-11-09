### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:12:12Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Provides MyBatis SQL mappings for CRUD and search operations on customer data in the shop domain.

#### Purpose
- Define SQL mappings for `shop.customer.Customer` entities.
- Expose queries for lookup, search, insert, update, and delete.

#### Responsibilities
- **Data Access Layer**: Translate between SQL rows and `Customer` objects.
- **Query Composition**: Build dynamic SQL for filtering and ordering.
- **Key Generation**: Handle auto‑generated primary keys on insert.

#### Key units
- **Mapper XML** `shop.customer.CustomerMapper`  
  - Namespace: `shop.customer.CustomerMapper`
- **ResultMap** `customerResult`  
  - Maps `CUSTOMER_ID` → `id`, `EMAIL` → `email`, `LOYALTY_TIER` → `loyaltyTier`
- **SQL Fragment** `customerColumns`  
  - `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`
- **Select** `findByEmail`  
  - Retrieves a single customer by email.
- **Select** `search`  
  - Dynamic filtering by `tier` and `createdAfter`; orders by `${sortColumn}`.
- **Insert** `insert`  
  - Inserts a new customer, uses `now()` for `CREATED_AT`, returns generated `id`.
- **Update** `promote`  
  - Batch updates `LOYALTY_TIER` for given customer IDs.
- **Delete** `deleteInactive`  
  - Removes customers whose `LAST_ORDER_AT` is older than a cutoff.

#### Data flow
1. **Input**: Method parameters (`email`, `tier`, `createdAfter`, `sortColumn`, `id`, `cutoff`).
2. **Processing**: MyBatis constructs SQL using fragments, dynamic tags, and parameter placeholders.
3. **Execution**: SQL runs against the `CUSTOMER` table.
4. **Output**: Result maps populate `shop.customer.Customer` objects or affect row counts.

#### Dependencies
- **Database**: Table `CUSTOMER` with columns `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.
- **Java**: Class `shop.customer.Customer` (fields `id`, `email`, `loyaltyTier`).
- **MyBatis**: Uses `useGeneratedKeys="true"` and `keyProperty="id"` for inserts.

#### Cross-cutting
- **Dynamic SQL**: Uses `<if>`, `<where>`, and `<choose>` tags for optional clauses.
- **Parameter Binding**: `#{}` for safe binding; `${}` for dynamic column names.
- **Result Mapping**: Single `ResultMap` reused across selects.
- **Fragment Inclusion**: `<include refid="customerColumns"/>` promotes DRYness.
