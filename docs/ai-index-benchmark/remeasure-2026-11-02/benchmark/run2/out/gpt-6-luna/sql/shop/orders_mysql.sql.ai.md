### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:44:50Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Models shop products, customer orders, and order lines, with a procedure that adds items, reserves inventory, and recalculates order totals.

#### Purpose
- Stores product catalog, order headers, and order line items.
- `add_order_item` adds a product to an order, decrements stock, and updates the order total.

#### Type
- MySQL 8: tables `product`, `customer_order`, `order_item`; procedure `add_order_item`.
- `customer_order.status`: enum values `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

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
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of PRIMARY KEY
  - `product_id INT UNSIGNED` — NOT NULL, part of PRIMARY KEY
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
- `product`: `product_id`, `price_cents`, `sku`, `stock`
- `order_item`: `order_id`, `quantity`, `unit_price_cents`

#### Writes
- `order_item`: inserts `order_id`, `product_id`, `quantity`, `unit_price_cents`; on duplicate key updates `quantity`.
- `product`: updates `stock`.
- `customer_order`: updates `total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE)
- `order_item.product_id -> product.product_id` (`fk_item_product`)
- `product.sku` is used to locate products for order placement.

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Locks a matching `product` row with sufficient stock; signals SQLSTATE `45000` if no matching SKU or insufficient stock.
- Inserts an `order_item`, or adds `p_qty` to the existing line quantity.
- Decrements `product.stock` and recalculates `customer_order.total_cents` from the order’s lines.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
