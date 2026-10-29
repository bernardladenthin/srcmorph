### other
- H: 1.0
- C: 142CEE21
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:12:04Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> This package provides parallel histogram computation, fixed-capacity byte buffering, and directory/CSV-based revenue aggregation for data processing workflows.

#### Purpose
- Provides OpenCL kernel implementations for parallel 256-bin byte-level histogram computation from image or byte-stream data.
- Implements a fixed-capacity single-producer single-consumer ring buffer for byte data transfer and backup rotation with monthly revenue aggregation from CSV order exports.

#### Responsibilities
- Accumulate per-work-group partial histograms into a global buffer via OpenCL local memory and atomic operations, then reduce across all groups into a final histogram.
- Manage a fixed-capacity circular buffer with power-of-two constraint, wrap-around index arithmetic, and one-slot reservation to distinguish full from empty states.
- Create dated tar.gz backup archives and prune oldest copies beyond a retained count; parse CSV order data and aggregate revenue by month and region with currency and threshold filtering.

#### Key units
- histogram_partial: OpenCL kernel that accumulates partial histograms into a global buffer per work-group using atomic_inc and local memory fencing.
- histogram_reduce: OpenCL kernel that reduces partial histograms into the final 256-bin histogram with an out-of-bounds guard bin >= BINS.
- BINS 256: constant defining the number of histogram bins.
- ringbuf_t: struct holding buffer data, capacity, head, tail indices under power-of-two capacity constraint.
- ringbuf_status_t: status type with RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM.
- ringbuf_init: allocates buffer and validates power-of-two capacity.
- ringbuf_write: copies bytes into buffer with wrap-around, returns actual bytes written.
- ringbuf_read: copies bytes from buffer with wrap-around, returns actual bytes read.
- ringbuf_used: computes stored byte count via (head - tail) & (capacity - 1).
- revenue_by_month_and_region: aggregator that totals revenue by (month, region) from order sequences filtered by currency.
- parse_orders: generator that yields orders filtered by currency from CSV paths.
- rotate_backups.sh: script that creates dated tar.gz archives and prunes oldest beyond KEEP count.

#### Data flow
- Histogram: input byte stream enters histogram_partial, work-groups zero local counters with CLK_LOCAL_MEM_FENCE, accumulate byte counts with atomic_inc, fence, write per-group partial slices to partial, then histogram_reduce sums partial[g * BINS + bin] into histogram[bin] with bin >= BINS guard.
- Ring buffer: producer calls ringbuf_write with source data and length; data wraps into ringbuf_t.data via head index advancement; consumer calls ringbuf_read with destination buffer; data wraps via tail index advancement; stored byte count derived from head/tail positions with bitmask wrap-around.
- Revenue reporting: CSV files feed parse_orders with currency filter, yielding order dataclasses that revenue_by_month_and_region accumulates by (month, region), output to stdout/stderr; backup rotation: SOURCE_DIR → tar.gz archive → BACKUP_DIR → prune oldest archives → stdout status.

#### Dependencies
- OpenCL C 1.2 runtime and atomic_inc operation for per-byte accumulation in histogram kernels.
- ringbuf.h: type and status declarations, <stdlib.h> malloc/free, <string.h> memcpy, <stddef.h> size_t, <stdint.h> uint8_t, C extern "C" linkage for C++ compatibility.
- sales_report.py: argparse, csv, sys, collections, dataclasses, datetime, decimal; rotate_backups.sh: tar, ls, mapfile, du, basename, dirname, date, mv, rm, set -euo pipefail.
- Types: uchar, uint, uint8_t; constant: BINS 256; buffers: partial, local_bins, histogram, ringbuf_t.data.

#### Cross-cutting
- Two-pass reduction pattern with CLK_LOCAL_MEM_FENCE synchronization between passes in histogram kernels.
- atomic_inc used for concurrent byte-level counter increments; out-of-bounds guard bin >= BINS prevents out-of-range access.
- Power-of-two capacity validation enforced in ringbuf_init; one-slot reservation pattern to distinguish full from empty buffer state.
- Error handling via return codes (RINGBUF_EINVAL, RINGBUF_ENOMEM) rather than exceptions in ringbuf; exit codes/status streams for error reporting in backup and revenue modules.
- Input validation and strict error handling patterns across all subpackages; trap for partial archive cleanup on EXIT in rotate_backups.sh; ValueError with file path and line number context in sales_report.py.
- Bitmask wrap-around arithmetic for index management in ringbuf; symmetrical write/read operations with dual-chunk copy logic for wrap-around.
