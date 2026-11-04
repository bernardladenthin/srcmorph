### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:43:04Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Provides Hibernate persistence configuration for the shop’s shipping domain, defining entity mappings, queries, and persistence units for both transactional contexts.  

#### Purpose  
- Configure Hibernate mappings for `Shipment` and its subclasses.  
- Define JPA persistence units for shop and reporting data sources.  

#### Responsibilities  
- **Entity mapping** – `Shipment`, `ExpressShipment`, and related associations.  
- **Query definition** – HQL and native SQL for shipment status and carrier statistics.  
- **Persistence unit setup** – JTA and RESOURCE_LOCAL units with data source, provider, and Hibernate properties.  

#### Key units  
- **`Shipment` entity** – maps to `SHIPMENT` table, includes `trackingCode`, `carrier`, `shippedAt`, `order` FK, and `parcels` set.  
- **`ExpressShipment` subclass** – joined to `EXPRESS_SHIPMENT`, adds `deliveryWindow`.  
- **`Shipment.undelivered` HQL** – selects undelivered shipments before a cutoff.  
- **`Shipment.countByCarrier` SQL** – aggregates shipment counts per carrier.  
- **Persistence unit `shop`** – JTA, uses `java:/jdbc/ShopDS`, includes `shop.Order`, `shop.OrderLine`, `shop.Customer`.  
- **Persistence unit `reporting`** – RESOURCE_LOCAL, uses `java:/jdbc/ReportingDS`, includes `shop.reporting.DailyRevenue`.  

#### Data flow  
1. **Entity persistence** – `Shipment` instances are persisted via the `shop` unit; `ExpressShipment` inherits and adds its own table.  
2. **Query execution** – `Shipment.undelivered` and `Shipment.countByCarrier` are invoked through the entity manager to retrieve shipment data.  
3. **Reporting** – `shop.reporting.DailyRevenue` is managed by the `reporting` unit for analytics queries.  

#### Dependencies  
- **Hibernate** – `org.hibernate.jpa.HibernatePersistenceProvider`, DTD `hibernate-mapping-3.0.dtd`.  
- **JPA** – Jakarta Persistence API.  
- **Database** – PostgreSQL (dialect `PostgreSQLDialect`).  
- **Data sources** – `java:/jdbc/ShopDS` (JTA), `java:/jdbc/ReportingDS` (RESOURCE_LOCAL).  
- **Entities** – `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`.  

#### Cross-cutting  
- **Batching** – `hibernate.jdbc.batch_size=50` for efficient inserts.  
- **Sequence generation** – `SHIPMENT_SEQ` for `Shipment.id`.  
- **Cascade strategy** – `all-delete-orphan` on `Shipment.parcels`.  
- **Security** – connection credentials hidden (`***`).  
- **Mapping file inclusion** – `META-INF/order-queries.xml` referenced in `shop` unit.
