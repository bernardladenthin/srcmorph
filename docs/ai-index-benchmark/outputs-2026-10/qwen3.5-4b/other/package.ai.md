### other
- H: 1.0
- C: 142CEE21
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:06:28Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a high-performance OpenCL histogram computation engine, a fixed-capacity circular buffer for zero-copy data transfer, and operational utilities for data archival and financial reporting.

#### Purpose
- Executes two-pass OpenCL kernel routines to compute 256-bin byte frequency histograms from image data or streams.
- Manages fixed-capacity circular buffers optimized for power-of-two indexing and zero-copy producer-consumer transfers.
- Orchestrates automated backup rotation, archival purging, and financial revenue aggregation via shell and Python scripts.

#### Responsibilities
- **OpenCL Histogram Engine**: Handles initial binning/local counting (Pass 1) and global aggregation (Pass 2) for byte frequency analysis.
- **Circular Buffer Management**: Allocates contiguous memory, enforces power-of-two capacity constraints, and performs atomic read/write operations with bitwise masking.
- **Data Archival & Reporting**: Manages backup lifecycle (creation, validation, rotation) and generates filtered monthly sales reports from CSV exports.

#### Key units
- `histogram_partial(input, length, partial, local_bins)`: Initiates the two-pass histogram computation by distributing data to work-groups.
- `histogram_reduce(partial, group_count, histogram)`: Aggregates partial counts from work-groups into the final 256-bin histogram.
- `ringbuf_t`: Structure defining the buffer instance with `head` (write), `tail` (read), and `data` pointers.
- `ringbuf_write(rb, src, len)` / `ringbuf_read(rb, dst, len)`: Atomic operations for writing and reading bytes with boundary wrapping.
- `rotate_backups.sh`: Shell script for creating dated tar.gz archives and purging old backups based on retention counts.
- `sales_report.py`: Python script for parsing CSV exports, aggregating revenue by month/region, and generating filtered reports.
- `Order` (dataclass): Structured type representing order details (date, region, amount, currency).

#### Data flow
- **Histogram Pipeline**: Global byte array (`input`) is sliced by work-groups in Pass 1 to produce partial sums in the `partial` array; Pass 2 iterates `partial` to sum values per bin into the `histogram` array.
- **Buffer Pipeline**: Producer writes data via `ringbuf_write` updating the `head` index and tracking used space; consumer reads via `ringbuf_read` updating the `tail` index and tracking free space.
- **Backup Pipeline**: User inputs source directory and retention count -> Script validates inputs -> Creates timestamped tar.gz -> Sorts archives -> Purges excess -> Outputs confirmation.
- **Report Pipeline**: User inputs CSV paths and filters -> Script parses rows into `Order` objects -> Aggregates by `(YYYY-MM, region)` -> Filters by currency/revenue -> Outputs summaries.

#### Dependencies
- **OpenCL Runtime**: Relies on `get_local_id`, `get_global_id`, `barrier`, `atomic_inc`, and global memory alignment for synchronization.
- **Standard Libraries**: `<stdlib.h>`, `<string.h>`, `<stdint.h>` for buffer operations; `argparse`, `csv`, `datetime`, `decimal` for reporting.
- **Unix Utilities**: `tar`, `ls`, `date`, `du`, `cut`, `mapfile` for backup management.
- **Data Sources**: CSV exports containing `order_date`, `region`, `amount`, `currency` columns.

#### Cross-cutting
- **Bitwise Optimization**: All buffer operations assume power-of-two capacity to use bitwise masking (`& (capacity - 1)`) instead of modulo arithmetic.
- **Error Handling**: OpenCL assumes valid memory alignment; scripts use exit codes (0/2) and Python exceptions (`ValueError`, `InvalidOperation`) for validation failures.
- **Data Consistency**: Uniform use of UTC timestamps for backups and ISO-formatted dates for report aggregation.
- **Concurrency**: OpenCL kernels utilize work-group barriers and atomic operations; buffer operations are designed for single-producer/single-consumer scenarios.
