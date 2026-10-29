### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T02:54:16Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Web shop: products, orders and order placement (MySQL 8).

#### Purpose
- **Products**: Manage products with SKU, name, price, and stock.
- **Customer Orders**: Track orders with status, creation time, and total amount.
- **Order Items**: Link orders to products and track quantities and prices.
- **Procedures**: Manage order items, including adding items to an order, reserving stock, and updating order totals.

#### Responsibilities
- **Products**: Manage products with SKU, name, price, and stock.
- **Customer Orders**: Track orders with status, creation time, and total amount.
- **Order Items**: Link orders to products and track quantities and prices.
- **Procedures**: Manage order items, including adding items to an order, reserving stock, and updating order totals.

#### Key units
- **product**: Manages products with SKU, name, price, and stock.
- **customer_order**: Tracks orders with status, creation time, and total amount.
- **order_item**: Links orders to products and tracks quantities and prices.
- **add_order_item**: Manages order items, including adding items to an order, reserving stock, and updating order totals.

#### Data flow
- Inputs: SKU, quantity, order ID, customer ID, product ID.
- Outputs: Order status, total amount, updated stock.
- Flow: Retrieves product details, checks stock, updates order items, updates product stock, and updates order totals.

#### Dependencies
- References `product` and `customer_order` tables.
- Uses `order_item` for item management.

#### Cross-cutting
- Uses procedures for complex logic.
- References external tables (`product`, `customer_order`, `order_item`).
- Manages stock and order totals.
