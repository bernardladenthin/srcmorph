### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:03:44Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity single-producer single-consumer byte ring buffer for lock-free streaming data transfer.

#### Purpose
- Implements a thread-safe circular buffer for passing bytes between one producer and one consumer.
- Manages allocation, capacity validation, and read/write tracking without blocking.

#### Type
C header file defining `ringbuf_t` struct and `ringbuf_status_t` enum with exported functions.

#### Input
- `ringbuf_init`: pointer to `ringbuf_t`, `capacity` (must be power of two, non-zero).
- `ringbuf_write`: pointer to `ringbuf_t`, source byte pointer `src`, length `len`.
- `ringbuf_read`: pointer to `ringbuf_t`, destination byte pointer `dst`, length `len`.
- `ringbuf_used` / `ringbuf_free_space`: const pointer to `ringbuf_t`.

#### Output
- `ringbuf_init`: `ringbuf_status_t` indicating success, invalid capacity, or allocation failure.
- `ringbuf_free`: frees internal `data` buffer, no return.
- `ringbuf_write` / `ringbuf_read`: `size_t` count of bytes actually copied.
- `ringbuf_used`: `size_t` bytes currently in buffer.
- `ringbuf_free_space`: `size_t` writable slots remaining.

#### Core logic
- `ringbuf_t` holds `data` pointer, `capacity`, volatile `head` (producer-owned next write index), and volatile `tail` (consumer-owned next read index).
- Capacity must be a power of two; one slot is reserved to distinguish full from empty, yielding capacity - 1 usable bytes.
- `ringbuf_write` copies up to `len` bytes from `src` into the ring and advances `head`.
- `ringbuf_read` copies up to `len` bytes from the ring into `dst` and advances `tail`.
- `ringbuf_used` computes `head - tail`; `ringbuf_free_space` computes `capacity - 1 - used`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` initializes buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` deallocates buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` writes bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` reads bytes.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` returns occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` returns available write slots.

#### Dependencies
- `<stddef.h>` for `size_t`.
- `<stdint.h>` for `uint8_t`.

#### Exceptions / Errors
- `RINGBUF_EINVAL` returned when capacity is zero or not a power of two.
- `RINGBUF_ENOMEM` returned when memory allocation fails.
