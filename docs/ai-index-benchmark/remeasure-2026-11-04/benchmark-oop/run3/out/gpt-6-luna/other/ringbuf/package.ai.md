### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:53:37Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded circular byte-stream buffering for passing data between a producer and a consumer.

#### Purpose
- Initialize, manage, and query fixed-capacity byte ring buffers.
- Transfer bytes partially according to available space or buffered data.

#### Responsibilities
- **Buffer lifecycle:** allocate and initialize storage; free storage and reset buffer state.
- **Byte transfer:** write into and read from the circular buffer, including wraparound copies.
- **Capacity accounting:** report used and writable bytes while reserving one slot to distinguish full from empty.

#### Key units
- `ringbuf_t` — stores the byte array, capacity, and `head`/`tail` indices.
- `ringbuf_status_t` — reports initialization success or invalid-capacity and allocation errors.
- `ringbuf_init` — validates power-of-two capacity and allocates buffer storage.
- `ringbuf_write` / `ringbuf_read` — copy up to the requested byte count, limited by free space or available data.
- `ringbuf_used` / `ringbuf_free_space` — report occupancy and writable capacity.
- `ringbuf_free` — releases storage and resets buffer fields.

#### Data flow
- `ringbuf_init` allocates storage and sets indices to zero; usable capacity is `capacity - 1`.
- `ringbuf_write` copies available input bytes into the buffer and advances `head`.
- `ringbuf_read` copies available buffered bytes to the destination and advances `tail`.
- Occupancy queries derive used and free byte counts from the indices; `ringbuf_free` releases and resets the buffer.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h` for the API and status codes, `<stdlib.h>` for allocation, and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` for `size_t` and `uint8_t`.

#### Cross-cutting
- Capacity must be a nonzero power of two; masked index updates provide wraparound.
- One slot is reserved to distinguish full from empty.
- `ringbuf_t` uses `volatile size_t` indices and is described as supporting a single producer and a single consumer.
- Initialization reports `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` for allocation failure.
