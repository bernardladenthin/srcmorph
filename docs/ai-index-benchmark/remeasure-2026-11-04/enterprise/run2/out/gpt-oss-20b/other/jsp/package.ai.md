### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:18:27Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> A JSP page that lists open orders, lets users filter by customer email and status, and provides a cancel link for each order.

#### Purpose
- Render a searchable list of open orders.
- Allow filtering by customer email and status.
- Offer a cancel action per order.

#### Responsibilities
- **Presentation**: Display order details (number, total, overdue flag) in an HTML table.
- **Filtering**: Provide a form to submit `customerEmail` and `status` parameters to `/orders/search`.
- **Action**: Generate a cancel link for each order (`orders/{id}/cancel`).

#### Key units
- **`order-list.jsp`** – JSP page that iterates over `orders` (type `List<Order>`) and renders the table.
- **`OrderFilter`** – Bean passed as request attribute `orderFilter` to pre‑populate filter fields.
- **`Order`** – Domain model with properties `number`, `total`, `overdue`, `id`.

#### Data flow
1. **Request** → `orderFilter` and `orders` attributes set by a servlet/controller.
2. **JSP** reads `orderFilter` to fill form fields and `orders` to populate the table.
3. **User submits** filter form → `/orders/search` servlet processes parameters, updates `orderFilter` and `orders`, forwards back to `order-list.jsp`.
4. **Cancel link** triggers `/orders/{id}/cancel` endpoint to cancel the selected order.

#### Dependencies
- **Tag libraries**: `jakarta.tags.core` (`c`), `jakarta.tags.fmt` (`fmt`).
- **Included fragments**: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.
- **Java classes**: `shop.Order`, `shop.OrderFilter`.

#### Cross-cutting
- Uses EL expressions (`${}`) for data binding.
- Relies on standard JSP scriptlet for page size calculation.
- Consistent use of `c:forEach` for iterating over orders.
