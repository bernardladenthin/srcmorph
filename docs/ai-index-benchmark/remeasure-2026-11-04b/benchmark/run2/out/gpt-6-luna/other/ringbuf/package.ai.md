### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:59:56Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO storage for transferring byte streams between a single producer and a single consumer.

#### Purpose
- Allocate, manage, and query a fixed-capacity byte ring buffer.
- Preserve FIFO order while wrapping reads and writes at the buffer boundary.

#### Responsibilities
- **Buffer lifecycle:** validate power-of-two capacity, allocate storage, and free/reset buffer state.
- **Byte transfer and capacity tracking:** write into available space, read stored data, and report used or free bytes.

#### Key units
- `ringbuf_t` — stores byte data, capacity, and volatile `head` and `tail` indices.
- `ringbuf_status_t` — initialization status type with `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free` — initialize and release ring-buffer storage.
- `ringbuf_write`, `ringbuf_read` — transfer bytes into and out of the buffer.
- `ringbuf_used`, `ringbuf_free_space` — report occupied and writable byte counts.

#### Data flow
- `ringbuf_init` validates capacity and allocates storage; `ringbuf_write` copies available input bytes into the ring, and `ringbuf_read` copies stored bytes to the destination.
- Transfers wrap across the storage boundary; query functions report occupancy and remaining space.
- One slot is reserved to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h` for buffer types and status codes, and standard-library `malloc`, `free`, and `memcpy`.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` for `size_t` and `uint8_t`.

#### Cross-cutting
- Capacity must be nonzero and a power of two; index wrapping uses a capacity mask.
- The API describes a single-producer/single-consumer buffer with producer-owned `head` and consumer-owned `tail`.
- Initialization reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
