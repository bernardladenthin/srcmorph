### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:39:59Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> Staff can review open orders, filter them by customer email and shipping status, and cancel individual orders.

#### Purpose
- Render the order list with search controls, order totals, and overdue indicators.
- Provide per-order cancellation links.

#### Responsibilities
- Search interface: GET form to `/orders/search` with customer email and `OPEN` or `SHIPPED` status.
- Order review: display each order’s number, total, and overdue status, with a cancellation link.

#### Key units
- `order-list.jsp` — renders the orders page, prepopulates the customer email from the request parameter, and displays the `OrderFilter` page size (default `25`).

#### Data flow
- Reads `orderFilter` and `param.customerEmail` from the request; iterates `${orders}` to render order details and cancellation links.
- Submits search criteria to `/orders/search`; forms cancellation URLs from each order’s ID.

#### Dependencies
- `shop.OrderFilter` for the displayed page size.
- `/WEB-INF/jspf/header.jspf` and `/WEB-INF/jspf/footer.jspf` for shared page content.
- Jakarta Tags core and format libraries.
