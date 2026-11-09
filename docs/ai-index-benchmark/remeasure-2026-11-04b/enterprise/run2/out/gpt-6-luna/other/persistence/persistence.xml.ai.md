### persistence.xml
- H: 1.0
- C: 24ACE2B2
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:30:11Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 1; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 2; JTA data sources: 1; non-JTA data sources: 1; listed classes: 4; entity mappings: 0; embeddables and mapped superclasses: 0; named queries: 0; associations: 0; mapping files: 1; properties: 4

> Defines JTA persistence for shop orders and customers, plus resource-local persistence for reporting data.

#### Purpose
- Configures two Jakarta Persistence units with separate data sources and entity classes.

#### Type
- Jakarta Persistence descriptor (`persistence.xml`); root element `persistence`.
- Namespace: default `https://jakarta.ee/xml/ns/persistence`.

#### Definitions
- Persistence unit `shop`: transaction type `JTA`; provider `org.hibernate.jpa.HibernatePersistenceProvider`; entities `shop.Order`, `shop.OrderLine`, `shop.Customer`; mapping file `META-INF/order-queries.xml`.
- Persistence unit `reporting`: transaction type `RESOURCE_LOCAL`; entity `shop.reporting.DailyRevenue`; unlisted classes excluded.

#### Configuration
- `shop`: JTA data source `java:/jdbc/ShopDS`.
- `shop`: `hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect`; `hibernate.jdbc.batch_size=50`; `hibernate.connection.username=shop_app`; `hibernate.connection.password=***`.
- `reporting`: non-JTA data source `java:/jdbc/ReportingDS`; `exclude-unlisted-classes=true`.

#### Dependencies
- Mapping file: `META-INF/order-queries.xml`.
- Provider class: `org.hibernate.jpa.HibernatePersistenceProvider`.
- Entity classes: `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`.
- Data sources: `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`.
- Hibernate dialect class: `org.hibernate.dialect.PostgreSQLDialect`.
