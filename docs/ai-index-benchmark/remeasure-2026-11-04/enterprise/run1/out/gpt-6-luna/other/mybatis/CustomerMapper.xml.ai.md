### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:07:53Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Defines MyBatis customer queries and mutations for the `CUSTOMER` table.

#### Purpose
- Maps customer rows to `shop.customer.Customer`.
- Provides email lookup, conditional search, insertion, promotion, and inactive-customer deletion.

#### Type
- MyBatis mapper; root element `mapper`; namespace `shop.customer.CustomerMapper`.
- No XML namespaces declared.

#### Definitions
- Result map `customerResult` → `shop.customer.Customer`: `id` ← `CUSTOMER_ID` (ID), `email` ← `EMAIL`, `loyaltyTier` ← `LOYALTY_TIER`.
- SQL fragment `customerColumns` → `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.
- Select `findByEmail`: reads `CUSTOMER`; parameter `email`; result map `customerResult`.
- Select `search`: reads `CUSTOMER`; parameters `tier`, `createdAfter`, `sortColumn`; result map `customerResult`; optional filters on `LOYALTY_TIER` and `CREATED_AT`, ordered by `sortColumn`.
- Insert `insert`: writes `CUSTOMER` (`EMAIL`, `LOYALTY_TIER`, `CREATED_AT`); parameters `email`, `loyaltyTier`; uses generated keys for `id`.
- Update `promote`: updates `CUSTOMER.LOYALTY_TIER` for `CUSTOMER_ID` values in `ids`; parameters `tier`, `ids`.
- Delete `deleteInactive`: deletes from `CUSTOMER` where `LAST_ORDER_AT` precedes `cutoff`; parameter `cutoff`.
- `${sortColumn}` is spliced into the SQL unescaped.

#### Configuration
- `insert`: `useGeneratedKeys="true"`; `keyProperty="id"`.
- `search`: applies the `tier` condition when `tier != null`; applies the `createdAfter` condition when `createdAfter != null`; sorts by `${sortColumn}`.
- `promote`: `ids` collection rendered with parentheses and comma separators.
- `insert`: sets `CREATED_AT` to `now()`.

#### Dependencies
- Mapper DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.
- Java type: `shop.customer.Customer`.
- Database table: `CUSTOMER`; columns referenced include `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.
