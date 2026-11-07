### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:41:49Z
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
- **ResultMap** `customerResult`: maps `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER` to `shop.customer.Customer` fields `id`, `email`, `loyaltyTier`.
- **SQL fragment** `customerColumns`: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.
- **Select** `findByEmail`: retrieves a single customer by `EMAIL`; uses `customerResult`.
- **Select** `search`: retrieves customers filtered by optional `tier` and `createdAfter`; orders by `${sortColumn}`; uses `customerResult`.
- **Insert** `insert`: inserts a new customer; generates key for `id`.
- **Update** `promote`: updates `LOYALTY_TIER` for a list of `ids`.
- **Delete** `deleteInactive`: deletes customers with `LAST_ORDER_AT` before `${cutoff}`.

#### Configuration
- `useGeneratedKeys="true"` on `insert`.
- `keyProperty="id"` on `insert`.
- `order by ${sortColumn}` in `search`.
- `where` clause in `search` uses conditional `<if>` elements.
- `foreach` in `promote` iterates over `ids` collection.

#### Dependencies
- DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`  
- Java class: `shop.customer.Customer` (entity mapped by `customerResult`).
