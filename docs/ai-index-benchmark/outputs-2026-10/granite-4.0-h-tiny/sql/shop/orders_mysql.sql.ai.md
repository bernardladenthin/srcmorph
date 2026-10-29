### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:09:33Z
- G: 
- A: 0.0.0
- X: file
---
**Purpose**  
- Models a web‑shop domain: products, customers, and order placement, including order items and stock reservation.

**Type**  
- MySQL 8 stored procedure (`add_order_item`).

**Schema**  
- `product` (`product_id INT UNSIGNED PK`, `sku VARCHAR(40) UNQ`, `name VARCHAR(200) UNQ`, `price_cents INT UNSIGNED`, `stock INT`)  
- `customer_order` (`order_id BIGINT UNSIGNED PK`, `customer_id BIGINT UNSIGNED`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED')`, `created_at DATETIME`, `total_cents INT UNSIGNED`)  
- `order_item` (`order_id BIGINT UNSIGNED FK→customer_order(order_id)`, `product_id INT UNSIGNED FK→product(product_id)`, `quantity SMALLINT UNSIGNED`, `unit_price_cents INT UNSIGNED`)  

**Reads**  
- `product` (sku lookup, stock check)  
- `customer_order` (order total update)  

**Writes**  
- `order_item` (inserts new line)  
- `product` (stock decrement)  
- `customer_order` (total_cents recomputed)  

**Relationships**  
- `order_item.order_id -> customer_order(order_id)`  
- `order_item.product_id -> product(product_id)`  

**Routine Logic**  
- Accepts order ID, SKU, and quantity.  
- Looks up product, verifies stock and availability.  
- Inserts order item; on duplicate SKU updates quantity.  
- Deducts stock from product.  
- Recalculates total_cents for the order.  
- Signals error if SKU not found or insufficient stock.
