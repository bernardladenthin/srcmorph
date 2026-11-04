### persistence.xml
- H: 1.0
- C: 24ACE2B2
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:20:45Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 1; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 2; JTA data sources: 1; non-JTA data sources: 1; listed classes: 4; entity mappings: 0; embeddables and mapped superclasses: 0; named queries: 0; associations: 0; mapping files: 1; properties: 4

> Defines the `shop` JTA persistence unit and a separate resource-local persistence unit for reporting.

#### Purpose
- Configures Hibernate persistence for shop orders and customers using a JTA data source.
- Defines a reporting persistence unit using a non-JTA data source.

#### Type
- JPA `persistence.xml`; root element: `persistence`.
- Namespace: default `https://jakarta.ee/xml/ns/persistence`.

#### Definitions
- Persistence unit `shop`: transaction type `JTA`; provider `org.hibernate.jpa.HibernatePersistenceProvider`; data source `java:/jdbc/ShopDS`; mapping file `META-INF/order-queries.xml`; managed classes `shop.Order`, `shop.OrderLine`, `shop.Customer`.
- Persistence unit `reporting`: transaction type `RESOURCE_LOCAL`; non-JTA data source `java:/jdbc/ReportingDS`; managed class `shop.reporting.DailyRevenue`; `exclude-unlisted-classes=true`.

#### Configuration
- `hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect`
- `hibernate.jdbc.batch_size=50`
- `hibernate.connection.username=shop_app`
- `hibernate.connection.password=***`

#### Dependencies
- Mapping file: `META-INF/order-queries.xml`
- Data sources: `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`
- Provider class: `org.hibernate.jpa.HibernatePersistenceProvider`
- Entity classes: `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`
