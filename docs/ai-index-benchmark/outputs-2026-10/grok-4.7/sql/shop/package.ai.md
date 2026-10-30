### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:47:21Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
I'm compiling a dense structured markdown summary of the package from the provided per-file summaries.> Web-shop catalog and checkout: products with stock, customer orders, line items, and placing an order line that reserves stock and updates the order total.

#### Purpose
- Models web-shop products, customer orders, and order line items in MySQL 8.
- Places an order line by SKU: checks stock, inserts or increments the line, decrements stock, and recalculates the order total.

#### Responsibilities
- Catalog storage: `product` identity, unique SKU, price, and stock.
- Order header storage: `customer_order` status, customer, timestamps, and `total_cents`.
- Line storage: `order_item` quantity and unit price, keyed by order and product.
- Checkout mutation: `add_order_item` locks stock, writes the line, and refreshes the order total.

#### Key units
- `product`: catalog row with `product_id`, unique `sku`, `name`, `price_cents`, and `stock`.
- `customer_order`: order header with `order_id`, `customer_id`, status `ENUM('NEW','PAID','SHIPPED','CANCELLED')`, `created_at`, and `total_cents`.
- `order_item`: line with PK `(order_id, product_id)`, `quantity`, and `unit_price_cents`.
- `add_order_item`: procedure `(p_order_id, p_sku, p_qty)` that reserves stock and updates the line and total.
- `uk_product_sku`, `idx_order_customer`, `fk_item_order`, `fk_item_product`: uniqueness, customer lookup, and line foreign keys.

#### Data flow
- Caller supplies `p_order_id`, `p_sku`, and `p_qty` to `add_order_item`.
- Procedure `SELECT ... FOR UPDATE` on `product` where `sku = p_sku` and `stock >= p_qty`; missing row signals `SQLSTATE '45000'` (`unknown SKU or not enough stock`).
- Inserts `order_item` or `ON DUPLICATE KEY UPDATE` adds `p_qty` to existing `quantity`.
- Decrements `product.stock` by `p_qty`, then sets `customer_order.total_cents` to `SUM(quantity * unit_price_cents)` for that `order_id`.

#### Dependencies
- `add_order_item` reads and writes `product`, `order_item`, and `customer_order`.
- `order_item.order_id` → `customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE).
- `order_item.product_id` → `product.product_id` (`fk_item_product`).
- `customer_order.customer_id` is indexed with `created_at`; no customer table FK in this file.

#### Cross-cutting
- MySQL 8, InnoDB, `utf8mb4`, `ENUM`, `SIGNAL`, and `DELIMITER`.
- Stock check and reservation share one locked `product` read (`FOR UPDATE`).
- Money is stored as integer cents (`price_cents`, `unit_price_cents`, `total_cents`).
