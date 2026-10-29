### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:43:54Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop order placement: product catalog, customer order headers, order line items, and a procedure that adds a line to an order while reserving stock and recalculating the order total.

#### Purpose
- Models the product catalog, customer orders, and order line items for a web shop.
- Provides a stored procedure to atomically add an item to an order, decrement stock, and update the order total.

#### Type
- `product` — table (MySQL 8, InnoDB)
- `customer_order` — table (MySQL 8, InnoDB)
- `order_item` — table (MySQL 8, InnoDB)
- `add_order_item` — stored procedure (MySQL 8)

#### Schema

**product**

| Column | Type | Constraints |
|--------|------|-------------|
| product_id | INT UNSIGNED | PK, AUTO_INCREMENT, NOT NULL |
| sku | VARCHAR(40) | NOT NULL, UNIQUE (uk_product_sku) |
| name | VARCHAR(200) | NOT NULL |
| price_cents | INT UNSIGNED | NOT NULL |
| stock | INT | NOT NULL, DEFAULT 0 |

**customer_order**

| Column | Type | Constraints |
|--------|------|-------------|
| order_id | BIGINT UNSIGNED | PK, AUTO_INCREMENT, NOT NULL |
| customer_id | BIGINT UNSIGNED | NOT NULL |
| status | ENUM('NEW','PAID','SHIPPED','CANCELLED') | NOT NULL, DEFAULT 'NEW' |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| total_cents | INT UNSIGNED | NOT NULL, DEFAULT 0 |

Index: `idx_order_customer` on (customer_id, created_at)

**order_item**

| Column | Type | Constraints |
|--------|------|-------------|
| order_id | BIGINT UNSIGNED | PK (composite), NOT NULL, FK → customer_order.order_id ON DELETE CASCADE |
| product_id | INT UNSIGNED | PK (composite), NOT NULL, FK → product.product_id |
| quantity | SMALLINT UNSIGNED | NOT NULL |
| unit_price_cents | INT UNSIGNED | NOT NULL |

#### Reads
- `add_order_item` reads `product.product_id`, `product.price_cents` (filtered by `sku`, `stock >= p_qty`, `FOR UPDATE`)
- `add_order_item` reads `order_item.quantity`, `order_item.unit_price_cents` (subquery for total)

#### Writes
- `add_order_item` INSERTs into `order_item` (order_id, product_id, quantity, unit_price_cents) with `ON DUPLICATE KEY UPDATE quantity`
- `add_order_item` UPDATEs `product.stock` (decrement)
- `add_order_item` UPDATEs `customer_order.total_cents` (recalculated sum)

#### Relationships
- `order_item.order_id` → `customer_order.order_id` (ON DELETE CASCADE)
- `order_item.product_id` → `product.product_id`
- `customer_order.customer_id` → external customer table (not defined in this file)

#### Routine logic

**add_order_item**

- Parameters: `p_order_id BIGINT UNSIGNED`, `p_sku VARCHAR(40)`, `p_qty SMALLINT UNSIGNED`
- Steps:
  1. `SELECT product_id, price_cents` from `product` where `sku = p_sku AND stock >= p_qty` with `FOR UPDATE` row lock.
  2. If no matching row → `SIGNAL SQLSTATE '45000'` ('unknown SKU or not enough stock').
  3. `INSERT INTO order_item` with `ON DUPLICATE KEY UPDATE quantity = quantity + p_qty` (idempotent line-item upsert).
  4. `UPDATE product SET stock = stock - p_qty`.
  5. `UPDATE customer_order SET total_cents = (SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id)`.
- Effect: one order line added/merged, stock decremented, order total recalculated — all within a single transaction (implicit).

#### Dependencies
- `product` (read + write)
- `customer_order` (write)
- `order_item` (read + write)
