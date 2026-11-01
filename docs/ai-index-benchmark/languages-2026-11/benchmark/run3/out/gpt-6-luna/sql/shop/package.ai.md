### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:02:04Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages shop product inventory and customer order totals, including adding order lines while checking and reserving stock.

#### Purpose
- Defines product, customer-order, and order-line storage.
- Provides `add_order_item` to add or increase an order line and recalculate the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and stock.
- **Order records:** `customer_order` stores customer, status, creation time, and total; `order_item` links products to orders with quantities and unit prices.
- **Order-line updates:** `add_order_item` checks product availability, adjusts stock, and updates the order total.

#### Key units
- `product` — catalog and stock table, with unique SKU.
- `customer_order` — order header and computed total table.
- `order_item` — order-to-product lines, keyed by order and product.
- `add_order_item` — adds or increments a line, reserves stock, and recalculates the order total.

#### Data flow
- `add_order_item` finds and locks a SKU-matching product with sufficient stock; if none is found, it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- It inserts the order line or increments its quantity, subtracts the requested quantity from product stock, then sums line quantities times unit prices to update `customer_order.total_cents`.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascade delete; `order_item.product_id` references `product.product_id`.
- `add_order_item` reads and writes `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- Product selection uses a row lock and filters for sufficient stock.
- Order lines use a composite primary key; duplicate-key insertion increments the existing line quantity.
