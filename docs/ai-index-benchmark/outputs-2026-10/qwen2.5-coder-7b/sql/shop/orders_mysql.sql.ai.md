### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:17:51Z
- G: 
- A: 0.0.0
- X: file
---
> Manages web shop orders, including adding items and updating stock and totals.

#### Type
procedure

#### Schema
- `product`: `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT`, `sku VARCHAR(40) NOT NULL`, `name VARCHAR(200) NOT NULL`, `price_cents INT UNSIGNED NOT NULL`, `stock INT NOT NULL DEFAULT 0`, `PRIMARY KEY (product_id)`, `UNIQUE KEY uk_product_sku (sku)`
- `customer_order`: `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT`, `customer_id BIGINT UNSIGNED NOT NULL`, `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'`, `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`, `total_cents INT UNSIGNED NOT NULL DEFAULT 0`, `PRIMARY KEY (order_id)`, `KEY idx_order_customer (customer_id, created_at)`
- `order_item`: `order_id BIGINT UNSIGNED NOT NULL`, `product_id INT UNSIGNED NOT NULL`, `quantity SMALLINT UNSIGNED NOT NULL`, `unit_price_cents INT UNSIGNED NOT NULL`, `PRIMARY KEY (order_id, product_id)`, `CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES customer_order (order_id) ON DELETE CASCADE`, `CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES product (product_id)`

#### Reads
- `product`: `sku`, `stock`
- `customer_order`: `order_id`, `customer_id`, `status`, `created_at`, `total_cents`
- `order_item`: `order_id`, `product_id`, `quantity`, `unit_price_cents`

#### Writes
- `product`: `stock`
- `customer_order`: `total_cents`
- `order_item`: `order_id`, `product_id`, `quantity`, `unit_price_cents`

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`

#### Routine logic
- Parameters: `p_order_id BIGINT UNSIGNED`, `p_sku VARCHAR(40)`, `p_qty SMALLINT UNSIGNED`
- Steps:
  - Selects `product_id` and `price_cents` from `product` where `sku` matches `p_sku` and `stock` is sufficient.
  - If product not found or insufficient stock, raises an error.
  - Inserts or updates `order_item` with `p_order_id`, `v_product_id`, `p_qty`, and `v_price`.
  - Decreases `stock` in `product` by `p_qty`.
  - Updates `total_cents` in `customer_order` to reflect the current total of items in the order.
- Result: Order item added, stock reserved, and order total updated.
