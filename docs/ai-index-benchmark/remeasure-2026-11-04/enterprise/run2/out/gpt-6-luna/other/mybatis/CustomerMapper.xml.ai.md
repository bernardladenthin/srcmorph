### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:14:21Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Defines MyBatis customer queries and mutations for the `CUSTOMER` table, including dynamic search and bulk promotion.

#### Type
- MyBatis mapper; root element `mapper`; no XML namespaces declared.
- Mapper namespace: `shop.customer.CustomerMapper`.

#### Definitions
- Result map `customerResult` → `shop.customer.Customer`: `id` ← `CUSTOMER_ID`, `email` ← `EMAIL`, `loyaltyTier` ← `LOYALTY_TIER`.
- SQL fragment `customerColumns`: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.
- `select` `findByEmail`: selects the shared columns from `CUSTOMER` by `EMAIL = #{email}`; result map `customerResult`.
- `select` `search`: selects the shared columns from `CUSTOMER`; optional filters for `tier` and `createdAfter`; orders by `${sortColumn}`; result map `customerResult`.
- `insert` `insert`: writes `EMAIL`, `LOYALTY_TIER`, and `CREATED_AT` (`now()`); generated keys enabled, assigned to `id`.
- `update` `promote`: sets `LOYALTY_TIER = #{tier}` for `CUSTOMER_ID` values from `ids`.
- `delete` `deleteInactive`: deletes from `CUSTOMER` where `LAST_ORDER_AT < #{cutoff}`.
- `${sortColumn}` is spliced into SQL unescaped.
- Commented-out `findAll` is not active.

#### Configuration
- `search`: includes `LOYALTY_TIER = #{tier}` when `tier != null`; includes `CREATED_AT >= #{createdAfter}` when `createdAfter != null`.
- `insert`: `useGeneratedKeys="true"`, `keyProperty="id"`.
- `promote`: `foreach` uses `collection="ids"`, `item="id"`, parentheses delimiters, comma separator.

#### Dependencies
- Java class `shop.customer.Customer`.
- MyBatis mapper DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.
