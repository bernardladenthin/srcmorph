### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:32:05Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains web-shop product inventory and customer orders, including adding order items, reserving stock, and recalculating order totals.

#### Purpose
- Defines the product, order, and order-line data model.
- Provides `add_order_item` to add or increase an order line while updating inventory and the order total.

#### Responsibilities
- **Inventory:** Store product SKUs, prices, and stock; enforce unique SKUs.
- **Order management:** Store customer orders, status, creation time, and total.
- **Order lines:** Record quantities and unit prices, linking products to orders.
- **Item addition:** Validate SKU and available stock, update the line and inventory, then refresh the order total.

#### Key units
- `product` — product SKU, name, price, and stock.
- `customer_order` — customer order status, timestamp, and total.
- `order_item` — per-product quantities and unit prices within an order.
- `add_order_item` — adds or increments an order line, decrements stock, and recalculates the order total.

#### Data flow
- `add_order_item` finds and locks a product by SKU with sufficient stock; if none is found, it signals SQLSTATE `45000`.
- It inserts the order line or increments its quantity, subtracts the requested quantity from product stock, and updates `customer_order.total_cents` from the order’s lines.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascade on order deletion.
- `order_item.product_id` references `product.product_id`.
- `customer_order` is indexed by `(customer_id, created_at)`; `product.sku` has a unique index.

#### Cross-cutting
- Uses MySQL 8 tables, foreign keys, indexes, and a stored procedure.
- `customer_order.status` is constrained to `NEW`, `PAID`, `SHIPPED`, or `CANCELLED`.
- `add_order_item` reports unknown SKU or insufficient stock with SQLSTATE `45000` and message `unknown SKU or not enough stock`.
