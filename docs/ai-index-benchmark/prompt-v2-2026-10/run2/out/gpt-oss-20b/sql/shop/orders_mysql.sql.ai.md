### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:53:51Z
- G: 
- A: 0.0.0
- X: file
---
> Defines a MySQL 8 schema for a web shop with products, orders, and order items, plus a stored procedure to add items to an order while managing stock and totals.

#### Purpose
- Store product catalog, customer orders, and order line items.
- Provide a procedure to add an item to an order, reserve stock, and recalculate the order total.

#### Type
- Table: `product`
- Table: `customer_order`
- Table: `order_item`
- Procedure: `add_order_item` (MySQL 8)

#### Schema
**product**
- `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK
- `sku VARCHAR(40) NOT NULL` UNIQUE KEY `uk_product_sku`
- `name VARCHAR(200) NOT NULL`
- `price_cents INT UNSIGNED NOT NULL`
- `stock INT NOT NULL DEFAULT 0`

**customer_order**
- `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK
- `customer_id BIGINT UNSIGNED NOT NULL`
- `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'
- `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
- `total_cents INT UNSIGNED NOT NULL DEFAULT 0`
- INDEX `idx_order_customer` (`customer_id`, `created_at`)

**order_item**
- `order_id BIGINT UNSIGNED NOT NULL`
- `product_id INT UNSIGNED NOT NULL`
- `quantity SMALLINT UNSIGNED NOT NULL`
- `unit_price_cents INT UNSIGNED NOT NULL`
- PK (`order_id`, `product_id`)
- FK `fk_item_order` (`order_id`) REFERENCES `customer_order`(`order_id`) ON DELETE CASCADE
- FK `fk_item_product` (`product_id`) REFERENCES `product`(`product_id`)

#### Reads
- `product` (SELECT product_id, price_cents WHERE sku = p_sku AND stock >= p_qty FOR UPDATE)
- `order_item` (SELECT SUM(quantity * unit_price_cents) WHERE order_id = p_order_id)
- `customer_order` (UPDATE total_cents calculation)

#### Writes
- `order_item` (INSERT ... ON DUPLICATE KEY UPDATE quantity = quantity + p_qty)
- `product` (UPDATE stock = stock - p_qty)
- `customer_order` (UPDATE total_cents = SUM(...))

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK `fk_item_order`)
- `order_item.product_id -> product.product_id` (FK `fk_item_product`)

#### Routine logic
- Parameters: `p_order_id BIGINT UNSIGNED`, `p_sku VARCHAR(40)`, `p_qty SMALLINT UNSIGNED`
- SELECT product_id and price_cents from `product` where SKU matches and stock sufficient, locking row.
- If no product found, SIGNAL error.
- INSERT or UPDATE `order_item` with quantity and unit price.
- Decrement `product.stock` by `p_qty`.
- Recalculate `customer_order.total_cents` as sum of line totals for the order.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`
- No external functions or sequences.
