### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:30:01Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Defines customer database mappings and CRUD/search operations for the shop customer repository.

#### Type
- MyBatis mapper; root element `mapper`; namespace `shop.customer.CustomerMapper`.

#### Definitions
- Result map `customerResult` maps `shop.customer.Customer`: `id` → `CUSTOMER_ID`, `email` → `EMAIL`, `loyaltyTier` → `LOYALTY_TIER`.
- SQL fragment `customerColumns`: `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`.
- `select` `findByEmail`: selects the shared customer columns from `CUSTOMER`, matching `EMAIL` to `#{email}`; uses `customerResult`.
- `select` `search`: selects the shared columns from `CUSTOMER`; conditionally filters by `tier` and `createdAfter`; orders by `${sortColumn}`; uses `customerResult`.
- `insert` `insert`: writes `EMAIL`, `LOYALTY_TIER`, and `CREATED_AT` to `CUSTOMER`; uses `#{email}`, `#{loyaltyTier}`, and `now()`; returns generated key to `id`.
- `update` `promote`: updates `CUSTOMER.LOYALTY_TIER` to `#{tier}` for IDs in `ids`.
- `delete` `deleteInactive`: deletes from `CUSTOMER` where `LAST_ORDER_AT` is before `#{cutoff}`.
- `${sortColumn}` is spliced into the search SQL unescaped.

#### Configuration
- `insert`: `useGeneratedKeys="true"`; `keyProperty="id"`.
- `search`: `tier` and `createdAfter` filters apply only when their values are non-null; sort column is supplied by `sortColumn`.
- `promote`: `ids` values are bound as a comma-separated `IN` list.

#### Dependencies
- Java type `shop.customer.Customer`.
- Database table `CUSTOMER`; columns `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.
- MyBatis mapper DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.
