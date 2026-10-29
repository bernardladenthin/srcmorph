### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:16:03Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Enables real-time order item addition with stock validation, reservation, and automatic order total updates in a web shop

#### Purpose  
- Adds a product to an existing order with stock validation and reservation  
- Updates the order total and product stock in real time during item addition  

#### Responsibilities  
- Order item management and stock validation  
- Real-time order total recalculation  
- Product stock reservation and deduction  

#### Key units  
- `orders_mysql.sql` – MySQL procedure for adding items to orders with stock checks and updates  
- `product` table – stores product details including price and stock levels  
- `order_item` table – tracks items in an order with quantity and unit price  
- `customer_order` table – holds the total order value in cents  

#### Data flow  
Input order ID and product SKU/quantity → validate product existence and stock → lock product row → check if stock sufficient → if valid, insert/update order item → deduct stock from product → recalculate and update order total  

#### Dependencies  
- `product` table (sku, product_id, price_cents, stock)  
- `order_item` table (order_id, product_id, quantity, unit_price_cents)  
- `customer_order` table (order_id, total_cents)  

#### Cross-cutting  
- Uses `FOR UPDATE` to ensure data consistency during stock validation  
- Validates input parameters and raises clear error on invalid SKU or insufficient stock  
- All monetary values stored in cents to avoid floating-point precision issues
