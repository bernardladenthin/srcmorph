### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:48:28Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity byte queue for producer/consumer stream buffering and in-memory data transfer.

#### Purpose
- Implements a circular byte buffer used to move byte streams between producer and consumer without reallocations.
- Exposes lifecycle, occupancy accounting, and wraparound read/write operations for bounded buffering.

#### Responsibilities
- Buffer lifecycle and validation: `ringbuf_init`, `ringbuf_free`, `ringbuf_status_t`, and power-of-two capacity rules.
- Occupancy tracking: `ringbuf_used` and `ringbuf_free_space` provide live capacity accounting with one slot intentionally left unused.
- Byte transfer: `ringbuf_write` and `ringbuf_read` copy data across wraparound segments using contiguous memory slices.
- API contract: `ringbuf.h` defines the public C interface and single-producer/single-consumer ownership model.

#### Key units
- `ringbuf_t` — struct storing `data`, `capacity`, `head`, and `tail` for a circular byte buffer.
- `ringbuf_status_t` — enum with `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM` for lifecycle and validation outcomes.
- `ringbuf_init` — validates `capacity`, allocates storage, and initializes buffer state.
- `ringbuf_free` — releases heap memory and resets ring state.
- `ringbuf_used` — reports current used bytes via circular index arithmetic.
- `ringbuf_free_space` — reports remaining writable bytes, accounting for the reserved unused slot.
- `ringbuf_write` — copies up to `len` bytes from `src` into the ring, splitting writes across wraparound boundaries.
- `ringbuf_read` — copies up to `len` bytes from the ring into `dst`, also handling wraparound.

#### Data flow
- Caller creates a `ringbuf_t` and passes a desired `capacity` to `ringbuf_init`.
- `ringbuf_init` validates the value, allocates `rb->data`, and initializes `rb->head` and `rb->tail`.
- Producer calls `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)`; bytes are copied into the ring in one or two segments and `head` advances.
- Consumer calls `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)`; bytes are copied out in one or two segments and `tail` advances.
- `ringbuf_used` and `ringbuf_free_space` expose occupancy state used to coordinate producer/consumer progress.

#### Dependencies
- Internal: `ringbuf.c` depends on the definitions and API declared in `ringbuf.h`.
- External C runtime: `stdlib.h`, `string.h`, `malloc`, `free`, `memcpy`, `size_t`, and `uint8_t`.
- Contract assumptions: single-producer/single-consumer ownership with `volatile size_t head` and `tail` in the header.

#### Cross-cutting
- Capacity discipline: capacity must be non-zero and a power of two; one slot remains unused so full and empty states are distinct.
- Buffer arithmetic: index advancement and occupancy use `(head + n) & (capacity - 1)`, `(tail + n) & (capacity - 1)`, and `(head - tail) & (capacity - 1)`.
- Copy semantics: both data paths use `memcpy` for each contiguous segment and return the actual number of bytes copied.
- Error handling: `ringbuf_init` returns `RINGBUF_EINVAL` for invalid capacities and `RINGBUF_ENOMEM` on allocation failure; no explicit null-pointer guards are present in the implementation.
- Interface alignment: the header and implementation describe the same `ringbuf_t` state and public API to keep the contract consistent.
