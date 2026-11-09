### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:54:18Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> A MySQL schema for a web shop that stores products, customer orders, and order items, and a stored procedure to add items to an order while reserving stock and updating totals.  

#### Purpose  
- Stores product catalog, orders, and line items.  
- Provides a procedure to add an item to an order, reserve stock, and recalculate the order total.  

#### Type  
- **Tables**: `product`, `customer_order`, `order_item`.  
- **Procedure**: `add_order_item`.  
- Dialect: MySQL 8.  

#### Schema  
- **product**  
  - `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK  
  - `sku VARCHAR(40) NOT NULL` UNIQUE `uk_product_sku`  
  - `name VARCHAR(200) NOT NULL`  
  - `price_cents INT UNSIGNED NOT NULL`  
  - `stock INT NOT NULL DEFAULT 0`  
  - Engine: InnoDB, charset utf8mb4  

- **customer_order**  
  - `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK  
  - `customer_id BIGINT UNSIGNED NOT NULL`  
  - `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`  
  - `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`  
  - `total_cents INT UNSIGNED NOT NULL DEFAULT 0`  
  - Index `idx_order_customer` on `(customer_id, created_at)`  

- **order_item**  
  - `order_id BIGINT UNSIGNED NOT NULL`  
  - `product_id INT UNSIGNED NOT NULL`  
  - `quantity SMALLINT UNSIGNED NOT NULL`  
  - `unit_price_cents INT UNSIGNED NOT NULL`  
  - PK `(order_id, product_id)`  
  - FK `fk_item_order` references `customer_order(order_id)` ON DELETE CASCADE  
  - FK `fk_item_product` references `product(product_id)`  

#### Reads  
- `product` (SELECT for SKU and stock).  
- `order_item` (SELECT SUM for total calculation).  

#### Writes  
- `order_item`: INSERT 1 row (or UPDATE if duplicate).  
- `product`: UPDATE 1 row (stock decrement).  
- `customer_order`: UPDATE 1 row (total_cents recalculation).  

#### Relationships  
- `order_item.order_id -> customer_order.order_id` (FK `fk_item_order`).  
- `order_item.product_id -> product.product_id` (FK `fk_item_product`).  

#### Routine logic  
- **Parameters**: `p_order_id BIGINT UNSIGNED`, `p_sku VARCHAR(40)`, `p_qty SMALLINT UNSIGNED`.  
- **Steps**:  
  1. SELECT `product_id`, `price_cents` into local vars where `sku = p_sku` and `stock >= p_qty` FOR UPDATE.  
  2. If no product found, SIGNAL error.  
  3. INSERT into `order_item`; on duplicate key, increment `quantity`.  
  4. UPDATE `product` stock by subtracting `p_qty`.  
  5. Recalculate `customer_order.total_cents` as SUM of `quantity * unit_price_cents` for the order.  

#### Dependencies  
- Tables: `product`, `customer_order`, `order_item`.  
- No external functions or sequences.
