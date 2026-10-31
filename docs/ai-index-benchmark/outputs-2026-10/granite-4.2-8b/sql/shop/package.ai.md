### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T11:19:51Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> The package defines database objects and logic for a web shop's product catalog, customer orders, and order placement with automatic stock reservation.

#### Purpose
- Creates tables for products, customer orders, and order items.
- Implements a stored procedure `add_order_item` that inserts an order item while reserving stock and updating order totals.

#### Responsibilities
- Schema definition for catalog (`product`) and order tracking (`customer_order`, `order_item`).
- Order placement logic via stored procedure with stock reservation and total recalculation.

#### Key units
- `product` table – stores product metadata including `stock`.
- `customer_order` table – tracks order status and total amount.
- `order_item` table – links products to orders with quantity and unit price.
- Stored procedure `add_order_item(p_order_id, p_sku, p_qty)` – inserts/updates order item, reserves stock, recalculates total.

#### Data flow
- Input: `p_order_id`, `p_sku`, `p_qty` passed to `add_order_item`.
- Reads `product` (to find matching product with sufficient stock), `order_item` (to compute current subtotal), `customer_order` (to update total).
- Writes to `product` (`stock` decrement), `order_item` (INSERT or UPDATE quantity), `customer_order` (`total_cents` update).
- Output: updated `order_item` row (or merged quantity), updated `product.stock`, updated `customer_order.total_cents`.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item` (all referenced in the procedure).
- No external packages, views, or functions; uses only MySQL built-ins (e.g., `CURRENT_TIMESTAMP`, `AUTO_INCREMENT`).

#### Cross-cutting
- Uses `ON DUPLICATE KEY UPDATE` for idempotent insertion of `order_item` when item already exists.
- Stock reservation is atomic with total recalculation via single transaction implied by procedure logic.
- Error handling signals "unknown SKU or not enough stock" when product lookup fails or stock insufficient.
- Primary and foreign keys enforce referential integrity (`fk_item_order`, `fk_item_product`).
- Index `idx_order_customer` on (`customer_id`, `created_at`) for query performance on order lookup.
