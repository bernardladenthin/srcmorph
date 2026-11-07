### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:57:33Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> A MySQL schema for a web shop that stores products, customer orders, and order items, with a stored procedure to add items to an order while reserving stock and updating totals.  

#### Purpose  
- Define tables for products, orders, and order items.  
- Provide a procedure to add an item to an order, reserve stock, and recalculate the order total.  

#### Type  
- Table: `product`  
- Table: `customer_order`  
- Table: `order_item`  
- Procedure: `add_order_item` (MySQL 8)  

#### Schema  
**product**  
- `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK  
- `sku VARCHAR(40) NOT NULL` UNIQUE `uk_product_sku`  
- `name VARCHAR(200) NOT NULL`  
- `price_cents INT UNSIGNED NOT NULL`  
- `stock INT NOT NULL DEFAULT 0`  

**customer_order**  
- `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK  
- `customer_id BIGINT UNSIGNED NOT NULL`  
- `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`  
- `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`  
- `total_cents INT UNSIGNED NOT NULL DEFAULT 0`  
- Index `idx_order_customer` on `(customer_id, created_at)`  

**order_item**  
- `order_id BIGINT UNSIGNED NOT NULL`  
- `product_id INT UNSIGNED NOT NULL`  
- `quantity SMALLINT UNSIGNED NOT NULL`  
- `unit_price_cents INT UNSIGNED NOT NULL`  
- PK `(order_id, product_id)`  
- FK `fk_item_order` references `customer_order(order_id)` ON DELETE CASCADE  
- FK `fk_item_product` references `product(product_id)`  

#### Reads  
- `product` (SELECT `product_id`, `price_cents` WHERE `sku` = `p_sku` AND `stock` >= `p_qty` FOR UPDATE)  
- `order_item` (SELECT SUM(`quantity` * `unit_price_cents`) WHERE `order_id` = `p_order_id`)  

#### Writes  
- `order_item` (INSERT or UPDATE via ON DUPLICATE KEY) – 1 row per call  
- `product` (UPDATE `stock` – 1 row)  
- `customer_order` (UPDATE `total_cents` – 1 row)  

#### Relationships  
- `order_item.order_id` → `customer_order.order_id` (FK `fk_item_order`)  
- `order_item.product_id` → `product.product_id` (FK `fk_item_product`)  

#### Routine logic  
- Input: `p_order_id`, `p_sku`, `p_qty`  
- Lookup product ID and price where SKU matches and stock sufficient (FOR UPDATE).  
- If not found, raise error `'unknown SKU or not enough stock'`.  
- Insert or update `order_item` with quantity and unit price; if duplicate, increment quantity.  
- Decrement `product.stock` by `p_qty`.  
- Recalculate `customer_order.total_cents` as sum of `quantity * unit_price_cents` for that order.  

#### Dependencies  
- Tables: `product`, `customer_order`, `order_item`  
- No external functions or sequences.
