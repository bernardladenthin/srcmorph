### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:10:56Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Maps customer data between SQL rows and `shop.customer.Customer` objects, providing CRUD and search operations.

#### Purpose
- Define SQL mappings for customer entities.
- Provide queries for lookup, search, insert, update, and delete.

#### Type
- MyBatis mapper XML  
  Root element: `mapper`  
  Namespace: `shop.customer.CustomerMapper`  
  Declared DTD: `mybatis.org/DTD Mapper 3.0`

#### Definitions
- **ResultMap** `customerResult`  
  - `id` → `CUSTOMER_ID`  
  - `result` `email` → `EMAIL`  
  - `result` `loyaltyTier` → `LOYALTY_TIER`
- **SQL fragment** `customerColumns` → `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`
- **Select** `findByEmail`  
  - ResultMap: `customerResult`  
  - SQL: `select <include refid="customerColumns"/> from CUSTOMER where EMAIL = #{email}`
- **Select** `search`  
  - ResultMap: `customerResult`  
  - Conditional clauses: `tier`, `createdAfter`  
  - Order by `${sortColumn}`
- **Insert** `insert`  
  - `useGeneratedKeys="true"`, `keyProperty="id"`  
  - Columns: `EMAIL, LOYALTY_TIER, CREATED_AT` (value `now()`)
- **Update** `promote`  
  - Set `LOYALTY_TIER = #{tier}`  
  - `WHERE CUSTOMER_ID IN (#{id} …)`
- **Delete** `deleteInactive`  
  - Condition: `LAST_ORDER_AT < #{cutoff}`

#### Configuration
- `useGeneratedKeys="true"` on `insert`
- `keyProperty="id"` on `insert`
- `order by ${sortColumn}` in `search`
- `now()` used for `CREATED_AT` in `insert`

#### Dependencies
- SQL table: `CUSTOMER`  
- Columns: `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`  
- Java class: `shop.customer.Customer` (fields: `id`, `email`, `loyaltyTier`)
