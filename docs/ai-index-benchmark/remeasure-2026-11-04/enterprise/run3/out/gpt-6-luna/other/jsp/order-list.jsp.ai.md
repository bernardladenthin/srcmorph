### order-list.jsp
- H: 1.0
- C: 939CAC41
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:20:15Z
- G: 
- A: 0.0.0
- X: file
- P: F6DC35F6
- N: F786E780
---
**Facts (exact, whole file):** page directives: 1; taglib directives: 2; includes and forwards: 2; tag file attributes: 0; scriptlets: 1; expressions: 1; declarations: 0; EL expressions: 7; prefixed tags: 5; forms: 1; input fields: 3; request parameters read: 1

> The open-orders page lets staff search orders by customer email and status, review totals and overdue status, and cancel an order.

#### Purpose
- Displays open orders and provides order search and cancellation controls.

#### Type
- JSP page; `text/html;charset=UTF-8`.
- Tag libraries: `c` = Jakarta Tags core (`jakarta.tags.core`); `fmt` = Jakarta Tags formatting (`jakarta.tags.fmt`).

#### Data
- Request attribute `orderFilter`: calls `getPageSize()` to determine the displayed page size.
- `${orders}`: iterated collection; variable `order` uses `number`, `total`, `overdue`, and `id`.
- `${param.customerEmail}`: populates the customer-email search field.

#### Actions
- Search form: GET `${pageContext.request.contextPath}/orders/search`; fields `customerEmail` (text input) and `status` (select with `OPEN` and `SHIPPED` options).
- Cancel link: `orders/${order.id}/cancel`.

#### Structure
- Includes `/WEB-INF/jspf/header.jspf` via JSP include directive.
- Includes `/WEB-INF/jspf/footer.jspf` via `jsp:include`.

#### Server code
- Imports `java.util.List`, `shop.Order`, and `shop.OrderFilter`.
- Reads request attribute `orderFilter` as `OrderFilter`; sets `pageSize` to its page size, or `25` if the attribute is null.
- Outputs `pageSize`.

#### Dependencies
- Included files: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.
- Classes: `shop.Order`, `shop.OrderFilter`.
