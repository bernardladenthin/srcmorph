### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:30:01Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains webshop product inventory and customer orders, including stock reservation and order-total calculation when items are added.

#### Purpose
- Defines the product catalog, customer orders, and per-order item quantities and prices.
- Provides a routine to add order items while checking and decrementing stock and recalculating totals.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and stock; `uk_product_sku` enforces unique SKUs.
- **Orders and line items:** `customer_order` stores customer, status, creation time, and total; `order_item` records product quantities and unit prices.
- **Item addition:** `add_order_item` validates SKU and stock, upserts an order line, decrements inventory, and updates the order total.

#### Key units
- `product` — product catalog and available inventory.
- `customer_order` — customer order records with status and total in cents.
- `order_item` — products, quantities, and unit prices associated with an order.
- `add_order_item(p_order_id, p_sku, p_qty)` — adds a requested product quantity when stock permits.

#### Data flow
- `add_order_item` matches `p_sku` to a product and checks stock; if unavailable, signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- On success, inserts or increments the matching `(order_id, product_id)` line, decrements product stock, then recalculates `customer_order.total_cents` from the order’s lines.

#### Dependencies
- MySQL 8 tables `product`, `customer_order`, and `order_item`.
- `order_item.order_id` references `customer_order.order_id` with cascade delete; `order_item.product_id` references `product.product_id`.

#### Cross-cutting
- `add_order_item` locks the matching product row during SKU and stock selection.
- SQLSTATE `45000` reports unknown SKUs or insufficient stock.
