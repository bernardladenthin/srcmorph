### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:10:38Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> MySQL schema and stored procedure for a web shop, managing product inventory, customer orders, and order line items with atomic stock reservation and total recalculation.

#### Purpose
- Defines the core e-commerce data model for products, customer orders, and order line items.
- Provides atomic transactional logic for adding order items, decrementing stock, and recalculating order totals.

#### Responsibilities
- **Data Modeling**: Defines `product` catalog, `customer_order` headers, and `order_item` line items with appropriate constraints and indexes.
- **Order Processing**: Implements the `add_order_item` procedure to handle stock validation, inventory decrement, line item insertion/updating, and order total recalculation.

#### Key units
- **`product` table**: Stores product catalog data including `sku`, `name`, `price_cents`, and `stock` levels.
- **`customer_order` table**: Stores order headers with `customer_id`, `status` (NEW, PAID, SHIPPED, CANCELLED), `created_at`, and `total_cents`.
- **`order_item` table**: Stores line items linking `order_id` and `product_id` with `quantity` and `unit_price_cents`.
- **`add_order_item` procedure**: Atomic routine that locks product rows, validates stock, inserts/updates order items, decrements stock, and recalculates order totals.

#### Data flow
- Input: `p_order_id`, `p_sku`, `p_qty` passed to `add_order_item`.
- Processing: Procedure locks matching `product` row by `sku`, validates `stock >= p_qty`, inserts or updates `order_item` quantity, decrements `product.stock`, and recalculates `customer_order.total_cents` as `SUM(quantity * unit_price_cents)`.
- Output: Updated `order_item`, `product.stock`, and `customer_order.total_cents` records; SQLSTATE `45000` signal on failure.

#### Dependencies
- Internal: `product`, `customer_order`, `order_item` tables.
- External: MySQL 8 InnoDB engine, utf8mb4 character set.

#### Cross-cutting
- **Concurrency Control**: Uses `FOR UPDATE` row locking on `product` to prevent race conditions during stock decrement.
- **Error Handling**: Signals SQLSTATE `45000` for missing products or insufficient stock.
- **Data Integrity**: Enforces referential integrity with CASCADE delete on `order_item.order_id` and foreign keys on `product_id`.
- **Atomicity**: All stock and total updates occur within a single transactional procedure.
