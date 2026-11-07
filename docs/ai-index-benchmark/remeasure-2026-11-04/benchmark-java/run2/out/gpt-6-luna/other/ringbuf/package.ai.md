### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:31:37Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded byte-stream buffering for producer-consumer transfer, allowing partial reads and writes across a fixed-capacity circular storage area.

#### Purpose
- Initialize, use, query, and release a fixed-capacity byte ring buffer.
- Support single-producer, single-consumer byte streams with usable capacity of `capacity - 1`.

#### Responsibilities
- **Buffer lifecycle and validation:** allocate storage for power-of-two capacities and reset or release buffer state.
- **Byte transfer:** perform bounded, partial reads and writes, including copies that wrap around the storage boundary.
- **Capacity queries:** report buffered bytes and writable space.

#### Key units
- `ringbuf_t` — stores byte storage, capacity, producer `head`, and consumer `tail`.
- `ringbuf_status_t` — reports initialization success or failure.
- `ringbuf_init` — validates capacity and allocates and initializes the buffer.
- `ringbuf_free` — releases storage and resets buffer fields.
- `ringbuf_write` / `ringbuf_read` — transfer up to the available space or data and return bytes transferred.
- `ringbuf_used` / `ringbuf_free_space` — report occupancy and writable capacity.

#### Data flow
- `ringbuf_init` allocates storage and sets both indices to zero; writes advance `head`, and reads advance `tail`.
- Occupancy and available space determine transfer limits; wrapped transfers are split at the allocation boundary.
- `ringbuf_free` releases storage and resets the buffer state.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h`, `<stdlib.h>` for allocation, and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` for `size_t` and `uint8_t`.

#### Cross-cutting
- Power-of-two capacity enables masked index wraparound; one slot remains reserved to distinguish full from empty.
- Initialization returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity and `RINGBUF_ENOMEM` on allocation failure.
- The API describes a single-producer, single-consumer buffer; no explicit null-pointer checks are provided.
