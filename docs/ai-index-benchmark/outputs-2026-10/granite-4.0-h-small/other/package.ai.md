### other
- H: 1.0
- C: 142CEE21
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T05:19:01Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A multi-language codebase providing OpenCL kernels for image histogram calculation, a C ring buffer implementation, and Python scripts for sales reporting and backup management.

#### Purpose
- **OpenCL Histogram Kernels**: Efficiently compute 256-bin byte histograms of images or byte streams using two-pass parallel processing.
- **C Ring Buffer**: Offer a lock-free, single-producer/single-consumer ring buffer for byte data storage and retrieval.
- **Python Sales Reporting**: Generate monthly sales reports from CSV order data, aggregating revenue by month and region.
- **Python Backup Management**: Create and manage dated backups of directories, retaining a specified number of recent archives.

#### Responsibilities
- **OpenCL Kernels**:
  - Calculate partial histograms in parallel work-groups.
  - Reduce partial histograms to final histogram in a second pass.
- **C Ring Buffer**:
  - Initialize and manage ring buffer memory.
  - Provide thread-safe write and read operations.
  - Track used and free space in the buffer.
- **Sales Reporting**:
  - Parse CSV order data.
  - Aggregate revenue by month and region.
  - Output formatted sales report.
- **Backup Management**:
  - Compress directories into dated archive files.
  - Maintain a configurable number of recent backups.
  - Delete older backups beyond retention count.

#### Key units
- **OpenCL Kernels**:
  - `histogram_partial`: OpenCL kernel for partial histogram calculation.
  - `histogram_reduce`: OpenCL kernel for reducing partial histograms.
- **C Ring Buffer**:
  - `ringbuf_t`: Structure representing the ring buffer.
  - `ringbuf_init`: Initialize the ring buffer.
  - `ringbuf_free`: Free ring buffer memory.
  - `ringbuf_write`: Write data to the ring buffer.
  - `ringbuf_read`: Read data from the ring buffer.
  - `ringbuf_used`: Get used space in the buffer.
  - `ringbuf_free_space`: Get free space in the buffer.
- **Sales Reporting**:
  - `sales_report.py`: Main script for generating sales reports.
  - `Order`: Dataclass representing an order.
- **Backup Management**:
  - `rotate_backups.sh`: Shell script for managing backups.

#### Data flow
- **OpenCL Histogram**:
  1. Input byte stream or image data is processed in parallel by work-groups.
  2. Each work-group computes a partial histogram stored in global memory.
  3. Work-items in a second pass read partial histograms and compute the final histogram.
- **C Ring Buffer**:
  1. Producer writes data to the ring buffer using `ringbuf_write`.
  2. Consumer reads data from the ring buffer using `ringbuf_read`.
  3. `ringbuf_used` and `ringbuf_free_space` provide buffer state information.
- **Sales Reporting**:
  1. CSV files are read and parsed to extract order data.
  2. Orders are filtered by currency.
  3. Revenue is aggregated by month and region using `defaultdict`.
  4. Aggregated data is sorted and filtered based on minimum revenue threshold.
  5. Results are printed in a tabular format.
- **Backup Management**:
  1. Input parameters are validated and directories are checked.
  2. Timestamped archive filename is constructed.
  3. Source directory is compressed into a temporary file.
  4. Temporary file is renamed to final archive name.
  5. Archives in backup directory are listed and sorted by newest first.
  6. Older archives beyond retention count are deleted.

#### Dependencies
- **OpenCL Kernels**:
  - OpenCL built-in functions for thread and memory management.
  - Global memory for input data, partial histograms, and final histogram.
- **C Ring Buffer**:
  - `stdlib.h`: Memory allocation and deallocation.
  - `string.h`: Memory copy operations.
  - `ringbuf.h`: Ring buffer structure and status codes.
- **Sales Reporting**:
  - `argparse`: Command-line argument parsing.
  - `csv`: CSV file reading.
  - `sys`: Standard input/output handling.
  - `collections.defaultdict`: Revenue data aggregation.
  - `dataclasses.dataclass`: Order data structure definition.
  - `datetime.date`: Date parsing and formatting.
  - `decimal.Decimal`: Accurate monetary value handling.
- **Backup Management**:
  - `tar`: Compressed archive file creation.
  - `date`: Timestamped filename generation.
  - `du`: Archive size reporting.
  - Standard shell utilities: `basename`, `mkdir`, `ls`, `mv`, `rm`

#### Cross-cutting
- **Error Handling**:
  - OpenCL kernels and C ring buffer use status codes for error indication.
  - Python scripts handle errors and exceptions with informative messages and appropriate exit codes.
- **Configuration**:
  - OpenCL kernels and C ring buffer have configurable parameters (e.g., buffer capacity).
  - Python scripts allow configuration through command-line arguments or script parameters (e.g., directory paths, retention counts, currency filters).
- **Thread Safety**:
  - C ring buffer is designed for single-producer/single-consumer access, eliminating the need for locks.
  - Python scripts and OpenCL kernels do not involve multi-threading concerns.
