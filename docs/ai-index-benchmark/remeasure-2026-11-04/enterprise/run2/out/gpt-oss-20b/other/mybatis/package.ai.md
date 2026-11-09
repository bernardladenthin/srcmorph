### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:18:37Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Provides MyBatis SQL mappings for customer persistence, including CRUD operations, result mapping, and dynamic queries for the `shop.customer.CustomerMapper` namespace.

#### Purpose
- Map SQL statements to Java methods for customer data access.
- Offer reusable column fragments and result mappings.

#### Responsibilities
- **Data Access Layer**: CRUD operations and conditional queries for customers.
- **SQL Fragment Reuse**: Centralized column list for consistency.
- **Dynamic Query Building**: Conditional filtering and ordering.

#### Key units
- **Mapper Namespace**: `shop.customer.CustomerMapper`
- **ResultMap** `customerResult`: maps `CUSTOMER_ID → id`, `EMAIL → email`, `LOYALTY_TIER → loyaltyTier`.
- **SQL Fragment** `customerColumns`: `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.
- **Select** `findByEmail`: fetches a single customer by email.
- **Select** `search`: retrieves customers with optional `tier` and `createdAfter` filters, ordered by `${sortColumn}`.
- **Insert** `insert`: inserts a new customer, auto-generates `id` with `useGeneratedKeys="true"`.
- **Update** `promote`: batch updates `LOYALTY_TIER` for a list of customer IDs.
- **Delete** `deleteInactive`: removes customers whose `LAST_ORDER_AT` precedes a cutoff date.

#### Data flow
1. **Input**: Method parameters (e.g., email, tier, dates, IDs).
2. **Processing**: MyBatis constructs SQL using the defined fragments and dynamic tags.
3. **Execution**: SQL runs against the `CUSTOMER` table.
4. **Output**: Result objects mapped via `customerResult` or affected row counts.

#### Dependencies
- **Database**: `CUSTOMER` table (`CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`).
- **Java Model**: `shop.customer.Customer`.
- **MyBatis**: DTD `https://mybatis.org/dtd/mybatis-3-mapper.dtd`.

#### Cross-cutting
- **Dynamic SQL**: Uses `<if>`, `<where>`, and `<foreach>` tags for optional clauses.
- **Parameter Binding**: `#{}` for safe parameters; `${}` for dynamic column names.
- **Key Generation**: `useGeneratedKeys="true"` with `keyProperty="id"` for inserts.
