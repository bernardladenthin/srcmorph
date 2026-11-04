### persistence.xml
- H: 1.0
- C: 24ACE2B2
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:38:52Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 1; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 2; JTA data sources: 1; non-JTA data sources: 1; listed classes: 4; entity mappings: 0; embeddables and mapped superclasses: 0; named queries: 0; associations: 0; mapping files: 1; properties: 4

> Defines JTA persistence for shop orders and customers, plus a resource-local persistence unit for reporting data.

#### Purpose
- Configures two Jakarta Persistence units for application and reporting data access.
- Registers entity classes, a mapping file, data sources, and Hibernate settings.

#### Type
- Jakarta Persistence descriptor (`persistence.xml`); root element: `persistence`.
- Declares the default namespace `https://jakarta.ee/xml/ns/persistence` with no prefix.

#### Definitions
- Persistence unit `shop`: `JTA`; provider `org.hibernate.jpa.HibernatePersistenceProvider`; data source `java:/jdbc/ShopDS`.
  - Mapping file: `META-INF/order-queries.xml`.
  - Classes: `shop.Order`, `shop.OrderLine`, `shop.Customer`.
- Persistence unit `reporting`: `RESOURCE_LOCAL`; non-JTA data source `java:/jdbc/ReportingDS`.
  - Class: `shop.reporting.DailyRevenue`.
  - Excludes unlisted classes.

#### Configuration
- `shop` properties:
  - `hibernate.dialect` = `org.hibernate.dialect.PostgreSQLDialect`
  - `hibernate.jdbc.batch_size` = `50`
  - `hibernate.connection.username` = `shop_app`
  - `hibernate.connection.password` = `***`

#### Dependencies
- Data sources: `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`.
- Provider class: `org.hibernate.jpa.HibernatePersistenceProvider`.
- Entity classes: `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`.
- Mapping file: `META-INF/order-queries.xml`.
- Database dialect: `org.hibernate.dialect.PostgreSQLDialect`.
