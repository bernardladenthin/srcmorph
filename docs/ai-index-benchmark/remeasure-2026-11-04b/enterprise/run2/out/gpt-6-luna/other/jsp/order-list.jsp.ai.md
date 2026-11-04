### order-list.jsp
- H: 1.0
- C: 939CAC41
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:29:37Z
- G: 
- A: 0.0.0
- X: file
- P: C9F70393
- N: F786E780
---
**Facts (exact, whole file):** page directives: 1; taglib directives: 2; includes and forwards: 2; tag file attributes: 0; scriptlets: 1; expressions: 1; declarations: 0; EL expressions: 7; prefixed tags: 5; forms: 1; input fields: 3; request parameters read: 1

> Lists customer orders for review and lets staff search by customer email or status and cancel an order.

#### Purpose
- Display orders with their number, total, overdue status, and cancellation link.
- Search orders by customer email and status.

#### Type
- JSP page; `text/html;charset=UTF-8`.
- `c` = Jakarta Tags core (`jakarta.tags.core`); `fmt` = Jakarta Tags formatting (`jakarta.tags.fmt`).
- Uses JSP standard actions via `jsp`.

#### Data
- Request attribute `orderFilter`; scriptlet calls `getPageSize()`.
- `${orders}` iterated as `order`.
- `order.number`, `order.total`, `order.overdue`, `order.id`.
- Request parameter `param.customerEmail`.

#### Actions
- GET form to `${pageContext.request.contextPath}/orders/search`; fields: `customerEmail` (text), `status` (select: `OPEN`, `SHIPPED`).
- Cancel link to `orders/${order.id}/cancel`.
- No validators or converters shown.

#### Structure
- Includes `/WEB-INF/jspf/header.jspf` via include directive.
- Includes `/WEB-INF/jspf/footer.jspf` via `jsp:include`.

#### Server code
- Imports `java.util.List`, `shop.Order`, and `shop.OrderFilter`.
- Reads request attribute `orderFilter` as `OrderFilter`; computes `pageSize` as 25 when the filter is null, otherwise calls `getPageSize()`.

#### Dependencies
- `/WEB-INF/jspf/header.jspf`
- `/WEB-INF/jspf/footer.jspf`
- `shop.Order`, `shop.OrderFilter`
- `${pageContext.request.contextPath}/orders/search`
- `orders/${order.id}/cancel`
