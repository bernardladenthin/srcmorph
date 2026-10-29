### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T05:21:57Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Web shop: products, orders and order placement (MySQL 8).

#### Purpose
- Models a simple e‑commerce domain: products, customer orders, and order items.  
- Provides a stored procedure to add an item to an order while reserving stock and updating totals.

#### Responsibilities
- Define the core domain tables (`product`, `customer_order`, `order_item`).  
- Implement the `add_order_item` procedure that validates stock, creates or updates an order line, adjusts product stock, and recalculates order totals.

#### Key units
- **product** – stores SKU, name, price, and available stock.  
- **customer_order** – represents a single purchase, tracks status, timestamps, and total amount.  
- **order_item** – links an order to a product with quantity and unit price.  
- **add_order_item** – stored procedure that performs stock check, inserts/updates `order_item`, decrements `product.stock`, and updates `customer_order.total_cents`.

#### Data flow
1. Caller supplies `order_id`, `sku`, and `quantity`.  
2. Procedure reads `product` (by SKU) and `customer_order` (by order_id) under a shared lock.  
3. If stock is insufficient, an error is raised.  
4. An `order_item` row is inserted or its quantity is increased.  
5. `product.stock` is reduced by the added quantity.  
6. `customer_order.total_cents` is recomputed from all items in the order.  

#### Dependencies
- **Internal**: `product`, `customer_order`, `order_item` tables.  
- **External**: MySQL 8 (native stored procedure syntax, `FOR UPDATE` row locking, `SIGNAL` for error handling).
