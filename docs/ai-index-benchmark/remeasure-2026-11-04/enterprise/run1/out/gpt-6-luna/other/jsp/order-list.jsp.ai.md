### order-list.jsp
- H: 1.0
- C: 939CAC41
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:07:34Z
- G: 
- A: 0.0.0
- X: file
- P: F6DC35F6
- N: F786E780
---
**Facts (exact, whole file):** page directives: 1; taglib directives: 2; includes and forwards: 2; tag file attributes: 0; scriptlets: 1; expressions: 1; declarations: 0; EL expressions: 7; prefixed tags: 5; forms: 1; input fields: 3; request parameters read: 1

> The open-orders page lets staff search orders by customer email or status, review totals and overdue status, and cancel an order.

#### Purpose
- Display open orders and provide order search and cancellation controls.
- Show the effective page size from the order filter, defaulting to 25.

#### Type
- JSP page; `text/html;charset=UTF-8`.
- `c` = Jakarta Tags core; `fmt` = Jakarta Tags fmt.

#### Data
- Request attribute `orderFilter`; scriptlet reads `getPageSize()`.
- `${orders}` iterated as `order`; reads `order.number`, `order.total`, `order.overdue`, `order.id`.
- Request parameters: `param.customerEmail`.
- Implicit object: `pageContext.request.contextPath`.

#### Actions
- GET form to `${pageContext.request.contextPath}/orders/search`; fields: `customerEmail` (text), `status` (select: `OPEN`, `SHIPPED`); submit button `Search`.
- Link `orders/${order.id}/cancel` for each order; no HTTP method specified.

#### Structure
- Statically includes `/WEB-INF/jspf/header.jspf`.
- Dynamically includes `/WEB-INF/jspf/footer.jspf`.

#### Server code
- Imports `java.util.List`, `shop.Order`, `shop.OrderFilter`.
- Reads request attribute `orderFilter` as `OrderFilter`; computes `pageSize` using its `getPageSize()` or defaults to 25.

#### Dependencies
- Tag libraries: `jakarta.tags.core`, `jakarta.tags.fmt`.
- Imported classes: `java.util.List`, `shop.Order`, `shop.OrderFilter`.
- Included files: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.
- URLs: `/orders/search`, `orders/${order.id}/cancel`.
