### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:57:27Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Computes byte-frequency histograms over image or stream data, manages a lock-free circular byte queue for producer-consumer transfer, and automates dated directory backups and monthly regional sales reports from CSV exports.

#### Purpose
- Provides low-level byte-stream processing utilities spanning OpenCL histogram reduction, circular buffering, and dated backup/sales-report automation.

#### Responsibilities
- OpenCL byte histogram: kernels/histogram.cl computes a 256-bin byte frequency histogram via per-work-group partial histograms and a global reduction.
- Circular byte buffer: ringbuf/ringbuf.c and ringbuf.h implement a fixed-capacity SPSC circular queue with enqueue, dequeue, and occupancy queries.
- CLI automation: scripts/rotate_backups.sh creates timestamped tar.gz archives and prunes by retention; scripts/sales_report.py parses CSV orders and reports monthly regional revenue above a threshold.

#### Key units
- histogram.cl: OpenCL C 1.2 source with histogram_partial and histogram_reduce kernels; BINS fixed at 256.
- histogram_partial: __kernel building per-work-group partial byte histograms using local memory scratch and atomic increments.
- histogram_reduce: __kernel summing partial slices into a final global 256-bin histogram.
- ringbuf_t: struct in ringbuf.h holding data pointer, capacity, and volatile head/tail indices.
- ringbuf_status_t: enum in ringbuf.h with RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM.
- ringbuf.c: ringbuf_init, ringbuf_free, ringbuf_write, ringbuf_read, ringbuf_used, ringbuf_free_space.
- rotate_backups.sh: Bash utility for SOURCE_DIR archiving, dated final archives, retention pruning, and .partial cleanup via EXIT trap.
- sales_report.py: Python utility with frozen Order dataclass, parse_orders generator, revenue_by_month_and_region aggregator, and main CLI driver.

#### Data flow
- Histogram: host enqueues histogram_partial over an input byte stream to fill per-group partial bins, then enqueues histogram_reduce with one work-item per bin to sum partials into the final histogram buffer.
- Ringbuf: producer calls ringbuf_write to copy bytes in up to two wrap-around chunks and advance head; consumer calls ringbuf_read to copy bytes out in up to two chunks and advance tail; occupancy is polled via ringbuf_used and ringbuf_free_space.
- rotate_backups.sh: source directory is tar-compressed to a .partial staging file, renamed to a dated archive, then archives beyond KEEP are deleted by ls -1t order.
- sales_report.py: CSV rows are read into Order objects, aggregated by (YYYY-MM, region) as Decimal totals, filtered by --min-revenue, and emitted as tab-separated month/region/revenue rows.

#### Dependencies
- kernels/: OpenCL C 1.2 builtins (get_local_id, get_local_size, get_global_id, get_global_size, get_group_id, barrier, atomic_inc, CLK_LOCAL_MEM_FENCE); requires partial buffer of group_count * BINS uints, histogram buffer of BINS uints, and local_bins of BINS uints per group.
- ringbuf/: ringbuf.h for types and status codes; stdlib.h, string.h, stddef.h, stdint.h; extern "C" linkage guard for C++.
- scripts/: rotate_backups.sh depends on bash, tar, gzip, date, ls, mapfile, basename, dirname, mv, rm, mkdir, trap; sales_report.py depends on argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal, decimal.InvalidOperation.
- No internal collaboration between kernels/, ringbuf/, and scripts/.

#### Cross-cutting
- Two-pass reduction pattern: partial per-work-group histograms followed by a single global reduction; BINS fixed at 256 across both kernels.
- SPSC ownership: producer exclusively updates head, consumer exclusively updates tail; both indices volatile for cross-thread visibility.
- Power-of-two capacity with one reserved slot; wrap-around via bitmask (capacity - 1) instead of modulo.
- Non-blocking ringbuf writes/reads clamp to available space or used bytes rather than failing.
- Procedural CLI utilities with explicit argument validation, non-zero exit codes, operational messages to stdout, and errors to stderr.
- rotate_backups.sh uses set -euo pipefail and an EXIT trap; sales_report.py raises ValueError on bad CSV columns or unparseable dates/amounts and returns exit 1 when no orders exist.
