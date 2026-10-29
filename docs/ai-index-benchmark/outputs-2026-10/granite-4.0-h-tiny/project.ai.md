### srcmorph-modelcard
- H: 1.0
- C: 0C452FAB
- D: 2026-10-29T00:07:37Z
- T: 2026-10-29T00:10:46Z
- G: 
- A: 0.0.0
- X: project
- F: [java/config](java/config/package.ai.md)
- F: [java](java/package.ai.md)
- F: [java/provider](java/provider/package.ai.md)
- F: [other/kernels](other/kernels/package.ai.md)
- F: [other](other/package.ai.md)
- F: [other/ringbuf](other/ringbuf/package.ai.md)
- F: [other/scripts](other/scripts/package.ai.md)
- F: [.](package.ai.md)
- F: [sql/library](sql/library/package.ai.md)
- F: [sql](sql/package.ai.md)
- F: [sql/shop](sql/shop/package.ai.md)
- F: [sql/warehouse](sql/warehouse/package.ai.md)
---
> Project index of srcmorph-modelcard: one line per package; leads here, clickable links in the header F list.

#### Overview
The project is organized around core AI-related subsystems and data management components. The java/config and java packages handle AI condition evaluation and configuration management, including VAT calculations and generation workflows. Provider and other/kernels focus on AI generation capabilities, with kernels computing histograms and other components offering thread-safe data storage. The sql packages provide relational models for lending libraries, web shops, and inventory management, enforcing business rules and supporting reporting. Automated backup and revenue reporting are managed by the other/scripts package.

#### Packages
- java/config — A package for evaluating and applying AI conditions to file contexts, calculating VAT for invoices, and managing AI generation configurations.
- java — A package for evaluating and applying AI conditions to file contexts, calculating VAT for invoices, and managing AI generation configurations.
- java/provider — Provides AI generation capabilities for local and remote models, with pluggable implementations for different providers.
- other/kernels — A pure OpenCL kernel for computing a 256-bin byte histogram of an input byte stream in two passes.
- other — A package providing a pure OpenCL histogram kernel, a thread-safe ring buffer, and scripts for automated backups and sales revenue reporting.
- other/ringbuf — A fixed-capacity byte ring buffer for single-producer, single-consumer scenarios, providing thread-safe data storage and retrieval.
- other/scripts — Automated backup creation and sales revenue reporting package
- . — ## Purpose
- sql/library — A PostgreSQL-based lending library schema that provides relational models for authors, books, members, and loan transactions, enforces business rules (free‑copy limits, loan validity), and includes reporting views and audit logging.
- sql — A comprehensive package that provides relational models for a lending library, a web‑shop domain model, and a warehouse inventory synchronization system, each enforcing business rules, handling data flow through transactions, and offering reporting and audit capabilities.
- sql/shop — A MySQL-based web‑shop domain model for managing products, customers, and order placement, including order items and stock reservation.
- sql/warehouse — A SQL Server T‑SQL package that merges supplier feed data into inventory tables and logs sync outcomes.
