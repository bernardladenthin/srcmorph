### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:50:31Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte-stream storage for single-producer/single-consumer data transfer.

#### Purpose
- Initialize, query, write to, read from, and release a fixed-capacity byte ring buffer.
- Preserve FIFO order while allowing partial transfers when the buffer is full or empty.

#### Responsibilities
- **Buffer lifecycle and validation:** allocate and initialize storage only for nonzero power-of-two capacities; release storage and reset buffer state.
- **Byte transfer and occupancy:** append and remove available bytes across wraparound boundaries, and report used or writable space.

#### Key units
- `ringbuf_t` — buffer state containing storage, capacity, and volatile `head` and `tail` indices.
- `ringbuf_status_t` — initialization status codes: `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init` / `ringbuf_free` — initialize the ring buffer or release and reset it.
- `ringbuf_write` / `ringbuf_read` — transfer bytes into or out of the buffer, returning the actual byte count.
- `ringbuf_used` / `ringbuf_free_space` — report occupied bytes and available writable space.

#### Data flow
- `ringbuf_init` validates capacity, allocates storage, and zeros the indices.
- `ringbuf_write` copies up to available space and advances `head`; `ringbuf_read` copies up to available data and advances `tail`.
- Transfers wrap around the backing array as needed; one slot remains unused to distinguish full from empty.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h` for buffer types and status definitions, plus `<stdlib.h>` for allocation and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` for `size_t` and `uint8_t`.

#### Cross-cutting
- Requires a nonzero power-of-two capacity; usable storage is `capacity - 1` bytes.
- Intended for single-producer/single-consumer use, with volatile producer-owned `head` and consumer-owned `tail` indices.
- Initialization returns `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` if allocation fails.
