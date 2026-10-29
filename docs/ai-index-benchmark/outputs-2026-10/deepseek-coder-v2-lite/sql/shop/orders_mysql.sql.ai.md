### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T02:29:25Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop: products, orders and order placement (MySQL 8).

#### Purpose
- **Products**: Manage products with SKU, name, price, and stock.
- **Customer Orders**: Track orders with status, creation time, and total amount.
- **Order Items**: Link orders to products and track quantities and prices.
- **Procedures**: Manage order items, including adding items to an order, reserving stock, and updating order totals.

#### Type
SQL object kind: table / view / procedure.

#### Schema
- **product**
  - `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT
  - `sku` VARCHAR(40) NOT NULL
  - `name` VARCHAR(200) NOT NULL
  - `price_cents` INT UNSIGNED NOT NULL
  - `stock` INT NOT NULL DEFAULT 0
  - PRIMARY KEY (`product_id`)
  - UNIQUE KEY `uk_product_sku` (`sku`)

- **customer_order**
  - `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT
  - `customer_id` BIGINT UNSIGNED NOT NULL
  - `status` ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'
  - `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
  - `total_cents` INT UNSIGNED NOT NULL DEFAULT 0
  - PRIMARY KEY (`order_id`)
  - KEY `idx_order_customer` (`customer_id`, `created_at`)

- **order_item**
  - `order_id` BIGINT UNSIGNED NOT NULL
  - `product_id` INT UNSIGNED NOT NULL
  - `quantity` SMALLINT UNSIGNED NOT NULL
  - `unit_price_cents` INT UNSIGNED NOT NULL
  - PRIMARY KEY (`order_id`, `product_id`)
  - CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `customer_order` (`order_id`) ON DELETE CASCADE
  - CONSTRAINT `fk_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)

#### Reads
- Reads from `product` to get product details and check stock.
- Reads from `customer_order` to update total.

#### Writes
- Writes to `order_item` to add items.
- Writes to `product` to update stock.
- Writes to `customer_order` to update total.

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`

#### Routine logic
- **add_order_item**
  - **Parameters**: `p_order_id`, `p_sku`, `p_qty`
  - **Steps**:
    - Retrieve product details and check stock.
    - Insert or update order item.
    - Update product stock.
    - Update order total.

#### Dependencies
- References `product` and `customer_order` tables.
- Uses `order_item` for item management.
