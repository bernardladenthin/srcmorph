### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:09:07Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> Staff can search and review open orders, including totals and overdue status, and access order cancellation actions.

#### Purpose
- Render the open-orders page with customer-email and status search controls.
- Display the effective page size from `orderFilter`, defaulting to 25.

#### Responsibilities
- Present `${orders}` as order number, total, overdue status, and cancellation link.
- Submit search criteria to `/orders/search` using a GET form.

#### Key units
- `order-list.jsp` — renders order search and results, reads `orderFilter`, and provides cancellation links.

#### Data flow
- Reads `orderFilter` from the request to determine page size and iterates `${orders}` for display.
- Sends `customerEmail` and selected status to `/orders/search`; exposes each order’s cancellation URL as `orders/${order.id}/cancel`.

#### Dependencies
- Uses `shop.Order` and `shop.OrderFilter`, plus Jakarta Tags core and fmt libraries.
- Includes `/WEB-INF/jspf/header.jspf` statically and `/WEB-INF/jspf/footer.jspf` dynamically.
