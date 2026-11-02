### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:02:53Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte buffering for transferring data between a single producer and a single consumer.

#### Purpose
- Implements a fixed-capacity circular byte buffer with initialization, cleanup, occupancy queries, and partial reads and writes.
- Supports power-of-two capacities and reserves one slot to distinguish full from empty.

#### Responsibilities
- **Buffer lifecycle and capacity:** Allocate and initialize storage, validate capacity, report used and free bytes, and release/reset resources.
- **FIFO transfers:** Copy bytes into and out of the buffer, limiting each operation to available space or data and handling wraparound.

#### Key units
- `ringbuf_t` — stores the byte allocation, capacity, and volatile `head` and `tail` indices.
- `ringbuf_status_t` — reports initialization success, invalid capacity, or allocation failure.
- `ringbuf_init`, `ringbuf_free` — initialize and release buffer storage.
- `ringbuf_write`, `ringbuf_read` — enqueue and dequeue bytes, returning the number transferred.
- `ringbuf_used`, `ringbuf_free_space` — report occupied and writable byte counts.

#### Data flow
- `ringbuf_init` validates the requested power-of-two capacity and allocates storage.
- Writes copy available bytes from the source, advancing `head`; reads copy available bytes to the destination, advancing `tail`.
- Transfers split at the allocation boundary when wrapping; `ringbuf_free` releases storage and resets buffer fields.

#### Dependencies
- `ringbuf.h` declares the buffer type, status values, and public API used by `ringbuf.c`.
- Uses `<stdlib.h>` for allocation, `<string.h>` for copying, and standard `size_t` and `uint8_t` types.

#### Cross-cutting
- Capacity must be a power of two; one slot remains unused, so usable capacity is `capacity - 1`.
- Index wrapping and occupancy calculations use capacity-based bitmasks.
- Initialization reports invalid capacity as `RINGBUF_EINVAL` and allocation failure as `RINGBUF_ENOMEM`.
