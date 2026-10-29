### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:58:14Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Atomic MySQL stored procedure to add items to a shop order while validating stock, reserving inventory, and recalculating totals.

#### Purpose
- Adds line items to an existing `customer_order` with immediate stock validation and inventory reservation.
- Merges duplicate items by incrementing quantity instead of creating new rows.

#### Responsibilities
- **Inventory Validation**: Locks product records to verify SKU existence and sufficient `stock` before proceeding.
- **Order Line Management**: Inserts new `order_item` rows or updates existing ones using `ON DUPLICATE KEY UPDATE`.
- **Financial Recalculation**: Atomically updates `product.stock` and recomputes `customer_order.total_cents`.

#### Key units
- `add_order_item`: Stored procedure handling the atomic transaction for adding items.
- `product`: Source table for SKU lookup, pricing (`price_cents`), and stock levels.
- `customer_order`: Target table for order status tracking and total amount aggregation.
- `order_item`: Junction table storing item quantities and unit prices per order.

#### Data flow
1. Input parameters `p_order_id` and `p_sku` are received.
2. Procedure locks `product` row via `SELECT ... FOR UPDATE` to check availability.
3. If valid, it executes an `INSERT ... ON DUPLICATE KEY UPDATE` on `order_item`.
4. Concurrently decrements `product.stock` and sums `quantity * unit_price_cents` to update `customer_order.total_cents`.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item`.
- External modules: None (pure SQL logic).

#### Cross-cutting
- **Concurrency Control**: Uses `FOR UPDATE` locking to prevent race conditions during stock checks.
- **Error Handling**: Raises explicit errors for missing products or insufficient stock.
- **Constraints**: Relies on composite primary keys in `order_item` and foreign key cascades for data integrity.
