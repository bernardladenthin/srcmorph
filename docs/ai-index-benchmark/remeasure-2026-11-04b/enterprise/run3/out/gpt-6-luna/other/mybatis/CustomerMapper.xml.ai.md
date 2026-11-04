### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:38:44Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Defines customer lookup, search, insertion, promotion, and inactive-customer deletion operations for the shop customer mapper.

#### Type
- MyBatis mapper; root element `mapper`; namespace `shop.customer.CustomerMapper`.

#### Definitions
- Result map `customerResult` maps `shop.customer.Customer`: `id` from `CUSTOMER_ID` (id), `email` from `EMAIL`, `loyaltyTier` from `LOYALTY_TIER`.
- SQL fragment `customerColumns`: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.
- Select `findByEmail`: reads `CUSTOMER`, includes `customerColumns`, filters by `EMAIL = #{email}`, uses `customerResult`.
- Select `search`: reads `CUSTOMER`, includes `customerColumns`, conditionally filters by `LOYALTY_TIER = #{tier}` and `CREATED_AT >= #{createdAfter}`, orders by `${sortColumn}`, uses `customerResult`.
- Insert `insert`: writes `CUSTOMER`; inserts `EMAIL`, `LOYALTY_TIER`, and `CREATED_AT` (`now()`); generated keys enabled with `keyProperty="id"`.
- Update `promote`: updates `LOYALTY_TIER` for `CUSTOMER_ID` values from `ids`.
- Delete `deleteInactive`: deletes from `CUSTOMER` where `LAST_ORDER_AT < #{cutoff}`.
- `${sortColumn}` is spliced into SQL unescaped.

#### Dependencies
- Java type `shop.customer.Customer`.
- MyBatis mapper DTD at `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.
- Database table `CUSTOMER`; columns `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.
