### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:51Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Calculates a 256-bin byte histogram of an image or byte stream using two-pass OpenCL parallel reduction, and provides a lock-free ring buffer for deterministic byte-stream throughput, while scripts manage backup rotation and sales revenue aggregation.

#### Purpose
The package provides image histogram computation via OpenCL parallel reduction, a deterministic lock-free ring buffer for byte-stream throughput, and utility scripts for backup rotation and sales revenue reporting from CSV data.

#### Responsibilities
- **Histogram computation**: Two-pass OpenCL kernel pattern (`histogram_partial`, `histogram_reduce`) that computes a 256-bin byte histogram from raw data using atomic operations and parallel reduction.
- **Lock-free ring buffer**: Power-of-two capacity buffer with volatile head/tail indices enabling deterministic single-producer/single-consumer byte-stream throughput without mutexes.
- **Backup rotation**: Shell script that creates dated compressed archives and prunes old backups to maintain a configurable retention count.
- **Sales reporting**: Python script that parses CSV order exports, filters by currency and minimum revenue, and aggregates revenue per month and region.

#### Key units
- `histogram_partial`: OpenCL kernel performing per-work-group partial histogram computation via atomic increments and local memory accumulation.
- `histogram_reduce`: OpenCL kernel reducing partial histograms into the final 256-bin output, one work-item per bin.
- `BINS` macro: Histogram bin count constant defined as 256.
- `ringbuf_t`: Struct containing volatile head/tail indices and a dynamically allocated uint8_t data buffer sized to power-of-two capacity.
- `ringbuf_status_t`: Enum with values `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init`: Validates power-of-two constraint, allocates buffer, resets indices.
- `ringbuf_free`: Releases allocated data buffer and resets state.
- `ringbuf_write`: Copies bytes into buffer with bitmasked wrap-around; returns actual bytes written.
- `ringbuf_read`: Copies bytes out of buffer with bitmasked wrap-around; returns actual bytes read.
- `ringbuf_used`: Computes current occupancy via `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space`: Computes available capacity via `(capacity - 1) - used`.
- `rotate_backups.sh`: Shell script creating dated `.tar.gz` archives and removing excess backups, keeping only the newest N.
- `sales_report.py`: Python script parsing CSV order files and outputting aggregated revenue grouped by month and region.

#### Data flow
Input byte data flows into `histogram_partial` where work-groups accumulate partial counts into a global buffer; subsequently `histogram_reduce` sums across all partial results to produce the final histogram. The ring buffer operates with the producer calling `ringbuf_write` to copy data (potentially spanning wrapped chunks) and advance the head index, while the consumer calls `ringbuf_read` to copy data out and advance the tail index; occupancy and free space are derived from the head-tail difference masked by `capacity - 1`. The backup rotation script generates timestamped archives and moves them to a backup directory, then prunes excess archives. The sales report script reads CSV files, validates columns and data, aggregates `Decimal` revenues per `(month, region)`, and prints tab-separated summaries to stdout.

#### Dependencies
- OpenCL API types: `uchar`, `uint`, `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`.
- Macro `BINS` defined as 256.
- `ringbuf.h`: defines `ringbuf_t` struct layout, `ringbuf_status_t` enum, and all public function signatures.
- `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`: standard C library headers for `size_t`, `uint8_t`, `malloc`, `free`, `memcpy`.
- C11 or later: required for `volatile` semantics on ring buffer indices.
- `argparse`, `csv`, `sys` (Python standard library); `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`.
- CSV files with `order_date`, `region`, `amount`, `currency` columns.
- `tar`, `mv`, `ls -1t`, `rm`, `du -h`, `date -u`, bash shell with `set -euo pipefail`.

#### Cross-cutting
- **Parallel reduction pattern**: Histogram uses two-pass accumulation and synchronization to balance work-group partial results and global sums.
- **Power-of-two constraint**: All ring buffer capacity checks, bitmask operations (`& (capacity - 1)`), and usable-byte calculations (`capacity - 1`) stem from the power-of-two requirement; invalid capacities are rejected.
- **Single-producer/single-consumer model**: Ring buffer uses `volatile` on head/tail indices for serialization without mutexes, assuming one writer and one reader.
- **One-slot-empty reservation**: Usable capacity is always `capacity - 1`, with the last slot permanently reserved to distinguish full and empty states.
- **Input validation**: All scripts validate inputs and exit with error codes on invalid data; `rotate_backups.sh` uses a trap on EXIT to clean up partial archive temp files on interruption.
- **Error handling**: Ring buffer write/read return actual byte count copied (may be less than requested); `sales_report.py` exits with code 1 and prints `"no orders found"` to stderr when no matching orders exist.
