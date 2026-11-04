### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:34:54Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides a MySQL 8 schema and stored procedure for managing products, orders, and order items in a web shop, including stock reservation and order total calculation.

#### Purpose
- Stores product catalog, customer orders, and line items.  
- Offers `add_order_item` to add or update an item, reserve stock, and recalculate order total.

#### Responsibilities
- Product catalog management.  
- Order lifecycle handling (NEW, PAID, SHIPPED, CANCELLED).  
- Order item creation and quantity updates.  
- Stock reservation and decrement.  
- Order total calculation.

#### Key units
- **Table `product`** – product catalog with SKU, price, and stock.  
- **Table `customer_order`** – order header with status, timestamps, and total.  
- **Table `order_item`** – line items linking orders to products.  
- **Procedure `add_order_item`** – inserts/updates items, reserves stock, updates totals.

#### Data flow
- `add_order_item(p_order_id, p_sku, p_qty)`  
  1. SELECT product by SKU with `FOR UPDATE`.  
  2. Verify stock ≥ `p_qty`; error if insufficient.  
  3. INSERT or UPDATE `order_item` quantity.  
  4. UPDATE `product.stock` decrement.  
  5. Recalculate `customer_order.total_cents` from all items.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item`.  
- FK constraints: `order_item.order_id → customer_order.order_id` (cascade delete), `order_item.product_id → product.product_id`.  
- No external functions or sequences.

#### Cross-cutting
- Row locking with `FOR UPDATE` for stock consistency.  
- FK and cascade delete enforce referential integrity.  
- Error handling for missing product or insufficient stock.
