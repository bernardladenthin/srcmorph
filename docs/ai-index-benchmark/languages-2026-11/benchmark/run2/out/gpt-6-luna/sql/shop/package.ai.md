### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:39:47Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains a web-shop catalog and inventory while building customer orders, reserving stock, and keeping order totals current.

#### Purpose
- Defines the product, customer-order, and order-line data model.
- Provides `add_order_item` to add or increase an order line, reserve inventory, and recalculate the order total.

#### Responsibilities
- Catalog and inventory: store product identifiers, SKU, pricing, and stock.
- Order management: represent customer orders, statuses, creation time, and totals.
- Order lines: associate products with orders and record quantities and unit prices.
- Item addition: find products by SKU, validate and reserve stock, upsert line quantities, and update totals.

#### Key units
- `product` — stores catalog details and available stock.
- `customer_order` — stores customer ownership, order status, timestamp, and total in cents.
- `order_item` — stores each order’s product quantities and unit prices, keyed by `(order_id, product_id)`.
- `add_order_item(p_order_id, p_sku, p_qty)` — adds or increments an order line and updates inventory and order total.

#### Data flow
- `add_order_item` looks up `product` by SKU and locks a matching row with sufficient stock; otherwise it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- It inserts an `order_item` or increments its quantity on duplicate key, then decrements `product.stock`.
- It recalculates `customer_order.total_cents` from the order’s line quantities and unit prices.

#### Dependencies
- MySQL 8 tables: `product`, `order_item`, and `customer_order`.
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`; `order_item.product_id` references `product.product_id`.

#### Cross-cutting
- Uses `FOR UPDATE` to lock the matching product row during stock validation.
- Reports missing SKU or insufficient stock through a signaled SQL error.
