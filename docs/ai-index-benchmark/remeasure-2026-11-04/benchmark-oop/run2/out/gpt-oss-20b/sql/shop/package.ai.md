### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:42:27Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL schema for a web shop that stores products, customer orders, and order items, with a stored procedure to add items to an order while reserving stock and updating totals.  

#### Purpose  
- Persist product catalog, customer orders, and line items.  
- Provide a transactional routine to add an item to an order, reserve stock, and recalculate the order total.  

#### Responsibilities  
- **Data persistence** – tables for products, orders, and order items.  
- **Business logic** – stored procedure `add_order_item` that enforces stock availability, updates inventory, and maintains order totals.  

#### Key units  
- **Table `product`** – holds SKU, name, price, and stock.  
- **Table `customer_order`** – tracks order status, timestamps, and total amount.  
- **Table `order_item`** – links orders to products with quantity and unit price.  
- **Procedure `add_order_item`** – accepts order ID, SKU, and quantity; locks product row, validates stock, inserts/updates item, decrements stock, and updates order total.  

#### Data flow  
1. Caller supplies `p_order_id`, `p_sku`, `p_qty`.  
2. Procedure selects product row with `FOR UPDATE`.  
3. If stock insufficient → error.  
4. Insert or update `order_item` row.  
5. Update `product.stock` by subtracting `p_qty`.  
6. Recalculate `customer_order.total_cents` from all items of the order.  

#### Dependencies  
- Internal: `product`, `customer_order`, `order_item` tables.  
- No external functions or sequences; relies on MySQL 8 features (`FOR UPDATE`, ENUM, auto‑increment).  

#### Cross-cutting  
- **Concurrency control** – row locking on product to prevent overselling.  
- **Atomicity** – all updates occur within the stored procedure’s transaction.  
- **Error handling** – raises error when product not found or stock insufficient.  
- **Indexing** – `idx_order_customer` on `(customer_id, created_at)` for order queries.
