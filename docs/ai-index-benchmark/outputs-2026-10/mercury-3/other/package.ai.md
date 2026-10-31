### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:33Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Embedded/edge byte-processing substrate: OpenCL GPU histogram kernels for byte-stream analysis, a lock-free power-of-two ring buffer for producer/consumer byte transport, and standalone shell/Python operational CLI utilities.

#### Purpose
- Bundles three self-contained low-level building blocks: GPU-accelerated 256-bin byte histograms, a lock-free single-producer/single-consumer byte FIFO, and ops scripts (backup rotation, revenue reporting).
- Each area is independent; no code is shared across the three subpackages.

#### Responsibilities
- GPU histogram computation (`kernels/histogram.cl`): two-pass parallel reduction over raw byte streams (images/raw buffers) into 256 bins.
- Lock-free byte streaming (`ringbuf/`): fixed-capacity, power-of-two circular FIFO for UART/DMA/audio/inter-thread/interrupt transfer with non-blocking bounded read/write.
- Backup retention (`scripts/rotate_backups.sh`): timestamped tar.gz creation, keep-N pruning, atomic archive finalization.
- Sales reporting (`scripts/sales_report.py`): CSV ingestion, column/currency validation, month/region revenue aggregation, threshold-based output.

#### Key units
- `histogram_partial` — first-pass OpenCL kernel; per work-group local bin counting via `atomic_inc`, writing a 256-bin slice per group.
- `histogram_reduce` — second-pass OpenCL kernel; one work-item per bin sums group partials into the final histogram.
- `ringbuf_t` — struct with `data`, `capacity`, volatile `head`/`tail`; the core FIFO state.
- `ringbuf_init` / `ringbuf_free` — lifecycle API; validates power-of-two capacity, allocates/frees storage.
- `ringbuf_write` / `ringbuf_read` — non-blocking copy paths wrapping via `(capacity - 1)` mask; silently truncate when full/empty.
- `ringbuf_used` / `ringbuf_free_space` — occupancy queries; free space reserves one slot for full/empty disambiguation.
- `rotate_backups.sh` — Bash CLI; arg validation, archive creation, stale archive pruning.
- `Order` — frozen dataclass (`order_date`, `region`, `amount`) for parsed CSV rows.
- `parse_orders(path, currency)` — generator yielding `Order` records, skipping mismatched-currency rows.
- `revenue_by_month_and_region(orders)` — aggregates `Decimal` revenue keyed by `(month, region)`, chronologically sorted.
- `main(argv=None)` — Python entry point returning exit code and printing `month<TAB>region<TAB>revenue`.

#### Data flow
- Byte buffer → `histogram_partial(input, length, partial, local_bins)` → `partial[group_id * BINS + bin]` → `histogram_reduce(partial, group_count, histogram)` → final 256-bin histogram.
- Producer: `ringbuf_write(rb, src, len)` → copies into buffer at `head`, advances via mask; Consumer: `ringbuf_read(rb, dst, len)` → copies from `tail`, advances via mask.
- Backup: `SOURCE_DIR` → `tar -C` on parent → staging `.partial` file → `mv` atomic finalization → `mapfile` from `ls -1t` (newest first) → delete archives beyond KEEP.
- Reporting: CSV(s) → `parse_orders` (column check, currency filter, date/amount parsing) → `revenue_by_month_and_region` → `main` prints buckets ≥ min revenue.

#### Dependencies
- Kernels: OpenCL built-ins `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `get_local_id/size`, `get_global_id/size`, `get_group_id`; macro `BINS` = 256.
- Ring buffer: `ringbuf.h` declarations consumed by `ringbuf.c`; stdlib `malloc`/`free`, `memcpy`, `size_t`, `uint8_t`; `extern "C"` guard.
- Scripts (backup): `tar`, `date`, `ls`, `du`, `cut`, `mv`, `rm`, `mkdir`, `basename`, `dirnamename; Bash builtins `mapfile`, `trap`, `[[ ]]`.
- Scripts (reporting): stdlib `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime.date`, `decimal.Decimal` / `decimal.InvalidOperation`.

#### Cross-cutting
- Kernels: no explicit error handling; correctness relies on OpenCL local-memory fencing and invocation semantics.
- Ring buffer: power-of-two capacity invariant enforced at init; volatile `head`/`tail` for lock-free coordination; no NULL-pointer guards; errors only from init (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- Scripts: both CLI-only, self-contained, return exit codes `0` success, `1` no data/empty results, `2` argument validation failure; strict up-front validation (KEEP regex, SOURCE_DIR existence, required CSV columns, currency match); errors to stderr with context (`trap EXIT` removes staging file; `ValueError` includes file path and line number).
