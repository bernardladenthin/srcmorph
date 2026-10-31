### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:37:38Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> MySQL 8 web-shop order schema: product catalog with stock, customer orders and line items, and a procedure that adds a SKU to an order while reserving stock and refreshing the order total.

#### Purpose
- Stores the web shop catalog (`product`), customer orders (`customer_order`) and order lines (`order_item`).
- Places items into orders via `add_order_item`, which reserves stock and recalculates `total_cents`.

#### Responsibilities
- Catalog and stock: `product` holds SKU, name, price and on-hand `stock`; `uk_product_sku` enforces unique SKUs.
- Order header: `customer_order` tracks customer, `status` (NEW, PAID, SHIPPED, CANCELLED), creation time and `total_cents`; indexed by `idx_order_customer (customer_id, created_at)`.
- Order lines: `order_item` keyed by composite PK `(order_id, product_id)`, storing `quantity` and `unit_price_cents`.
- Order placement logic: `add_order_item` locks the matching product row, inserts or merges the line, decrements stock and recomputes the total.

#### Key units
- `add_order_item(p_order_id, p_sku, p_qty)`: stored procedure; locks product with `FOR UPDATE`, raises SQLSTATE `45000` on unknown SKU or insufficient stock, upserts the line, decrements stock, refreshes order total.
- `product`: catalog table with `sku`, `price_cents`, `stock`.
- `customer_order`: order header table with `status` ENUM and `total_cents`.
- `order_item`: order line table, PK `(order_id, product_id)`.
- `uk_product_sku`, `idx_order_customer`, `fk_item_order`, `fk_item_product`: named constraints/indexes.

#### Data flow
- Caller passes order id, SKU and quantity to `add_order_item`.
- Procedure looks up `product_id` and `price_cents` where `sku` matches and `stock >= p_qty`, row-locked.
- Missing match raises an error; otherwise the line is inserted with the current price, or `quantity` is incremented on duplicate key.
- `product.stock` is reduced by `p_qty`.
- `customer_order.total_cents` is set to the sum of `quantity * unit_price_cents` over the order's lines.
- No return value; effects are the line change, stock reservation and refreshed total.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`.
- Foreign keys: `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE); `order_item.product_id -> product.product_id` (`fk_item_product`).
- Engine/dialect: MySQL 8, InnoDB, utf8mb4, `DELIMITER //`, `SIGNAL SQLSTATE`, `ON DUPLICATE KEY UPDATE`.

#### Cross-cutting
- Concurrency: `FOR UPDATE` row lock on the product during the lookup guards stock reservation.
- Error handling: business-rule failures signalled with `SIGNAL SQLSTATE '45000'` and message "unknown SKU or not enough stock".
- Monetary values stored as integer cents (`price_cents`, `unit_price_cents`, `total_cents`).
