### ai
- H: 1.0
- C: FFA8A94C
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:02:21Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package provides AI condition evaluation, fact extraction, and generation configuration for source morphing, OpenCL-based byte frequency histogram computation with ring buffers, and SQL data management for lending, e-commerce, and warehouse stock synchronization.

#### Purpose
- Java package: AI condition evaluation, fact extraction, generation configuration, oversize handling, and configuration management for source morphing.
- Other package: OpenCL kernels for byte frequency histogram computation, ring buffer streaming, backup rotation, and sales revenue reporting.
- SQL package: PostgreSQL lending library, MySQL e-commerce order system, and SQL Server warehouse stock synchronization.

#### Responsibilities
- Java: AiConditionEvaluator evaluates AiCondition trees against AiFileContext; AiConditionGroup holds child conditions with defensive copying; AiFactExtractor generates fact counts; AiGenerationConfig and SrcMorphConfiguration hold generation parameters; AiOversizeStrategy selects oversize handling strategies; AiCompletionParser strips thinking blocks; AiGenerationProvider, LlamaCppJniAiGenerationProvider, Mock, OpenAiCompatibleProviderSupport provide generation backends; AiGenerationProviderFactory selects provider implementations; LlamaCppJniConfig holds immutable config.
- Other: histogram_partial and histogram_reduce kernels compute per-workgroup histograms and aggregate; ringbuf_t and ringbuf_status_t manage ring buffer lifecycle; rotate_backups.sh rotates backups; sales_report.py generates sales reports.
- SQL: Library schema and PL/pgSQL routines for checkout, return, late fees, and audit; Shop product/order tables and order_item stored procedure; Warehouse dbo.usp_SyncStock merges supplier feed and logs.

#### Key units
- Java: AiConditionEvaluator, AiConditionGroup, AiFactExtractor, AiGenerationConfig, AiOversizeStrategy, SrcMorphConfiguration, AiCompletionParser, AiGenerationProvider, AiGenerationProviderFactory, LlamaCppJniAiGenerationProvider, LlamaCppJniConfig, OpenAiCompatibleProviderSupport.
- Other: histogram_partial, histogram_reduce, ringbuf_t, ringbuf_status_t, ringbuf_init, ringbuf_free, ringbuf_write, ringbuf_read, ringbuf_used, ringbuf_free_space, rotate_backups.sh, sales_report.py.
- SQL: author, book, member, loan, loan_audit, branch, book_copy, product, customer_order, order_item, dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog, overdue_loans, member_loan_stats, popular_books, idx_popular_books_id.

#### Data flow
- Java: AiFactExtractor produces facts from source code; AiCondition trees evaluated by AiConditionEvaluator with AiFileContext; AiConditionGroup stores conditions; AiGenerationConfig and SrcMorphConfiguration supply parameters; AiOversizeStrategy handles oversized files; raw completion text parsed by AiCompletionParser and generated via AiGenerationProvider; provider selected by AiGenerationProviderFactory.
- Other: Pass 1 of kernels resets local_bins and increments counters; Pass 2 sums partial histograms; ringbuf_init allocates buffer; ringbuf_write/read copy data; rotate_backups.sh creates dated archives; sales_report.py parses CSV and aggregates.
- SQL: Library seed inserts rows; checkout/return routines update copies and loan; views join tables; warehouse MERGE feeds into dbo.StockLevel and logs in dbo.SyncLog.

#### Dependencies
- Java: java.time.Instant, java.time.format.DateTimeParseException, java.util.Collections, List, net.ladenthin.srcmorph.support.AiSourceExcludeFilter, org.jspecific.annotations.Nullable, org.jspecify.annotations.Nullable, java.io.File, lombok.ToString, net.ladenthin.srcmorph.prompt.AiPromptDefinition, net.ladenthin.srcmorph.config.AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition, AiGenerationConfig.
- External providers: net.ladenthin.srcmorph.config (AiCondition, AiFileContext, AiConditionGroup, AiGenerationConfig), net.ladenthin.srcmorph.support (AiSourceExcludeFilter), org.jspecific (annotations), org.jspecify (annotations), lombok, net.ladenthin.srcmorph.document (AiGenerationRequest), net.ladenthin.srcmorph.prompt (AiPromptSupport), net.ladenthin.srcmorph.support (Java8CompatibilityHelper), net.ladenthin.llama.value (ChatResponse, Usage), com.fasterxml.jackson.databind (ObjectMapper, ArrayNode, ObjectNode), java.io, java.util, java.net.
- Other: OpenCL C 1.2 runtime, atomic_inc, OpenCL built-ins, ringbuf.h, stdlib.h, string.h, stdint.h, stddef.h, Bash built-ins, tar, date, ls, du, Python modules argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal, decimal.InvalidOperation.
- SQL: PL/pgSQL, PostgreSQL runtime, MySQL 8 syntax, system functions SYSUTCDATETIME, IDENTITY, ERROR_MESSAGE, THROW.

#### Cross-cutting
- Stateless final classes (AiConditionEvaluator, AiOversizeStrategy) are thread-safe by immutability; mutable beans (AiGenerationConfig, SrcMorphConfiguration) are not thread-safe.
- Defensive copying in AiConditionGroup.setConditions and AiFactExtractor.
- Null handling via @Nullable annotations and null-safe defaults; IllegalArgumentException for invalid configurations or malformed dates.
- Provider side: stateless design, immutable config after construction, lazy model loading, null input as empty string, null config throws NullPointerException.
- Other: Two-pass algorithm with __global and __local memory; atomic increment for thread-safe counters; ring buffer power-of-two capacity; single-producer single-consumer semantics.
- SQL: Constraints, audit trail, late fee formula min(max(overdue_days*50,0),2000), foreign keys, transactional BEGIN/COMMIT/ROLLBACK, row-level locking SELECT ... FOR UPDATE.
