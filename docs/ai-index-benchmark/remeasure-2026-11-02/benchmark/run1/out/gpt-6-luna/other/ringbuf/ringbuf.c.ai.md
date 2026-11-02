### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:28:43Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Provides bounded byte-stream buffering so producers and consumers can write and read data across a circular buffer’s wraparound point.

#### Purpose
- Implements byte-oriented ring-buffer allocation, capacity tracking, writes, reads, and cleanup.
- Leaves one slot unused to distinguish full from empty.

#### Type
- C implementation file; operates on `ringbuf_t` and returns `ringbuf_status_t`, declared in `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer `rb`, requested `capacity`.
- `ringbuf_write`: buffer `rb`, source bytes `src`, requested length `len`.
- `ringbuf_read`: buffer `rb`, destination bytes `dst`, requested length `len`.
- `ringbuf_free`, `ringbuf_used`, `ringbuf_free_space`: buffer pointer `rb`.

#### Output
- `ringbuf_init` returns a status; allocates `rb->data` and initializes buffer state on success.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred.
- `ringbuf_free` releases storage and resets buffer fields; used/free-space queries return byte counts.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities, allocates `capacity` bytes, then sets `head` and `tail` to zero.
- `ringbuf_used` computes `(head - tail) & (capacity - 1)`; `ringbuf_free_space` reserves one slot, returning `capacity - 1 - used`.
- `ringbuf_write` transfers at most the available free space; copies in up to two chunks across the array boundary, then advances `head` with a capacity mask.
- `ringbuf_read` transfers at most the number of used bytes; copies in up to two chunks across the boundary, then advances `tail` with a capacity mask.
- `ringbuf_free` releases allocated storage and resets `data`, `capacity`, `head`, and `tail`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report buffered bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — consume bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- Status codes referenced: `RINGBUF_EINVAL` (invalid capacity), `RINGBUF_ENOMEM` (allocation failure), `RINGBUF_OK` (success); numeric values are not specified here.
- Capacity must be a nonzero power of two.

#### Dependencies
- `"ringbuf.h"` — ring-buffer types and status codes.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- `uint8_t`, `size_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
