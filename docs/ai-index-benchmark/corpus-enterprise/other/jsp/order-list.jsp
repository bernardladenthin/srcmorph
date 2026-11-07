<%--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: Apache-2.0
--%>
<%@ page contentType="text/html;charset=UTF-8" import="java.util.List,shop.Order,shop.OrderFilter" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%-- The CSV export link was removed: <a href="export.csv?status=${param.status}">CSV</a> --%>
<%
    OrderFilter filter = (OrderFilter) request.getAttribute("orderFilter");
    int pageSize = filter == null ? 25 : filter.getPageSize();
%>
<h1>Open orders</h1>
<form action="${pageContext.request.contextPath}/orders/search" method="get">
    <input type="text" name="customerEmail" value="${param.customerEmail}"/>
    <select name="status">
        <option value="OPEN">open</option>
        <option value="SHIPPED">shipped</option>
    </select>
    <input type="submit" value="Search"/>
</form>
<table>
    <c:forEach items="${orders}" var="order">
        <tr>
            <td><c:out value="${order.number}"/></td>
            <td><fmt:formatNumber value="${order.total}" type="currency"/></td>
            <td><c:if test="${order.overdue}">overdue</c:if></td>
            <td><a href="orders/${order.id}/cancel">cancel</a></td>
        </tr>
    </c:forEach>
</table>
<p>Page size: <%= pageSize %></p>
<jsp:include page="/WEB-INF/jspf/footer.jspf"/>
