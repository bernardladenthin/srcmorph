### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:23:36Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte-stream buffering between a single producer and a single consumer.

#### Purpose
- Implements a fixed-capacity circular buffer for writing, reading, and querying buffered bytes.
- Uses a power-of-two capacity and reserves one slot to distinguish full from empty.

#### Responsibilities
- **Buffer lifecycle:** validate capacity, allocate storage, initialize indices, and free/reset buffer state.
- **Byte transfer and occupancy:** copy available bytes in or out, report bytes used and free space, and wrap transfers at the end of the storage array.

#### Key units
- `ringbuf_t` — stores the byte array, capacity, and producer `head` and consumer `tail` indices.
- `ringbuf_status_t` — reports initialization success or invalid-capacity/allocation errors.
- `ringbuf_init`, `ringbuf_free` — initialize and release buffer storage.
- `ringbuf_write`, `ringbuf_read` — write or read up to the requested byte count.
- `ringbuf_used`, `ringbuf_free_space` — report occupied and writable bytes.

#### Data flow
- `ringbuf_init` validates a nonzero power-of-two capacity, allocates storage, and resets indices.
- `ringbuf_write` copies up to the available space from `src`, advances `head`, and wraps as needed.
- `ringbuf_read` copies up to the occupied byte count into `dst`, advances `tail`, and wraps as needed.
- Occupancy and available space are derived from the indices; `ringbuf_free` releases storage and clears buffer fields.

#### Dependencies
- `ringbuf.c` implements the API declared in `ringbuf.h`.
- Uses `<stdlib.h>` for allocation and release, `<string.h>` for copying, and standard `size_t`/`uint8_t` types.

#### Cross-cutting
- `head` and `tail` are `volatile size_t` indices, described as producer- and consumer-owned.
- Capacity must be a power of two; usable capacity is `capacity - 1`.
- Initialization returns `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` on allocation failure.
