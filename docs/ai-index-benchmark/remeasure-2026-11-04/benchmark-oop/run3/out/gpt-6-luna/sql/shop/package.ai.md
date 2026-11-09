### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:54:02Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages a web shop’s product inventory, customer orders, and order-line pricing, including stock reservation and order-total updates.

#### Purpose
- Stores product catalog and inventory, customer orders, and their priced line items.
- Adds or increments order lines while reserving stock and recalculating the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and available stock.
- **Orders and line items:** `customer_order` tracks customer, status, creation time, and total; `order_item` records product quantities and unit prices.
- **Order-item placement:** `add_order_item` validates SKU and stock, updates the line, deducts inventory, and refreshes the total.

#### Key units
- `product` — product catalog and stock table, with a unique SKU.
- `customer_order` — customer order record with `NEW`, `PAID`, `SHIPPED`, or `CANCELLED` status.
- `order_item` — order-product lines keyed by `(order_id, product_id)`.
- `add_order_item` — MySQL procedure that adds or increments an order line and applies inventory and total changes.

#### Data flow
- `add_order_item` looks up a product by SKU, requiring sufficient stock and locking the matching row.
- If no eligible product is found, it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- It inserts the order line or increments its quantity, deducts the requested quantity from product stock, then recalculates `customer_order.total_cents` from the order’s line items.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`.
- `order_item.product_id` references `product.product_id`.
- `add_order_item` reads and updates `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- `order_item` uses a composite primary key on `(order_id, product_id)`; duplicate-key handling increments the existing quantity.
- Order totals are computed as the sum of `quantity * unit_price_cents`; monetary values are stored in cents.
