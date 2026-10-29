### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:48:53Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity, power-of-two byte ring buffer with wrap-around read/write operations for circular data storage.

#### Purpose
- Provides a thread-unsafe, fixed-size circular buffer (`ringbuf_t`) for byte-level data queuing.
- Uses bitwise masking (power-of-two capacity) for O(1) index wrap-around.

#### Type
- C implementation file (`ringbuf.c`); includes `ringbuf.h`.
- Defines static helpers and public functions operating on `ringbuf_t` struct.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be power of two).
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`.
- `ringbuf_used` / `ringbuf_free_space`: `const ringbuf_t *rb`.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_write` / `ringbuf_read` return `size_t` (bytes actually transferred).
- `ringbuf_used` / `ringbuf_free_space` return `size_t` (bytes in use / available).
- `ringbuf_free` releases heap memory and resets struct fields.

#### Core logic
- Capacity validation: `is_power_of_two` checks `n != 0 && (n & (n-1)) == 0`; rejects non-power-of-two sizes with `RINGBUF_EINVAL`.
- Allocation: `malloc(capacity)` for raw byte storage; on failure returns `RINGBUF_ENOMEM`.
- Index arithmetic: head/tail wrap via `& (capacity - 1)` bitmask.
- `ringbuf_used`: computes `(head - tail) & (capacity - 1)` to get occupied byte count.
- `ringbuf_free_space`: `capacity - 1 - used` (one slot reserved to distinguish full/empty).
- `ringbuf_write`: copies up to `min(len, free_space)` bytes in at most two `memcpy` chunks (to end of array, then from index 0); advances `head` with bitmask.
- `ringbuf_read`: copies up to `min(len, used)` bytes in at most two `memcpy` chunks (from tail to end, then from index 0); advances `tail` with bitmask.
- `ringbuf_free`: `free(rb->data)`, nulls pointer, zeroes capacity/head/tail.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer memory.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — bytes available for write.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue bytes from buffer.

#### Dependencies
- `ringbuf.h` (struct `ringbuf_t`, `ringbuf_status_t`, constants `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `stdlib.h` (`malloc`, `free`).
- `string.h` (`memcpy`).

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM`: `malloc` fails.
- No error on partial read/write; caller checks returned `size_t` against requested `len`.
