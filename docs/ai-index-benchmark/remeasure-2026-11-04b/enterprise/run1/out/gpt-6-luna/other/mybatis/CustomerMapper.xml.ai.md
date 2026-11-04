### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:23:30Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Defines customer database operations for lookup, conditional search, insertion, promotion, and deletion.

#### Purpose
- Maps SQL operations on `CUSTOMER` to the `shop.customer.CustomerMapper` namespace.
- Maps customer query columns to `shop.customer.Customer` properties.

#### Type
- MyBatis mapper; root element `mapper`; no XML namespace declared.
- DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.

#### Definitions
- Namespace: `shop.customer.CustomerMapper`.
- Result map `customerResult` → `shop.customer.Customer`: `id` from `CUSTOMER_ID`, `email` from `EMAIL`, `loyaltyTier` from `LOYALTY_TIER`.
- SQL fragment `customerColumns`: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.
- Select `findByEmail`: uses `customerResult`; reads `CUSTOMER`, filters by `EMAIL = #{email}`; parameter and result types not explicitly declared.
- Select `search`: uses `customerResult`; reads `CUSTOMER`; optional filters on `tier` and `createdAfter`; orders by `${sortColumn}`; parameter and result types not explicitly declared.
- Insert `insert`: writes `CUSTOMER`; inserts `EMAIL`, `LOYALTY_TIER`, and `CREATED_AT` (`now()`); generated keys enabled with `keyProperty="id"`.
- Update `promote`: updates `CUSTOMER.LOYALTY_TIER` for `CUSTOMER_ID` values from `ids`; parameter and result types not explicitly declared.
- Delete `deleteInactive`: deletes from `CUSTOMER` where `LAST_ORDER_AT < #{cutoff}`; parameter and result types not explicitly declared.
- `${sortColumn}` is spliced into SQL unescaped.

#### Configuration
- `search`: adds `LOYALTY_TIER = #{tier}` when `tier != null`; adds `CREATED_AT >= #{createdAfter}` when `createdAfter != null`.
- `search`: sort column supplied by `${sortColumn}`.
- `insert`: `useGeneratedKeys="true"`; `keyProperty="id"`.
- `promote`: `ids` expanded as a comma-separated `IN` list.

#### Dependencies
- Java type: `shop.customer.Customer`.
- Database table: `CUSTOMER`; columns referenced include `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, and `LAST_ORDER_AT`.
- MyBatis mapper DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.
