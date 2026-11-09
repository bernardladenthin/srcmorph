### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:13:36Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte storage for passing data between a single producer and a single consumer.

#### Purpose
- Define and implement a dynamically allocated byte ring buffer with partial reads and writes.
- Measure buffered and writable bytes while reserving one slot to distinguish full from empty.

#### Responsibilities
- **Buffer API and state:** Declare `ringbuf_t`, status codes, and operations for initialization, transfer, measurement, and cleanup.
- **Ring-buffer operations:** Validate power-of-two capacity, allocate storage, copy bytes across wrap boundaries, and update producer and consumer positions.

#### Key units
- `ringbuf_t` — stores the byte array, capacity, and volatile `head` and `tail` indices.
- `ringbuf_status_t` — defines `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init` — validates capacity and allocates and initializes buffer storage.
- `ringbuf_write` / `ringbuf_read` — transfer up to the available bytes into or out of the FIFO.
- `ringbuf_used` / `ringbuf_free_space` — report occupancy and writable capacity.
- `ringbuf_free` — releases storage and resets buffer state.

#### Data flow
- `ringbuf_init` accepts a nonzero power-of-two capacity and prepares the buffer, failing with `RINGBUF_EINVAL` or `RINGBUF_ENOMEM` when appropriate.
- `ringbuf_write` copies available input bytes from `src`, advancing `head`; `ringbuf_read` copies buffered bytes to `dst`, advancing `tail`.
- Transfers split into up to two copies when they wrap around the array; `ringbuf_used` and `ringbuf_free_space` report current occupancy and capacity.

#### Dependencies
- `ringbuf.c` implements the API declared in `ringbuf.h`.
- Uses standard `malloc`/`free` and `memcpy`; the header uses `size_t` and `uint8_t`.

#### Cross-cutting
- Designed for single-producer/single-consumer use, with producer-owned `head` and consumer-owned `tail`.
- Requires nonzero power-of-two capacity; usable capacity is allocated capacity minus one.
- `head` and `tail` are `volatile size_t`; C++ callers receive C linkage.
