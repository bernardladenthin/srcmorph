### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T10:01:17Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> MySQL schema and stored procedure for a web shop that manages product inventory, customer orders, and order line items with atomic stock reservation and order total recalculation.

#### Purpose
- Defines the core e-commerce data model for products, customer orders, and order line items.
- Provides transactional logic for adding items to orders while enforcing stock availability and maintaining accurate order totals.

#### Responsibilities
- **Data Modeling**: Defines `product` catalog, `customer_order` headers, and `order_item` line items with appropriate constraints and indexes.
- **Order Processing**: Implements the `add_order_item` stored procedure to handle item addition, stock decrement, and total recalculation atomically.

#### Key units
- **`product` table**: Stores SKU, name, price in cents, and current stock levels.
- **`customer_order` table**: Tracks order status (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`), customer association, creation timestamp, and total amount in cents.
- **`order_item` table**: Links orders to products with quantity and unit price, forming the line items of an order.
- **`add_order_item` procedure**: Accepts order ID, SKU, and quantity; locks product row, validates stock, inserts/updates line item, decrements stock, and recalculates order total.

#### Data flow
1. Input: `p_order_id`, `p_sku`, `p_qty` passed to `add_order_item`.
2. Lock: `SELECT ... FOR UPDATE` on `product` by `sku` to prevent race conditions.
3. Validation: Check if product exists and `stock >= p_qty`; signal SQLSTATE `45000` on failure.
4. Update Line Item: `INSERT` into `order_item` or `UPDATE` quantity if duplicate key exists.
5. Decrement Stock: Update `product.stock` by subtracting `p_qty`.
6. Recalculate Total: Update `customer_order.total_cents` with `SUM(quantity * unit_price_cents)` for the order.

#### Dependencies
- **Internal Tables**: `product`, `customer_order`, `order_item`.
- **Foreign Keys**: `order_item.order_id` references `customer_order.order_id` (ON DELETE CASCADE); `order_item.product_id` references `product.product_id`.
- **Database Engine**: MySQL 8, InnoDB storage engine.

#### Cross-cutting
- **Concurrency Control**: Uses `FOR UPDATE` row locking in `add_order_item` to ensure atomic stock checks and decrements.
- **Error Handling**: Signals SQLSTATE `45000` for business logic violations (product not found, insufficient stock).
- **Data Integrity**: Enforces unique SKUs, non-negative stock, and cascading deletes for order items.
