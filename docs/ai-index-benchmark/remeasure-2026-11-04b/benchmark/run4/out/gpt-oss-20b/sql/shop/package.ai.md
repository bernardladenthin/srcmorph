### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:53:05Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides a MySQL schema and stored procedure for a web shop’s product catalog, orders, and order line items, handling stock reservation and order total calculation.

#### Purpose
- Persist products, customer orders, and order items.
- Offer a single procedure to add items, reserve stock, and update totals.

#### Responsibilities
- **Data storage**: tables `product`, `customer_order`, `order_item`.
- **Business logic**: stored procedure `add_order_item` that validates stock, updates inventory, and recalculates order totals.
- **Integrity enforcement**: foreign keys, unique SKU, and status enum.

#### Key units
- **Table `product`** – holds SKU, name, price, and stock.
- **Table `customer_order`** – tracks order status, timestamps, and total amount.
- **Table `order_item`** – links orders to products with quantity and unit price.
- **Procedure `add_order_item(p_order_id, p_sku, p_qty)`** – reserves stock, inserts/updates an order item, and updates the order total.

#### Data flow
1. Caller invokes `add_order_item` with order ID, SKU, and quantity.  
2. Procedure locks the product row (`FOR UPDATE`), checks stock.  
3. If sufficient, inserts or updates the `order_item` row.  
4. Decrements `product.stock` by the ordered quantity.  
5. Recalculates `customer_order.total_cents` from all items of that order.

#### Dependencies
- **Internal**: `product`, `customer_order`, `order_item` tables.  
- **External**: MySQL 8 features (ENUM, `FOR UPDATE`, auto‑increment).  

#### Cross-cutting
- **Concurrency**: row‑level locking on `product` to prevent overselling.  
- **Error handling**: procedure raises errors when SKU not found or stock insufficient.  
- **Data integrity**: foreign keys with `ON DELETE CASCADE`, unique SKU constraint, and status enum enforce consistent state.
