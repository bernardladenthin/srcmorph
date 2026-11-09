### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:35:13Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Maps customer data to SQL operations for the `shop.customer.CustomerMapper` namespace.

#### Purpose
- Provides CRUD and search operations for `shop.customer.Customer` entities.

#### Type
- MyBatis mapper XML  
  Root element: `mapper`  
  Namespace: `shop.customer.CustomerMapper`  
  Declared DTD: `mybatis-3-mapper.dtd`

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
  - SQL: `select <include refid="customerColumns"/> from CUSTOMER`  
  - Conditional clauses:  
    - `LOYALTY_TIER = #{tier}` if `tier != null`  
    - `and CREATED_AT >= #{createdAfter}` if `createdAfter != null`  
  - Order by `${sortColumn}`
- **Insert** `insert`  
  - `useGeneratedKeys="true"`  
  - `keyProperty="id"`  
  - SQL: `insert into CUSTOMER (EMAIL, LOYALTY_TIER, CREATED_AT) values (#{email}, #{loyaltyTier}, now())`
- **Update** `promote`  
  - SQL: `update CUSTOMER set LOYALTY_TIER = #{tier} where CUSTOMER_ID in (#{id}, …)`
- **Delete** `deleteInactive`  
  - SQL: `delete from CUSTOMER where LAST_ORDER_AT < #{cutoff}`

#### Configuration
- `useGeneratedKeys="true"` on `insert`  
- `keyProperty="id"` on `insert`  
- `order by ${sortColumn}` in `search` (parameterized column name)

#### Dependencies
- SQL tables: `CUSTOMER`  
- Columns referenced: `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`  
- Java class: `shop.customer.Customer` (used in `resultMap` type)
