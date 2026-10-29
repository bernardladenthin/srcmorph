### other
- H: 1.0
- C: 142CEE21
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:18:29Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Manages image histograms, thread-safe data storage, and backup rotation and sales report generation.

#### Purpose
- Computes histograms of byte values in image or byte streams.
- Provides a circular buffer for efficient data storage and retrieval.
- Rotates backups of specified directories and generates monthly sales reports.

#### Responsibilities
- Parallel byte counting and reduction for histograms.
- Data storage and retrieval with thread safety.
- Backup management and sales report generation.

#### Key units
- `histogram_partial`: Counts byte occurrences in the input stream and writes partial histograms.
- `histogram_reduce`: Sums partial histograms to produce the final histogram.
- `ringbuf_t`: Represents the ring buffer structure.
- `ringbuf_status_t`: Enumerates the status of operations.
- `rotate_backups.sh`: Bash script for rotating backups.
- `sales_report.py`: Python script for generating sales reports.

#### Data flow
- Histograms: `input` (global constant array of `uchar`), `length` (uint), `partial` (global array), `histogram` (global array).
- Ring buffer: Data written to `ringbuf_write`, read from `ringbuf_read`, tracked by `ringbuf_used` and `ringbuf_free_space`.
- Backup rotation: `rotate_backups.sh` reads `<source_dir>`, creates archives, deletes older ones.
- Sales report generation: `sales_report.py` reads `csv_files`, calculates revenue, and prints reports.

#### Dependencies
- Histograms: `uchar`, `uint`, `__global`, `__local`.
- Ring buffer: `ringbuf.h`, `stdlib.h`, `string.h`, `stddef.h`, `stdint.h`.
- Backup rotation: Bash, tar, ls, rm, du.
- Sales report generation: argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal.

#### Cross-cutting
- Bash scripts handle file operations.
- Python scripts use standard libraries for data processing.
- Both scripts validate input parameters and handle exceptions.
