### CustomerMapper.xml
- H: 1.0
- C: F9CCC6BC
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:17:31Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 12173379
---
**Facts (exact, whole file):** mapper namespaces: 1; select statements: 2; insert, update and delete statements: 3; result maps: 1; SQL fragments: 1; fragment includes: 2; dynamic SQL tags: 5; parameters #{}: 8; substitutions ${}: 1

> Defines MyBatis SQL mappings for the `shop.customer.CustomerMapper` namespace, including result mapping, reusable column fragments, CRUD operations, and conditional queries for customer data.  

#### Purpose  
- Maps SQL statements to Java methods for customer persistence.  
- Provides reusable column list and result mapping.  

#### Type  
- MyBatis mapper XML.  
- Root element: `mapper`.  
- Namespace: `shop.customer.CustomerMapper`.  

#### Definitions  
- **ResultMap** `customerResult`: maps `CUSTOMER_ID` → `id`, `EMAIL` → `email`, `LOYALTY_TIER` → `loyaltyTier`.  
- **SQL fragment** `customerColumns`: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.  
- **Select** `findByEmail`: retrieves a single customer by `EMAIL`.  
- **Select** `search`: retrieves customers filtered by optional `tier` and `createdAfter`; orders by `${sortColumn}`.  
- **Insert** `insert`: inserts a new customer, generates `id`.  
- **Update** `promote`: updates `LOYALTY_TIER` for a list of `ids`.  
- **Delete** `deleteInactive`: deletes customers with `LAST_ORDER_AT` before `cutoff`.  

#### Configuration  
- `useGeneratedKeys="true"` on `insert`.  
- `keyProperty="id"` on `insert`.  
- `order by ${sortColumn}` in `search`.  
- `now()` used for `CREATED_AT` in `insert`.  

#### Dependencies  
- SQL tables: `CUSTOMER`.  
- Columns: `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`.  
- Java class: `shop.customer.Customer`.  
- MyBatis DTD: `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.
