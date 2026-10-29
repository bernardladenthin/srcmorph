### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:41:22Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages web shop order placement and inventory reservation logic.

#### Purpose
*   Defines product catalog and order transaction schema for a web shop.
*   Provides stored procedure to add items to orders with stock validation.

#### Responsibilities
*   Schema definition for products, orders, and line items.
*   Transactional logic for order item insertion and stock deduction.
*   Inventory validation and order total recalculation.

#### Key units
*   `product` table stores SKU, pricing, and available stock.
*   `customer_order` table tracks order status and customer association.
*   `order_item` table links orders to products with quantities and prices.
*   `add_order_item` procedure executes atomic order item creation and stock updates.
*   `idx_order_customer` index on `customer_id` and `created_at`.

#### Data flow
*   Input `p_sku` and `p_qty` trigger `product` row lock.
*   Stock validation prevents insufficient inventory errors.
*   `order_item` record inserted or updated with `quantity` and `unit_price_cents`.
*   `product.stock` decremented and `customer_order.total_cents` recalculated.

#### Dependencies
*   Internal tables: `product`, `customer_order`, `order_item`.
*   MySQL 8 dialect features (stored procedures, `FOR UPDATE` locks).

#### Cross-cutting
*   Transactional integrity enforced via row-level locking (`FOR UPDATE`).
*   Error signaling for unknown SKUs or insufficient stock.
*   Composite primary keys on `order_item` (`order_id`, `product_id`).
