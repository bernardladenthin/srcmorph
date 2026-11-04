### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:26:30Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Defines MyBatis SQL mappings for the `shop.customer.CustomerMapper` namespace, including CRUD operations and dynamic queries for customer data.

#### Purpose
- Maps SQL statements to Java methods for customer persistence.
- Provides dynamic filtering and ordering in search queries.

#### Type
- MyBatis mapper XML
- Root element: `mapper`
- Namespace: `shop.customer.CustomerMapper`
- Declares DTD: `mybatis-3-mapper.dtd`

#### Definitions
- **ResultMap** `customerResult` (type `shop.customer.Customer`):
  - `id` → `CUSTOMER_ID`
  - `email` → `EMAIL`
  - `loyaltyTier` → `LOYALTY_TIER`
- **SQL fragment** `customerColumns`: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`
- **Select** `findByEmail`:
  - Uses `customerResult`
  - Query: `select <include refid="customerColumns"/> from CUSTOMER where EMAIL = #{email}`
- **Select** `search`:
  - Uses `customerResult`
  - Dynamic `WHERE` clauses:
    - `LOYALTY_TIER = #{tier}` if `tier` not null
    - `CREATED_AT >= #{createdAfter}` if `createdAfter` not null
  - Order by `${sortColumn}`
- **Insert** `insert`:
  - `useGeneratedKeys="true"`, `keyProperty="id"`
  - Query: `insert into CUSTOMER (EMAIL, LOYALTY_TIER, CREATED_AT) values (#{email}, #{loyaltyTier}, now())`
- **Update** `promote`:
  - Sets `LOYALTY_TIER = #{tier}`
  - `WHERE CUSTOMER_ID in (#{id}, …)` via `<foreach>`
- **Delete** `deleteInactive`:
  - `WHERE LAST_ORDER_AT < #{cutoff}`

#### Configuration
- `useGeneratedKeys="true"` on `insert`
- `keyProperty="id"` on `insert`
- `open="("`, `separator=","`, `close=")"` on `<foreach>` in `promote`

#### Dependencies
- Java class `shop.customer.Customer` (result type)
- SQL table `CUSTOMER`
- Columns: `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`
