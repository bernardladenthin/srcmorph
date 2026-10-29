### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:45:40Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages product inventory, customer order tracking, and transactional stock reservation for a web shop.

#### Purpose
* Models product inventory and customer order tracking.
* Provides a transactional mechanism to add items to orders, update stock levels, and recalculate order totals.

#### Responsibilities
* Inventory Management: Tracking product SKUs, pricing, and stock levels.
* Order Tracking: Managing customer orders, statuses, and total costs.
* Order Processing: Atomic handling of adding items to orders and updating inventory.

#### Key units
* `product` table: Stores SKU, pricing, and stock levels.
* `customer_order` table: Tracks order status and total cost per customer.
* `order_item` table: Links products to orders with quantity and unit price.
* `add_order_item` procedure: Handles stock validation, item insertion/update, stock decrement, and order total recalculation.

#### Data flow
* `add_order_item` input (`p_order_id`, `p_sku`, `p_qty`) $\rightarrow$ `product` (stock check/lock) $\rightarrow$ `order_item` (insert/update) $\rightarrow$ `product` (stock decrement) $\rightarrow$ `customer_order` (total update).

#### Dependencies
* Internal: `product`, `customer_order`, and `order_item` tables.

#### Cross-cutting
* MySQL 8 specific implementation.
* Error handling via signal '45000' for missing products or insufficient stock.
* Referential integrity via foreign keys with `ON DELETE CASCADE` for order items.
