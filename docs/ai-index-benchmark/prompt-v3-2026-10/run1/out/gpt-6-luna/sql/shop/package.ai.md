### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:51:55Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains a web-shop catalog, inventory, customer orders, and order totals while reserving stock when items are added.

#### Purpose
- Defines product, customer-order, and order-line storage, including prices captured when items are added.
- Provides `add_order_item` to add or increment an order line, reserve inventory, and recalculate the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price in cents, and stock.
- **Order lifecycle:** `customer_order` records customer, status, creation time, and total in cents.
- **Order contents:** `order_item` stores quantities and unit prices per product and order.
- **Item addition and stock reservation:** `add_order_item` validates SKU availability, updates the order line and product stock, and recalculates the order total.

#### Key units
- `product` — catalog and inventory table, with unique `sku`.
- `customer_order` — customer order table with `NEW`, `PAID`, `SHIPPED`, and `CANCELLED` statuses.
- `order_item` — order-line table keyed by `(order_id, product_id)`.
- `add_order_item(IN p_order_id, IN p_sku, IN p_qty)` — adds an item or increments its existing quantity, reserves stock, and updates `total_cents`.

#### Data flow
- `add_order_item` finds and locks the product matching `p_sku` when stock is sufficient; otherwise signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- Inserts an `order_item` row or increments its quantity, using the selected product price as `unit_price_cents`.
- Decrements `product.stock`, then recalculates `customer_order.total_cents` from the order’s line quantities and unit prices.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`; `order_item.product_id` references `product.product_id`.
- `idx_order_customer` indexes `customer_order(customer_id, created_at)`; `uk_product_sku` uniquely indexes `product.sku`.

#### Cross-cutting
- `add_order_item` uses `FOR UPDATE` to lock the selected product row during stock validation and reservation.
- Monetary values are stored as integer cents; order status is an inline MySQL `ENUM`.
