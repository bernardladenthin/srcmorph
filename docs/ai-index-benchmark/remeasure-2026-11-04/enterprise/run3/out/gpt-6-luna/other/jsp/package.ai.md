### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:21:52Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> Provides staff with a searchable view of open orders, including order totals and overdue status, and a way to cancel orders.

#### Purpose
- Renders the open-orders page with search and cancellation controls.
- Displays the page size from `orderFilter`, defaulting to 25 when it is absent.

#### Responsibilities
- Search orders by customer email and status using a GET form.
- List orders with their number, total, overdue status, and ID; provide a cancellation link for each order.
- Include shared header and footer JSP fragments.

#### Key units
- `order-list.jsp` — renders the order search form and open-order list, and reads `orderFilter` to set the displayed page size.

#### Data flow
- Reads request attribute `orderFilter` to determine `pageSize`; outputs 25 if it is null.
- Populates the search field from `${param.customerEmail}` and submits customer email and status to `/orders/search`.
- Iterates `${orders}` to render order details and cancellation links at `orders/${order.id}/cancel`.

#### Dependencies
- `shop.Order` and `shop.OrderFilter`.
- Jakarta Tags core and formatting libraries.
- `/WEB-INF/jspf/header.jspf` and `/WEB-INF/jspf/footer.jspf`.
