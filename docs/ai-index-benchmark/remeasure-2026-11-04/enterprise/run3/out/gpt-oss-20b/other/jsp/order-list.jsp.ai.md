### order-list.jsp
- H: 1.0
- C: 939CAC41
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:23:35Z
- G: 
- A: 0.0.0
- X: file
- P: F6DC35F6
- N: F786E780
---
**Facts (exact, whole file):** page directives: 1; taglib directives: 2; includes and forwards: 2; tag file attributes: 0; scriptlets: 1; expressions: 1; declarations: 0; EL expressions: 7; prefixed tags: 5; forms: 1; input fields: 3; request parameters read: 1

> Displays a searchable list of open orders, allowing filtering by customer email and status, and providing a cancel link for each order.

#### Purpose
- Show open orders with total and overdue status.
- Enable search by customer email and status.
- Offer cancel action per order.

#### Type
- JSP page
- Content type: `text/html;charset=UTF-8`
- Tag libraries:
  - `c` = `jakarta.tags.core`
  - `fmt` = `jakarta.tags.fmt`

#### Data
- Request attribute: `orderFilter` → `OrderFilter` (used for `pageSize`)
- Request attribute: `orders` → `List<Order>` (iterated with `var="order"`)
- Request parameter: `customerEmail` (used in search form)
- Request parameter: `status` (used in search form)
- Order properties accessed:
  - `${order.number}`
  - `${order.total}`
  - `${order.overdue}`
  - `${order.id}`

#### Actions
- Search form:
  - `action`: `${pageContext.request.contextPath}/orders/search`
  - `method`: `get`
  - Fields:
    - `customerEmail` (text)
    - `status` (select with values `OPEN`, `SHIPPED`)
- Cancel link per order:
  - URL: `orders/${order.id}/cancel`

#### Structure
- Includes:
  - `/WEB-INF/jspf/header.jspf`
  - `/WEB-INF/jspf/footer.jspf`

#### Server code
- Scriptlet:
  - Retrieves `OrderFilter` from request attribute `orderFilter`.
  - Determines `pageSize` (default 25 if filter is null).
- Expression:
  - `<%= pageSize %>` outputs the page size.

#### Dependencies
- Java classes:
  - `java.util.List`
  - `shop.Order`
  - `shop.OrderFilter`
- Tag libraries:
  - `jakarta.tags.core`
  - `jakarta.tags.fmt`
- Included JSP fragments:
  - `/WEB-INF/jspf/header.jspf`
  - `/WEB-INF/jspf/footer.jspf`
