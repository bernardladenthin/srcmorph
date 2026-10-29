### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:59:04Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity, power-of-two byte ring buffer with wrap-around read/write operations for circular data storage.

#### Purpose
- Provides a lock-free, single-producer/single-consumer circular buffer for `uint8_t` data.
- Manages allocation, deallocation, and capacity tracking for a ring buffer instance.

#### Type
- C implementation file (`ringbuf.c`); defines static helpers and public API functions operating on `ringbuf_t` (defined in `ringbuf.h`).

#### Input
- `ringbuf_t *rb`: pointer to the ring buffer structure.
- `size_t capacity`: buffer size (must be a power of two).
- `const uint8_t *src` / `uint8_t *dst`: source/destination byte arrays for write/read.
- `size_t len`: number of bytes to write or read.

#### Output
- `ringbuf_status_t`: status code (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) from `ringbuf_init`.
- `size_t`: number of bytes actually written or read (may be less than requested).
- Side effects: modifies `rb->head`, `rb->tail`, `rb->data`, `rb->capacity`; allocates/frees heap memory.

#### Core logic
- Capacity must be a power of two; validated via bitwise check `n & (n - 1) == 0`.
- Uses bitwise AND with `capacity - 1` for modular index arithmetic (head/tail wrap-around).
- `ringbuf_write`: copies data in up to two `memcpy` chunks (end-of-array then start) to handle wrap-around; advances `head`.
- `ringbuf_read`: copies data in up to two `memcpy` chunks from `tail`; advances `tail`.
- `ringbuf_used`: computes `(head - tail) & (capacity - 1)` for current occupancy.
- `ringbuf_free_space`: returns `capacity - 1 - used` (one slot reserved to distinguish full from empty).
- `ringbuf_free`: releases `rb->data` and resets all fields to zero.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — deallocate and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — return bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — return available write space.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — write bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — read bytes from buffer.

#### Dependencies
- `ringbuf.h` (type definitions, status codes)
- `stdlib.h` (`malloc`, `free`)
- `string.h` (`memcpy`)

#### Exceptions / Errors
- `RINGBUF_EINVAL`: returned if `capacity` is not a power of two.
- `RINGBUF_ENOMEM`: returned if `malloc` fails.
- No error handling for `memcpy` overflow; caller must ensure `src`/`dst` are valid for `len` bytes.
