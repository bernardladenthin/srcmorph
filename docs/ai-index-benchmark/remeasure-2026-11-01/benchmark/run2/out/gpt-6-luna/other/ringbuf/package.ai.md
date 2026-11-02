### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:45:03Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Queues byte streams in FIFO order through a fixed-capacity circular buffer for single-producer, single-consumer use.

#### Purpose
- Provide initialization, cleanup, byte transfer, and occupancy queries for a power-of-two-capacity byte buffer.
- Reserve one slot to distinguish full from empty, yielding usable capacity of `capacity - 1`.

#### Responsibilities
- **Buffer lifecycle:** allocate and initialize storage; release it and reset buffer state.
- **Byte transfer:** enqueue and dequeue up to available space or data, including transfers that wrap around the storage boundary.
- **Capacity queries:** report queued bytes and writable space.

#### Key units
- `ringbuf_t` — stores the byte array, capacity, and volatile producer-owned `head` and consumer-owned `tail` indices.
- `ringbuf_status_t` — reports initialization success, invalid capacity (`RINGBUF_EINVAL`), or allocation failure (`RINGBUF_ENOMEM`).
- `ringbuf_init` — validates power-of-two capacity, allocates storage, and resets indices.
- `ringbuf_free` — releases storage and resets buffer fields.
- `ringbuf_write` / `ringbuf_read` — enqueue or dequeue bytes and return the number transferred.
- `ringbuf_used` / `ringbuf_free_space` — report occupied and available byte counts.

#### Data flow
- Initialize with a nonzero power-of-two capacity; successful initialization allocates storage and sets `head` and `tail` to zero.
- Writes copy source bytes into available space; reads copy queued bytes to the destination, each using up to two `memcpy` operations when wrapping.
- Index advancement uses a capacity mask; usage is computed from the index difference, with one slot reserved.

#### Dependencies
- `ringbuf.h` declares the API, buffer and status types, and status codes.
- Uses `<stdlib.h>` for allocation and release, and `<string.h>` for wrapped copies.
- Header uses `<stddef.h>` and `<stdint.h>` for `size_t` and `uint8_t`.
