### checkout.xhtml
- H: 1.0
- C: A3D4AA3E
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:41:15Z
- G: 
- A: 0.0.0
- X: file
- P: C9F70393
- N: 2F819391
---
**Facts (exact, whole file):** elements: 14; namespace declarations: 4; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; EL expressions: 7; forms: 1; input fields: 4; EL method bindings: 2; templates and includes: 2; composite component interfaces: 0

> A checkout page that collects shipping address, selects a shipping method, shows the total, and places an order.

#### Purpose
- Collect shipping address and method.
- Display calculated total.
- Submit order.

#### Type
- Facelets page (`.xhtml`), template-based.
- Uses namespaces: `h` = `jakarta.faces.html`, `f` = `jakarta.faces.core`, `ui` = `jakarta.faces.facelets`.

#### Data
- `#{checkoutBean.address.street}` – input value.
- `#{checkoutBean.address.zip}` – input value, validated with regex `[0-9]{5}`.
- `#{checkoutBean.shippingMethod}` – selected value.
- `#{checkoutBean.shippingMethods}` – collection for `<f:selectItems>`.
- `#{checkoutBean.total}` – displayed total, formatted as EUR currency.
- `#{checkoutBean.recalculate}` – AJAX listener for shipping change.
- `#{checkoutBean.placeOrder}` – form submission action.

#### Actions
- `<h:form id="checkoutForm">` submits to `#{checkoutBean.placeOrder}` via `h:commandButton`.
- `<f:ajax listener="#{checkoutBean.recalculate}" render="total">` updates total when shipping changes.
- Commented `<h:commandButton value="Save cart" action="#{checkoutBean.saveCart}"/>` (inactive).

#### Structure
- Extends template `/WEB-INF/templates/shop.xhtml`.
- Defines `content` section.
- Includes `/WEB-INF/includes/payment-hints.xhtml` after the form.

#### Server code
- No scriptlets or expressions beyond EL.

#### Dependencies
- Tag libraries: `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`.
- Template: `/WEB-INF/templates/shop.xhtml`.
- Include: `/WEB-INF/includes/payment-hints.xhtml`.
- Managed bean: `checkoutBean` (assumed to be in application scope).
