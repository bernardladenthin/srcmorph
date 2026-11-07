### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:39:18Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains shop inventory and customer order totals by recording order lines, reserving product stock, and tracking order status.

#### Purpose
- Stores products, inventory, customer orders, and per-product order quantities and prices.
- Adds order items while decrementing stock and recalculating the order total.

#### Responsibilities
- Product and inventory records: SKU, name, price, and available stock.
- Customer order records: customer, lifecycle status, creation time, and total.
- Order-line records: product, quantity, and unit price, linked to orders and products.
- Order-item insertion or quantity increment with stock reservation and total refresh.

#### Key units
- `product` — stores product identity, unique SKU, price, and stock.
- `customer_order` — stores customer orders, status (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`), and total.
- `order_item` — records each product and quantity in an order, with unit price.
- `add_order_item` — finds a SKU, checks and locks available stock, updates the order line and stock, then recalculates the order total.

#### Data flow
- `add_order_item` receives an order ID, SKU, and quantity; it selects the matching product and price if stock is sufficient.
- It inserts an order line or increments the existing line quantity, decrements product stock, and updates `customer_order.total_cents` from the order’s lines.
- If the SKU is unknown or stock is insufficient, it signals SQLSTATE `45000`.

#### Dependencies
- Internal tables: `product`, `order_item`, and `customer_order`.
- `order_item.order_id` references `customer_order.order_id` with cascading delete; `order_item.product_id` references `product.product_id`.

#### Cross-cutting
- MySQL 8 schema and stored procedure; `product.sku` is unique and `order_item` uses `(order_id, product_id)` as its primary key.
- Stock selection uses `FOR UPDATE`; routine errors use SQLSTATE `45000`.
