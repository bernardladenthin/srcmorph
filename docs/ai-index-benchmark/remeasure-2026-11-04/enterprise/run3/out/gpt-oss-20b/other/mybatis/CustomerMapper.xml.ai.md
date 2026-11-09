### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:23:46Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Maps customer domain objects to SQL statements for CRUD and search operations.

#### Purpose
- Provides MyBatis SQL mappings for `shop.customer.Customer` entities.
- Supports find, search, insert, update, and delete operations.

#### Type
- MyBatis mapper XML
- Root element: `mapper`
- Namespace: `shop.customer.CustomerMapper`

#### Definitions
- **ResultMap** `customerResult`:
  - `id` → `CUSTOMER_ID`
  - `email` → `EMAIL`
  - `loyaltyTier` → `LOYALTY_TIER`
- **SQL fragment** `customerColumns`: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`
- **Select** `findByEmail`:
  - Uses `customerColumns`
  - Filters by `EMAIL = #{email}`
- **Select** `search`:
  - Uses `customerColumns`
  - Conditional `WHERE` clauses:
    - `LOYALTY_TIER = #{tier}` if `tier` not null
    - `CREATED_AT >= #{createdAfter}` if `createdAfter` not null
  - Order by `${sortColumn}`
- **Insert** `insert`:
  - Columns: `EMAIL, LOYALTY_TIER, CREATED_AT`
  - Uses `now()` for `CREATED_AT`
  - Generates key into `id`
- **Update** `promote`:
  - Sets `LOYALTY_TIER = #{tier}`
  - Targets `CUSTOMER_ID` in list `ids` via `<foreach>`
- **Delete** `deleteInactive`:
  - Deletes where `LAST_ORDER_AT < #{cutoff}`

#### Configuration
- `useGeneratedKeys="true"` in `insert`
- `keyProperty="id"` in `insert`
- `open="(" separator="," close="")` in `<foreach>` of `promote`

#### Dependencies
- MyBatis DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`
- Domain class: `shop.customer.Customer` (entity)
- SQL tables: `CUSTOMER` (columns: `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`)
