### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:44:25Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity circular byte buffer with power-of-two sizing, using bitmask wraparound and two-chunk memcpy for O(1) read/write.

#### Purpose
- Implements a lock-free-style ring buffer (`ringbuf_t`) for byte-level FIFO storage with guaranteed power-of-two capacity.
- Provides `ringbuf_write` / `ringbuf_read` that handle wraparound in at most two `memcpy` calls.

#### Type
- C implementation file (`.c`); companion header `ringbuf.h` defines `ringbuf_t`, `ringbuf_status_t`, and public prototypes.
- Two `static` helpers: `is_power_of_two`, `min_size`.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be power of two).
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`.

#### Output
- `ringbuf_init` / `ringbuf_free`: mutate `ringbuf_t` in place; return `ringbuf_status_t` (init) or void (free).
- `ringbuf_used` / `ringbuf_free_space`: return `size_t` byte counts.
- `ringbuf_write` / `ringbuf_read`: return `size_t` — actual bytes transferred (may be less than requested).

#### Core logic
- Capacity must be a power of two; wraparound uses `& (capacity - 1)` instead of `%`.
- One slot is permanently reserved (free space = `capacity - 1 - used`) to distinguish full from empty without a separate count.
- `ringbuf_write`: computes `n = min(len, free_space)`; splits into `first = min(n, capacity - head)` bytes to end-of-array and `n - first` bytes from index 0; two `memcpy` calls; advances `head`.
- `ringbuf_read`: symmetric — splits at `capacity - tail`; two `memcpy` calls; advances `tail`.
- `ringbuf_used`: `(head - tail) & (capacity - 1)` — unsigned subtraction wraps correctly.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and zero-init buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release backing memory, reset fields.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — bytes currently occupied.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — bytes available for write.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append bytes, returns count written.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — consume bytes, returns count read.

#### Dependencies
- `ringbuf.h` (type and status definitions)
- `stdlib.h` (`malloc`, `free`)
- `string.h` (`memcpy`)

#### Exceptions / Errors
- `RINGBUF_EINVAL` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM` — `malloc` fails.
- No error path for `ringbuf_write` / `ringbuf_read`; they silently clamp to available space.
