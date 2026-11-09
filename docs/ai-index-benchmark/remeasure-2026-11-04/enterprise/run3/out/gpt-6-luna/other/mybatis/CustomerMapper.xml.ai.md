### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:20:33Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Defines MyBatis customer data access for retrieving, searching, inserting, promoting, and deleting customer records.

#### Purpose
- Maps customer SQL operations to `shop.customer.CustomerMapper`.
- Maps query results to `shop.customer.Customer`.

#### Type
- MyBatis mapper; root element `mapper`.
- Declares no XML namespaces; uses the MyBatis Mapper 3.0 DTD.

#### Definitions
- Namespace: `shop.customer.CustomerMapper`.
- Result map `customerResult` → `shop.customer.Customer`: `id` from `CUSTOMER_ID`, `email` from `EMAIL`, `loyaltyTier` from `LOYALTY_TIER`.
- SQL fragment `customerColumns`: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.
- Select `findByEmail`: selects `customerColumns` from `CUSTOMER` where `EMAIL = #{email}`; result map `customerResult`.
- Select `search`: selects `customerColumns` from `CUSTOMER`; conditionally filters by `tier` and `createdAfter`; orders by `${sortColumn}`; result map `customerResult`.
- Insert `insert`: inserts `EMAIL`, `LOYALTY_TIER`, `CREATED_AT` into `CUSTOMER`; uses generated keys for `id`.
- Update `promote`: sets `LOYALTY_TIER` to `#{tier}` for `CUSTOMER_ID` values in `ids`.
- Delete `deleteInactive`: deletes from `CUSTOMER` where `LAST_ORDER_AT < #{cutoff}`.
- `${sortColumn}` is spliced into SQL unescaped.
- Commented-out `findAll` is not active.

#### Configuration
- `insert`: `useGeneratedKeys="true"`; `keyProperty="id"`.
- `search`: `tier != null` adds a loyalty-tier condition; `createdAfter != null` adds a creation-time lower bound.
- `promote`: `ids` supplies the `IN` list; `tier` supplies the new loyalty tier.
- `search` ordering uses `${sortColumn}`.

#### Dependencies
- Mapper namespace/package: `shop.customer.CustomerMapper`.
- Result class: `shop.customer.Customer`.
- Database table: `CUSTOMER`; columns include `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.
- MyBatis Mapper 3.0 DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.
