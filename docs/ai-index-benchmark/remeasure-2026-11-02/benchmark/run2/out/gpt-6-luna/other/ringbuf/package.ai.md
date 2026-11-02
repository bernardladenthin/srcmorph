### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:46:09Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Buffers byte streams between a producer and consumer with bounded FIFO storage and partial transfers.

#### Purpose
- Provides a fixed-capacity byte ring buffer for enqueueing and dequeueing data.
- Preserves FIFO order across wraparound; usable capacity is one less than configured capacity.

#### Responsibilities
- **Buffer lifecycle:** validate capacity, allocate storage, initialize indices, and release/reset resources.
- **Byte transfer and occupancy:** write available bytes, read buffered bytes, and report used or free space.

#### Key units
- `ringbuf_t` — holds the allocated data, capacity, producer index `head`, and consumer index `tail`.
- `ringbuf_status_t` — reports initialization success, invalid capacity, or allocation failure.
- `ringbuf_init` / `ringbuf_free` — initialize and release a ring buffer.
- `ringbuf_write` / `ringbuf_read` — enqueue or dequeue up to the requested byte count.
- `ringbuf_used` / `ringbuf_free_space` — report occupancy and writable capacity.
- `is_power_of_two` / `min_size` — internal capacity-validation and size-selection helpers.

#### Data flow
- `ringbuf_init` accepts a nonzero power-of-two capacity, allocates storage, and starts `head` and `tail` at zero.
- `ringbuf_write` copies available input bytes, wrapping at the storage boundary, then advances `head`.
- `ringbuf_read` copies available buffered bytes, wrapping as needed, then advances `tail`.
- Occupancy queries calculate used and available bytes; `ringbuf_free` releases storage and resets buffer fields.

#### Dependencies
- `ringbuf.c` implements the API declared in `ringbuf.h`.
- Uses C standard-library allocation and copying facilities: `<stdlib.h>` and `<string.h>`; the public header uses `<stddef.h>` and `<stdint.h>`.

#### Cross-cutting
- Capacity must be a nonzero power of two; index wraparound uses a capacity mask.
- One slot is reserved to distinguish full from empty, so usable storage is `capacity - 1`.
- The API is intended for single-producer/single-consumer use; writes and reads may transfer fewer bytes than requested.
- Initialization reports `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` for allocation failure; operations perform no null-pointer checks.
