### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:38:51Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Stores and transfers byte streams through a bounded FIFO buffer, supporting partial reads and writes across wraparound.

#### Purpose
- Provides a fixed-capacity byte ring buffer with initialization, cleanup, occupancy queries, and read/write operations.
- Reserves one slot to distinguish full from empty, so usable capacity is one less than configured capacity.

#### Responsibilities
- **Buffer lifecycle and capacity:** validate power-of-two capacity, allocate storage, report status, and release/reset state.
- **Byte transfer and accounting:** copy available bytes in FIFO order, handle wraparound, and report used and writable space.

#### Key units
- `ringbuf_t` — stores byte storage, capacity, and `head`/`tail` indices.
- `ringbuf_status_t` — reports initialization success, invalid capacity, or allocation failure.
- `ringbuf_init`, `ringbuf_free` — allocate and initialize the buffer, then release and reset it.
- `ringbuf_write`, `ringbuf_read` — transfer up to the requested number of bytes.
- `ringbuf_used`, `ringbuf_free_space` — report occupied and writable byte counts.

#### Data flow
- `ringbuf_init` validates capacity and allocates storage; writes copy source bytes into available space, and reads copy stored bytes to the destination.
- `ringbuf_used` and `ringbuf_free_space` expose current occupancy; `ringbuf_free` releases storage.

#### Dependencies
- `ringbuf.c` implements the API declared in `ringbuf.h`.
- Uses standard C allocation and copy routines (`malloc`, `free`, `memcpy`) and byte type `uint8_t`.

#### Cross-cutting
- Capacity must be a nonzero power of two; index wraparound uses a capacity mask.
- The API describes a single-producer/single-consumer buffer; the provided summaries do not specify synchronization.
- Initialization reports `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` if allocation fails.
