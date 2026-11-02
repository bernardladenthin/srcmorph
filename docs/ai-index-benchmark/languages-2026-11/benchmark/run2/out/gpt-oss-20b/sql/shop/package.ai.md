### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:44:43Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides a MySQL‑based e‑commerce data model with tables for products, orders, and order items, plus a stored procedure that adds items to an order while reserving stock and updating the order total.  

#### Purpose  
- Define core e‑commerce tables (`product`, `customer_order`, `order_item`).  
- Offer a single stored procedure (`add_order_item`) to handle item addition, stock reservation, and total recalculation.  

#### Responsibilities  
- **Schema definition** – create tables, indexes, and foreign keys for product catalog, order tracking, and line items.  
- **Business logic** – encapsulate order‑item insertion, stock decrement, and order total update in a single atomic routine.  

#### Key units  
- **Table `product`** – holds SKU, name, price, and stock.  
- **Table `customer_order`** – tracks order status, timestamps, and total amount.  
- **Table `order_item`** – links orders to products with quantity and unit price.  
- **Procedure `add_order_item(p_order_id, p_sku, p_qty)`** – reserves stock, inserts/updates line items, and recalculates the order total.  

#### Data flow  
1. Caller supplies `p_order_id`, `p_sku`, `p_qty`.  
2. Procedure locks the matching `product` row (`FOR UPDATE`).  
3. If stock is sufficient, inserts or updates an `order_item`.  
4. Decrements `product.stock` by `p_qty`.  
5. Recomputes `customer_order.total_cents` from all `order_item` rows for that order.  

#### Dependencies  
- **Tables**: `product`, `customer_order`, `order_item`.  
- **Columns**: `product.sku`, `product.stock`, `product.price_cents`, `order_item.order_id`, `order_item.product_id`, `order_item.quantity`, `order_item.unit_price_cents`, `customer_order.total_cents`.  
- **Foreign keys**: `order_item.fk_item_order`, `order_item.fk_item_product`.  

#### Cross-cutting  
- Uses `FOR UPDATE` locking to prevent race conditions on stock.  
- Relies on MySQL 8 features: `ENUM` for order status, `AUTO_INCREMENT` for primary keys, and `SIGNAL` for error handling.  
- Consistent naming convention (`*_id`, `*_cents`, `*_qty`) for clarity and type safety.
