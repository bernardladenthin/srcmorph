### order-list.jsp
- H: 1.0
- C: 939CAC41
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:41:30Z
- G: 
- A: 0.0.0
- X: file
- P: C9F70393
- N: F786E780
---
**Facts (exact, whole file):** page directives: 1; taglib directives: 2; includes and forwards: 2; tag file attributes: 0; scriptlets: 1; expressions: 1; declarations: 0; EL expressions: 7; prefixed tags: 5; forms: 1; input fields: 3; request parameters read: 1

> Displays a list of open orders with search and cancel options, showing order number, total, overdue status, and page size.

#### Purpose
- List open orders.
- Provide search by customer email and status.
- Allow cancellation of individual orders.

#### Type
- JSP page
- Content type: `text/html;charset=UTF-8`
- Tag libraries: `c` = `jakarta.tags.core`, `fmt` = `jakarta.tags.fmt`

#### Data
- `request` attribute `orderFilter` → `OrderFilter filter`
- `filter.getPageSize()` used for page size display
- `request` attribute `orders` → iterated as `${orders}` with var `order`
- `order.number`, `order.total`, `order.overdue`, `order.id`
- Request parameters: `param.customerEmail`, `param.status`

#### Actions
- Form: `action="${pageContext.request.contextPath}/orders/search"`, method `get`
  - Inputs: `customerEmail`, `status`
- Link: `href="orders/${order.id}/cancel"` for each order

#### Structure
- Includes header: `/WEB-INF/jspf/header.jspf`
- Includes footer: `/WEB-INF/jspf/footer.jspf`

#### Server code
- Scriptlet imports: `java.util.List`, `shop.Order`, `shop.OrderFilter`
- Scriptlet retrieves `OrderFilter` from request and computes `pageSize`

#### Dependencies
- Tag libraries: `jakarta.tags.core`, `jakarta.tags.fmt`
- Included files: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`
- Beans/Classes: `shop.Order`, `shop.OrderFilter`
