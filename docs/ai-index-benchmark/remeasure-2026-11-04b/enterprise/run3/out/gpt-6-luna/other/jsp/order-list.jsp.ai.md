### order-list.jsp
- H: 1.0
- C: 939CAC41
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:38:17Z
- G: 
- A: 0.0.0
- X: file
- P: C9F70393
- N: F786E780
---
**Facts (exact, whole file):** page directives: 1; taglib directives: 2; includes and forwards: 2; tag file attributes: 0; scriptlets: 1; expressions: 1; declarations: 0; EL expressions: 7; prefixed tags: 5; forms: 1; input fields: 3; request parameters read: 1

> The open-orders page lets staff search orders by customer email and status, review order totals and overdue status, and cancel an order.

#### Purpose
- Display orders and provide search and cancellation controls.

#### Type
- JSP page; `text/html;charset=UTF-8`.
- Tag libraries: `c` = Jakarta Tags core; `fmt` = Jakarta Tags format.

#### Data
- Request attribute `orderFilter`: calls `getPageSize()` to display page size; defaults to `25` if absent.
- `${orders}`: iterated as `order`; reads `order.number`, `order.total`, `order.overdue`, and `order.id`.
- Request parameter `param.customerEmail`: prepopulates the customer email field.
- `${pageContext.request.contextPath}`: form action context path.

#### Actions
- GET form to `${pageContext.request.contextPath}/orders/search`; fields: `customerEmail` (text), `status` (select with `OPEN` and `SHIPPED` options).
- Cancel link for each order: `orders/${order.id}/cancel`.

#### Structure
- Includes `/WEB-INF/jspf/header.jspf` via JSP include directive.
- Includes `/WEB-INF/jspf/footer.jspf` via `jsp:include`.

#### Server code
- Imports `java.util.List`, `shop.Order`, and `shop.OrderFilter`; only `OrderFilter` is used.
- Reads request attribute `orderFilter`, casts it to `OrderFilter`, and computes the displayed page size using `getPageSize()` or default `25`.

#### Dependencies
- `/WEB-INF/jspf/header.jspf`
- `/WEB-INF/jspf/footer.jspf`
- `shop.OrderFilter`
- `/orders/search`
- `orders/${order.id}/cancel`
