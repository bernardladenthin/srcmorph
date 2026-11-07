### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:48:52Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> A MySQL schema for a web shop that stores products, customer orders, and order items, with a stored procedure to add items to an order while reserving stock and updating totals.  

#### Purpose  
- Define tables for products, orders, and order items.  
- Provide a procedure to add an order line, reserve stock, and recalculate the order total.  

#### Type  
- **Tables**: `product`, `customer_order`, `order_item`.  
- **Procedure**: `add_order_item`.  
- Dialect: MySQL 8.  

#### Schema  

**product**  
- `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` – PK  
- `sku VARCHAR(40) NOT NULL` – UNIQUE `uk_product_sku`  
- `name VARCHAR(200) NOT NULL`  
- `price_cents INT UNSIGNED NOT NULL`  
- `stock INT NOT NULL DEFAULT 0`  

**customer_order**  
- `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` – PK  
- `customer_id BIGINT UNSIGNED NOT NULL`  
- `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`  
- `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`  
- `total_cents INT UNSIGNED NOT NULL DEFAULT 0`  
- INDEX `idx_order_customer` (`customer_id`, `created_at`)  

**order_item**  
- `order_id BIGINT UNSIGNED NOT NULL` – PK part, FK `fk_item_order` → `customer_order(order_id)` ON DELETE CASCADE  
- `product_id INT UNSIGNED NOT NULL` – PK part, FK `fk_item_product` → `product(product_id)`  
- `quantity SMALLINT UNSIGNED NOT NULL`  
- `unit_price_cents INT UNSIGNED NOT NULL`  

#### Reads  
- `product` (SELECT `product_id`, `price_cents` WHERE `sku` = `p_sku` AND `stock` ≥ `p_qty` FOR UPDATE)  
- `order_item` (SELECT SUM(`quantity` * `unit_price_cents`) WHERE `order_id` = `p_order_id`)  

#### Writes  
- `order_item`: INSERT one row (or UPDATE existing) – 1 row affected.  
- `product`: UPDATE `stock` – 1 row affected.  
- `customer_order`: UPDATE `total_cents` – 1 row affected.  

#### Relationships  
- `order_item.order_id` → `customer_order.order_id` (FK `fk_item_order`, ON DELETE CASCADE)  
- `order_item.product_id` → `product.product_id` (FK `fk_item_product`)  

#### Routine logic  
- Parameters: `p_order_id BIGINT UNSIGNED`, `p_sku VARCHAR(40)`, `p_qty SMALLINT UNSIGNED`.  
- Lookup product by SKU with stock check; lock row (`FOR UPDATE`).  
- If not found or insufficient stock, raise error.  
- Insert or update `order_item` (duplicate key adds quantity).  
- Decrement `product.stock` by `p_qty`.  
- Recalculate `customer_order.total_cents` from all items of the order.  

#### Dependencies  
- Tables: `product`, `customer_order`, `order_item`.  
- No external functions or sequences.
