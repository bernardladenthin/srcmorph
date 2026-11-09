### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:50:02Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL schema for a web shop that stores products, customer orders, and order items, with a stored procedure to add items to an order while reserving stock and updating totals.  

#### Purpose  
- Define core tables for products, orders, and order items.  
- Provide a stored procedure to add an order line, reserve stock, and recalculate the order total.  

#### Responsibilities  
- **Data modeling** – tables `product`, `customer_order`, `order_item`.  
- **Business logic** – procedure `add_order_item` that enforces stock reservation and order total consistency.  

#### Key units  
- **`product`** – holds SKU, name, price, and stock.  
- **`customer_order`** – tracks order status, timestamps, and total amount.  
- **`order_item`** – links orders to products with quantity and unit price.  
- **`add_order_item`** – stored procedure that validates stock, updates or inserts an order item, decrements product stock, and updates the order total.  

#### Data flow  
1. Caller supplies `p_order_id`, `p_sku`, `p_qty`.  
2. Procedure looks up `product` by SKU, locking the row (`FOR UPDATE`).  
3. If stock insufficient, error is raised.  
4. Order item is inserted or its quantity updated.  
5. `product.stock` is decremented by `p_qty`.  
6. `customer_order.total_cents` is recalculated from all items of the order.  

#### Dependencies  
- Internal: tables `product`, `customer_order`, `order_item`.  
- No external functions or sequences.  

#### Cross-cutting  
- **Referential integrity** – foreign keys with cascade delete from `order_item` to `customer_order`.  
- **Concurrency control** – row locking (`FOR UPDATE`) to prevent race conditions on stock.  
- **Error handling** – procedure raises an error when product not found or stock is insufficient.  
- **Indexing** – `idx_order_customer` on `customer_order` for efficient customer‑order queries.
