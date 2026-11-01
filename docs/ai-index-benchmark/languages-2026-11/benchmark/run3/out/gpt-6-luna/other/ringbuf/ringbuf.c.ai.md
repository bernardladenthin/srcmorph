### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:00:05Z
- G: 
- A: 0.0.0
- X: file
---
> Provides bounded byte-buffer storage with wraparound reads and writes for streaming data.

#### Purpose
- Implements a ring buffer that stores bytes in a fixed-capacity allocation.
- Limits writes to available space and reads to stored data.

#### Type
- C implementation file for the `ringbuf` module; declarations and type definitions are in `ringbuf.h`.

#### Input
- `ringbuf_init(rb, capacity)`: buffer pointer and requested capacity.
- `ringbuf_write(rb, src, len)`: source bytes and requested write length.
- `ringbuf_read(rb, dst, len)`: destination buffer and requested read length.
- Buffer state fields: `data`, `capacity`, `head`, and `tail`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t` status.
- `ringbuf_write` and `ringbuf_read` return the number of bytes written or read.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases the allocation and resets buffer fields.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities, allocates `capacity` bytes, and initializes indices to zero.
- Tracks used bytes as `(head - tail) & (capacity - 1)`; reserves one slot, so maximum usable space is `capacity - 1`.
- `ringbuf_write` copies at most the free-space limit, splitting the copy at the array boundary when needed; advances `head` with a capacity mask.
- `ringbuf_read` copies at most the used-byte count, splitting at the array boundary when needed; advances `tail` with a capacity mask.
- `ringbuf_free` frees `data` and resets `data`, `capacity`, `head`, and `tail`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report stored bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — consume bytes
- Internal helpers: `is_power_of_two`, `min_size`

#### Constants and configuration
- `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` — status codes returned by `ringbuf_init`; definitions and values are not present in this file.

#### Dependencies
- `ringbuf.h` — ring-buffer declarations and types
- `<stdlib.h>` — `malloc`, `free`
- `<string.h>` — `memcpy`
- `uint8_t` — byte type used by read and write interfaces

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
