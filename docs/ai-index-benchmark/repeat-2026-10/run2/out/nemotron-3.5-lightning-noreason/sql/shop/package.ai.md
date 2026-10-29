### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:03:02Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL data model and procedure for a web shop, handling product catalog, customer orders, and stock-aware order item insertion with total recalculation.

#### Purpose
- Provides the database schema and stock-enforced order-adding logic for a web shop's order lifecycle.

#### Responsibilities
- Defines the core data model for products, customers, and orders.
- Encapsulates stock validation and order-total maintenance in a single procedure.

#### Key units
- **`add_item_to_order`** (procedure): Accepts a SKU and quantity; validates stock, inserts/updates an order item, reserves stock, and recalculates the order total.
- **`product`** table: Stores catalog data (`sku`, `name`, `price_cents`, `stock`).
- **`customer_order`** table: Tracks orders (`status`, `total_cents`, `customer_id`).
- **`order_item`** table: Links products to orders with quantity and unit price; uses `ON DELETE CASCADE`.

#### Data flow
1. Procedure receives `p_sku` and `p_qty`.
2. Selects `product_id` and `price_cents` from `product` where `sku` matches and `stock >= p_qty`, locking the row with `FOR UPDATE`.
3. If insufficient stock or unknown SKU, raises an error.
4. Inserts a new `order_item` row (or increments quantity via `ON DUPLICATE KEY`).
5. Decrements `product.stock` by `p_qty`.
6. Recalculates `customer_order.total_cents` as `SUM(quantity * unit_price_cents)` for the target order.

#### Dependencies
- `product` table (referenced by SKU and stock check; locked via `FOR UPDATE`).
- `order_item` table (inserted into, updated via `ON DUPLICATE KEY`, read for total recalculation).
- `customer_order` table (updated `total_cents` via aggregate subquery).
- `customer` table (implicit via `customer_order.customer_id` FK, not defined in file).

#### Cross-cutting
- Uses `ON DUPLICATE KEY` pattern for idempotent item insertion.
- Employs `FOR UPDATE` row locking to prevent race conditions on stock decrement.
- Error handling raises 'unknown SKU or not enough stock' when validation fails.
