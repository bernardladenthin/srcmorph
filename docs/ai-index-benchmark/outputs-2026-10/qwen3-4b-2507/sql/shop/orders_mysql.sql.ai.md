### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:14:31Z
- G: 
- A: 0.0.0
- X: file
---
> Models order placement with product stock reservation and real-time order total updates in a web shop

#### Purpose
- Adds a product line to an existing order with stock validation and reservation  
- Updates order total and product stock in real time during order item addition  

#### Type
Procedure (MySQL 8)

#### Schema
- `p_order_id` (BIGINT UNSIGNED) – input order identifier  
- `p_sku` (VARCHAR(40)) – input product SKU to add  
- `p_qty` (SMALLINT UNSIGNED) – input quantity to add  

#### Reads
- `product` (sku, product_id, price_cents, stock)  
- `order_item` (order_id, product_id, quantity, unit_price_cents)  
- `customer_order` (order_id, total_cents)  

#### Writes
- `order_item` (order_id, product_id, quantity, unit_price_cents)  
- `product` (stock)  
- `customer_order` (total_cents)  

#### Relationships
- `order_item.order_id` → `customer_order.order_id`  
- `order_item.product_id` → `product.product_id`  

#### Routine logic
- Validates product exists and has sufficient stock via `sku` and `stock >= p_qty`  
- Uses `FOR UPDATE` to lock product row during validation  
- If product not found or stock insufficient, raises error with message 'unknown SKU or not enough stock'  
- Inserts or updates order item with quantity and unit price  
- Deducts stock from product table  
- Recalculates and updates order total from all items in the order  

#### Dependencies
- `product` table (sku, product_id, price_cents, stock)  
- `customer_order` table (order_id, total_cents)  
- `order_item` table (order_id, product_id, quantity, unit_price_cents)
