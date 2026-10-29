### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:59:02Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL stored procedure that manages order line items, reserves stock, and updates order totals in an e‑commerce webshop.

#### Purpose
- Handle adding or updating a product line in a customer order.
- Reserve inventory and maintain accurate order totals.

#### Responsibilities
- **Order line management** – insert or update `order_item` rows.
- **Stock reservation** – decrement `product.stock` safely.
- **Order total calculation** – recompute `customer_order.total_cents` after changes.

#### Key units
- **`add_order_item(p_order_id, p_sku, p_qty)`** – stored procedure that orchestrates the above responsibilities.
- **Tables**  
  - `product` – product catalog with price and stock.  
  - `customer_order` – order header with status and total.  
  - `order_item` – line items linking orders to products.

#### Data flow
1. **Input**: order ID, product SKU, quantity.  
2. **Read**: lock the product row (`FOR UPDATE`) to fetch `product_id` and `price_cents`.  
3. **Validate**: ensure product exists and stock ≥ quantity.  
4. **Write**:  
   - Insert or update `order_item` (increment quantity if duplicate).  
   - Decrease `product.stock` by the ordered quantity.  
5. **Recalculate**: sum `quantity * unit_price_cents` for all items in the order and update `customer_order.total_cents`.  
6. **Output**: updated order total and reserved stock.

#### Dependencies
- **Tables**: `product`, `customer_order`, `order_item`.  
- **Constraints**: foreign keys enforce referential integrity; `FOR UPDATE` ensures row‑level locking during stock reservation.

#### Cross-cutting
- **Transactional safety** – all operations occur within a single transaction to maintain consistency.  
- **Error handling** – signals an error if the product is missing or stock is insufficient.  
- **Index usage** – `idx_order_customer` on `customer_order` supports efficient order lookups.
