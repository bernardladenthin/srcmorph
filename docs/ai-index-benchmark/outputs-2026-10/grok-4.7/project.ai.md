### srcmorph-modelcard
- H: 1.0
- C: 54FC1043
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:48:15Z
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
Synthesizing a concise project overview from the per-package one-line leads.

The java/config lead is the only substantive package description. The java lead is meta noise. I will extract content from that single lead.

The java/provider lead is substantive. It describes local llama.cpp and OpenAI-compatible backends that turn source-morph requests into cleaned completion text and model timings. The other package leads are meta noise or lack content.

The sql lead describes three independent databases for lending, web-shop checkout, and nightly warehouse stock sync. The sql/shop lead covers products with stock, customer orders, line items, and order-line stock reservation. The sql/warehouse lead covers nightly supplier feed merging into on-hand stock levels.

The other/kernels package builds a 256-bin byte histogram using two-pass OpenCL partial-count and reduction. Ringbuf and scripts lack substantive behavioral leads, so they will not be described.

The project name is srcmorph-modelcard. The overview will be two to four sentences. It will describe the Java generation pipeline, the OpenCL byte analysis, and the three SQL databases.

The overview now spans four sentences. It names the three SQL databases and their specific behaviors. The root lead only lists the subpackages, so no causal links between them are invented.

The sql lead names lending circulation, web-shop checkout, and nightly warehouse stock sync. The library subpackage is not explicitly described in its own lead. I will attribute those three purposes to the sql package without naming library as the source.The Java side configures a srcmorph run: java/config chooses which sources are indexed, how oversized files are treated, and which model, sampling, and exact-fact settings drive `.ai.md` generation, while java/provider selects local llama.cpp or OpenAI-compatible backends that turn those generation requests into cleaned completion text and model timings. Separately, other/kernels builds a 256-bin byte histogram of an image or byte stream with a two-pass OpenCL partial-count and reduction. The sql tree holds three independent databases for lending circulation, web-shop checkout, and nightly warehouse stock sync. sql/shop covers catalog products with stock, customer orders, line items, and placing an order line that reserves stock and updates the order total; sql/warehouse merges a supplier feed into on-hand stock and logs the outcome.

#### Packages
- java/config — Synthesizing a package summary from the provided per-file summaries.> Configures and evaluates which sources a srcmorph run indexes, how oversized files are treated, and which model, sampling, and exact-fact settings drive `.ai.md` generation.
- java — Synthesizing a package summary from the provided per-file summaries.
- java/provider — I am synthesizing the per-file summaries into a dense structured markdown overview of the package.> Selects and runs local llama.cpp or OpenAI-compatible backends that turn source-morph generation requests into cleaned completion text and model timings.
- other/kernels — Builds a 256-bin byte histogram of an image or byte stream with a two-pass OpenCL partial-count and reduction.
- other — Synthesizing a package-level summary from the provided per-file and subdirectory summaries.
- other/ringbuf — I will produce a dense, structured markdown summary of the ringbuf package based on the per-file summaries.
- other/scripts — I will produce a dense structured markdown summary of the package based on the per-file summaries.
- . — Synthesizing a package summary from the per-file summaries for the ai package, which contains java/, other/, and sql/ subpackages.
- sql/library — Synthesizing the sql/library package summary from its per-file breakdowns.
- sql — Synthesizing the sql package summary from its library, shop, and warehouse subpackages.> Three independent SQL databases for lending circulation, web-shop checkout, and nightly warehouse stock sync.
- sql/shop — I'm compiling a dense structured markdown summary of the package from the provided per-file summaries.> Web-shop catalog and checkout: products with stock, customer orders, line items, and placing an order line that reserves stock and updates the order total.
- sql/warehouse — I will produce a dense, structured markdown summary of the package based on the provided file summaries.> Nightly warehouse stock synchronisation that merges a supplier feed into on-hand stock levels and logs the outcome.
