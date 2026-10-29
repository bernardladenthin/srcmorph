### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:52:15Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages shop product inventory and customer order totals as order lines are added.

#### Purpose
- Defines the product catalog, stock, customer orders, and order-line storage.
- Provides `add_order_item` to add or increase an order line, reserve stock, and recalculate the order total.

#### Responsibilities
- Store products, SKU uniqueness, and available stock.
- Represent customer orders and their product lines, including line quantity and unit price.
- Validate SKU and stock availability before updating inventory and order totals.

#### Key units
- `product` — product catalog with SKU, price, and stock.
- `customer_order` — customer order status, creation time, and total.
- `order_item` — order lines keyed by order and product, with quantity and unit price.
- `add_order_item` — stored procedure that adds or increments a line, decrements stock, and recalculates the order total.

#### Data flow
- `add_order_item` finds and locks a product by SKU with sufficient stock; if none is found, it raises SQLSTATE `45000`.
- It inserts the order line or increments its quantity, decrements product stock, then sets the order total from the order’s line quantities and unit prices.

#### Dependencies
- Internal tables: `product`, `order_item`, and `customer_order`.
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`; `order_item.product_id` references `product.product_id`.

#### Cross-cutting
- Product selection uses `FOR UPDATE` to lock the matching product row.
- Unavailable SKU or insufficient stock produces the error message `unknown SKU or not enough stock`.
