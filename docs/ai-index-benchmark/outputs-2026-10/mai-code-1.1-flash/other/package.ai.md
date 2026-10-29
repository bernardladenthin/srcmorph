### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:48:42Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low-level byte-stream processing, bounded in-memory transfer, and operational data hygiene/reporting utilities for OpenCL, C, and shell/Python workflows.

#### Purpose
- Implements a small family of runtime utilities for byte analysis, ring-buffer transport, and operational reporting/retention tasks.
- Keeps each capability self-contained: OpenCL histogramming, circular buffer data movement, and backup/sales automation.

#### Responsibilities
- OpenCL byte analytics: `kernels/` computes a 256-bin histogram from source bytes or image regions using a two-phase local-plus-global reduction.
- Circular buffering: `ringbuf/` provides a fixed-capacity byte queue for producer/consumer transfer with wraparound reads and writes and occupancy tracking.
- Operational scripts: `scripts/` manages backup archive rotation and aggregates monthly regional sales totals from CSV order data.
- Validation and output discipline: each subsystem checks input constraints, rejects invalid states, and emits explicit status/error output.

#### Key units
- `histogram_partial` — counts bytes in a work-group slice into a per-group partial histogram.
- `histogram_reduce` — sums partial group totals into final `histogram[bin]` output values.
- `BINS` — fixed 256-bin bucket count used across the OpenCL histogram kernels.
- `ringbuf_t` — circular byte buffer storing `data`, `capacity`, `head`, and `tail`.
- `ringbuf_status_t` — status enum for validation and allocation outcomes: `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init` — validates capacity, allocates memory, and initializes ring state.
- `ringbuf_write` — copies producer data into the ring across wraparound boundaries.
- `ringbuf_read` — copies consumer data out of the ring across wraparound boundaries.
- `rotate_backups.sh` — shell CLI that creates timestamped archived snapshots and removes older backup files beyond `KEEP`.
- `KEEP` — retention count used to prune older archive artifacts.
- `Order` — frozen dataclass capturing parsed CSV order fields: `order_date`, `region`, `amount`, and currency.
- `parse_orders(path, currency)` — reads a CSV, validates headers, filters by currency, and yields `Order` records.
- `revenue_by_month_and_region(orders)` — aggregates totals by `(YYYY-MM, region)` with `defaultdict(Decimal)`.
- `main(argv=None)` — CLI entry point for reporting totals and handling empty/no-data cases.

#### Data flow
- `kernels/`:
  - input byte data is read from `input` with a source length
  - each work-item increments local bin counts in `local_bins`
  - per-group totals are flushed to `partial[group_id * BINS + bin]`
  - `histogram_reduce` sums per-group partials across groups to produce the final 256-entry histogram
- `ringbuf/`:
  - caller initializes `ringbuf_t` with a capacity
  - producer calls `ringbuf_write` to append bytes into the circular storage
  - consumer calls `ringbuf_read` to consume bytes, advancing `tail`
  - `ringbuf_used` and `ringbuf_free_space` expose occupancy to coordinate progress
- `scripts/`:
  - backup flow validates inputs, builds timestamped archive names, writes `.tar.gz` files, and prunes old snapshots beyond `KEEP`
  - sales flow reads CSV inputs, filters by currency and minimum revenue threshold, groups totals by month and region, and prints tab-delimited results

#### Dependencies
- Internal package boundaries:
  - `kernels/` is an OpenCL-only kernel module with no direct file-level dependency on `ringbuf/` or `scripts/`
  - `ringbuf/` is implemented as a C header/implementation pair: `ringbuf.h` and `ringbuf.c`
  - `scripts/` is standalone shell and Python logic with no shared packaged library dependencies
- External modules and runtimes:
  - OpenCL: `__kernel`, `__global`, `__local`, `uchar`, `uint`, `atomic_inc`, `barrier`, `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `CLK_LOCAL_MEM_FENCE`
  - C runtime: `stdlib.h`, `string.h`, `malloc`, `free`, `memcpy`, `size_t`, `uint8_t`
  - Bash utilities: `set`, `trap`, `mapfile`, `mkdir`, `basename`, `dirname`, `date`, `mv`, `rm`, `echo`, `exit`, `tar`, `ls`, `du`, `cut`
  - Python stdlib: `argparse`, `csv`, `sys`, `defaultdict`, `dataclass`, `date`, `Decimal`, `InvalidOperation`
- External data sources:
  - source directories for backup creation
  - archive destination directories for retained snapshots
  - order CSV files containing `order_date`, `region`, `amount`, and `currency`

#### Cross-cutting
- Input validation is explicit and strict:
  - `ringbuf_init` rejects invalid capacities and requires power-of-two size
  - `rotate_backups.sh` validates `KEEP`, source directory, and positional arguments with exit codes and stderr messages
  - `sales_report.py` checks CSV headers and row data and raises `ValueError` with file/line context on malformed input
- Failure handling is immediate:
  - OpenCL kernel logic uses early exits for out-of-range bin indexing
  - `ringbuf_init` returns `RINGBUF_EINVAL`/`RINGBUF_ENOMEM` on invalid states
  - shell script uses `set -euo pipefail` to abort on command failure
  - Python CLI exits with status `1` when no orders are found and surfaces malformed input as `ValueError`
- Fixed-size, bounded-state design is recurring:
  - `BINS = 256` in the histogram kernel
  - ring buffer uses one unused slot so full/empty states are distinguishable
  - backup retention counts are bounded by `KEEP`
- Parallel and accumulation patterns recur across the package:
  - kernels use local aggregation followed by global reduction
  - ringbuf uses index arithmetic and contiguous segment copies to preserve order under wraparound
  - scripts aggregate by month and region after filtering, then emit stable sorted totals
- Output conventions are deliberate:
  - histogram emits a final 256-bin distribution
  - ring buffer returns actual byte counts copied
  - backup script emits `created ...` and `removed ...`
  - sales report prints `YYYY-MM\tregion\tamount currency` and errors separately to stderr when empty
