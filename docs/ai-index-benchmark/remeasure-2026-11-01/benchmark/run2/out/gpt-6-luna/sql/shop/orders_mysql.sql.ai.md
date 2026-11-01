### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:43:45Z
- G: 
- A: 0.0.0
- X: file
---
> Models webshop products, customer orders, and order lines, with a procedure that adds an item, reserves stock, and refreshes the order total.

#### Purpose
- Stores product inventory, customer order status and totals, and per-product order quantities and prices.
- `add_order_item` adds or increments an order line while reserving stock and recalculating the order total.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status`: `ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku`
  - `name VARCHAR(200)` — NOT NULL
  - `price_cents INT UNSIGNED` — NOT NULL
  - `stock INT` — NOT NULL, DEFAULT `0`
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `customer_id BIGINT UNSIGNED` — NOT NULL
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of composite PRIMARY KEY
  - `product_id INT UNSIGNED` — NOT NULL, part of composite PRIMARY KEY
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL
  - PRIMARY KEY (`order_id`, `product_id`)
  - CONSTRAINT `fk_item_order`: FOREIGN KEY (`order_id`) REFERENCES `customer_order` (`order_id`) ON DELETE CASCADE
  - CONSTRAINT `fk_item_product`: FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
- Indexes:
  - `uk_product_sku` on `product` (`sku`), UNIQUE
  - `idx_order_customer` on `customer_order` (`customer_id`, `created_at`)
  - PRIMARY KEY on `product` (`product_id`)
  - PRIMARY KEY on `customer_order` (`order_id`)
  - PRIMARY KEY on `order_item` (`order_id`, `product_id`)

#### Reads
- Procedure `add_order_item`: `product.product_id`, `product.price_cents`, `product.sku`, `product.stock`; filters by `sku = p_sku` and `stock >= p_qty`, with `FOR UPDATE`.
- Procedure `add_order_item`: `order_item.quantity`, `order_item.unit_price_cents`; filters by `order_id = p_order_id` to calculate the sum.

#### Writes
- Procedure `add_order_item`:
  - `order_item`: INSERT `order_id`, `product_id`, `quantity`, `unit_price_cents`; on duplicate key, increments `quantity` by `p_qty`.
  - `product`: UPDATE `stock = stock - p_qty` for the selected `product_id`.
  - `customer_order`: UPDATE `total_cents` to the sum of matching order-item quantities times unit prices.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE)
- `order_item.product_id -> product.product_id` (`fk_item_product`)

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Locks the matching in-stock product row and retrieves its ID and price.
- Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no product is selected.
- Inserts an order line or increments its quantity on duplicate key; decrements product stock and recalculates the order total.
- No result set or return value.

#### Dependencies
- `product`
- `order_item`
- `customer_order`
