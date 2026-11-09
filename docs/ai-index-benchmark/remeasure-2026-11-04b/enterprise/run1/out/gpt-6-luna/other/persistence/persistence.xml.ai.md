### persistence.xml
- H: 1.0
- C: 24ACE2B2
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:23:42Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 1; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 2; JTA data sources: 1; non-JTA data sources: 1; listed classes: 4; entity mappings: 0; embeddables and mapped superclasses: 0; named queries: 0; associations: 0; mapping files: 1; properties: 4

> Defines JTA persistence for shop orders and customers, plus a resource-local persistence unit for reporting data.

#### Purpose
- Configures two Jakarta Persistence units with separate data sources and managed classes.
- Supplies Hibernate settings and an external named-query mapping for the shop unit.

#### Type
- Jakarta Persistence descriptor; root element `persistence`.
- Namespace: default `https://jakarta.ee/xml/ns/persistence`.

#### Definitions
- Persistence unit `shop`: transaction type `JTA`; provider `org.hibernate.jpa.HibernatePersistenceProvider`; JTA data source `java:/jdbc/ShopDS`; mapping file `META-INF/order-queries.xml`; managed classes `shop.Order`, `shop.OrderLine`, `shop.Customer`.
- Persistence unit `reporting`: transaction type `RESOURCE_LOCAL`; non-JTA data source `java:/jdbc/ReportingDS`; managed class `shop.reporting.DailyRevenue`; excludes unlisted classes.

#### Configuration
- `hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect`
- `hibernate.jdbc.batch_size=50`
- `hibernate.connection.username=shop_app`
- `hibernate.connection.password=***`

#### Dependencies
- Mapping file: `META-INF/order-queries.xml`
- Data sources: `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`
- Provider class: `org.hibernate.jpa.HibernatePersistenceProvider`
- Managed classes: `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`
