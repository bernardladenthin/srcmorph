### other/mybatis
- H: 1.0
- C: A14A29CA
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:24:54Z
- G: 
- A: 0.0.0
- X: package
- F: [CustomerMapper.xml](CustomerMapper.xml.ai.md)
---
> Maps customer domain objects to SQL statements for CRUD and search operations.

#### Purpose
- Provides MyBatis SQL mappings for `shop.customer.Customer` entities.
- Supports find, search, insert, update, and delete operations.

#### Responsibilities
- **Data persistence**: CRUD operations for customer records.
- **Query building**: Dynamic search queries with optional filters and sorting.
- **Batch updates**: Promote multiple customers in a single statement.

#### Key units
- `CustomerMapper` (namespace `shop.customer.CustomerMapper`): orchestrates all SQL operations.
- `customerResult` (ResultMap): maps `CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER` to `shop.customer.Customer`.
- `customerColumns` (SQL fragment): reusable column list `CUSTOMER_ID, EMAIL, LOYALTY_TIER, CREATED_AT`.

#### Data flow
1. **Input**: method parameters (`email`, `tier`, `createdAfter`, `sortColumn`, `ids`, `cutoff`).
2. **Processing**: MyBatis constructs SQL using fragments, dynamic tags, and `<foreach>` for batch lists.
3. **Output**: Result sets mapped to `Customer` objects or affected row counts.

#### Dependencies
- **MyBatis**: DTD `https://mybatis.org/dtd/mybatis-3-mapper.dtd`, `useGeneratedKeys="true"`.
- **Domain**: `shop.customer.Customer` entity.
- **Database**: `CUSTOMER` table (`CUSTOMER_ID`, `EMAIL`, `LOYALTY_TIER`, `CREATED_AT`, `LAST_ORDER_AT`).

#### Cross-cutting
- Dynamic SQL tags (`<if>`, `<foreach>`) for optional conditions.
- Parameter placeholders (`#{}`) and substitution (`${}`) for runtime values.
- Generated key handling (`keyProperty="id"`).
- Reusable SQL fragments for consistent column selection.
