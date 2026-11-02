### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:00:05Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte-stream buffering for producer/consumer data exchange using reusable circular storage.

#### Purpose
- Initialize, manage, and query a fixed-capacity byte ring buffer.
- Copy bytes into and out of the buffer in FIFO order, up to available space or stored data.

#### Responsibilities
- **Buffer lifecycle and validation:** Allocate storage for nonzero power-of-two capacities; release storage and reset state.
- **Byte transfer and capacity accounting:** Write and read across wraparound boundaries; report used bytes and free space. One slot is reserved, so usable capacity is `capacity - 1`.

#### Key units
- `ringbuf_t` — stores the byte allocation, capacity, and producer `head` / consumer `tail` indices.
- `ringbuf_status_t` — initialization status codes: `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init` — validates capacity, allocates storage, and initializes buffer state.
- `ringbuf_write` / `ringbuf_read` — transfer available bytes into or out of the ring.
- `ringbuf_used` / `ringbuf_free_space` — report stored bytes and writable capacity.
- `ringbuf_free` — releases storage and resets buffer fields.

#### Data flow
- `ringbuf_init` receives a buffer and requested capacity, validates the power-of-two constraint, then allocates and initializes storage.
- `ringbuf_write` copies source bytes up to available space; `ringbuf_read` copies stored bytes to the destination. Both handle array wraparound and return the number of bytes transferred.
- `ringbuf_used` and `ringbuf_free_space` expose buffer occupancy; `ringbuf_free` releases the allocation.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h` for public types and status codes, plus `<stdlib.h>` for allocation and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` for `size_t` and `uint8_t`.

#### Cross-cutting
- Power-of-two capacities permit index wrapping with a capacity mask; one slot remains unused to distinguish full from empty.
- Initialization reports invalid capacities with `RINGBUF_EINVAL` and allocation failures with `RINGBUF_ENOMEM`.
- The header describes producer-owned `head` and consumer-owned `tail` for single-producer/single-consumer use; no atomic operations are specified.
