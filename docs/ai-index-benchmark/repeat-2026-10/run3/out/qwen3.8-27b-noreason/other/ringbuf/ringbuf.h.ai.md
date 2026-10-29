### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:49:12Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity, power-of-two byte ring buffer API for single-producer/single-consumer lock-free data exchange.

#### Purpose
- Defines a thread-safe (via volatile indices) circular byte buffer structure and its lifecycle/IO functions.
- Enforces power-of-two capacity to enable bitwise masking for index wrapping.

#### Type
- Header file (`ringbuf.h`) in C.
- Defines `ringbuf_t` struct and `ringbuf_status_t` enum.
- Exports C API via `extern "C"` guards for C++ compatibility.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be power of two).
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`.
- `ringbuf_used` / `ringbuf_free_space`: `const ringbuf_t *rb`.

#### Output
- `ringbuf_init`: `ringbuf_status_t` (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_write` / `ringbuf_read`: `size_t` (bytes actually copied).
- `ringbuf_used` / `ringbuf_free_space`: `size_t` (byte count).
- `ringbuf_free`: `void` (releases internal `data` buffer).

#### Core logic
- Capacity is constrained to power-of-two; effective storage is `capacity - 1` bytes (one slot reserved to distinguish full/empty).
- `head` index is owned by the producer; `tail` index is owned by the consumer.
- `ringbuf_init` validates capacity (non-zero, power of two) and allocates the `data` buffer.
- `ringbuf_write` copies up to `len` bytes from `src` into the buffer starting at `head`, updating `head` atomically/volatilely.
- `ringbuf_read` copies up to `len` bytes from the buffer starting at `tail` into `dst`, updating `tail` atomically/volatilely.
- `ringbuf_used` calculates current occupancy; `ringbuf_free_space` calculates available capacity.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` : Initialize buffer with validation.
- `ringbuf_free(ringbuf_t *rb) -> void` : Release allocated memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` : Write bytes to buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` : Read bytes from buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` : Get used byte count.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` : Get free byte count.

#### Dependencies
- `<stddef.h>` for `size_t`.
- `<stdint.h>` for `uint8_t`.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Returned by `ringbuf_init` if capacity is zero or not a power of two.
- `RINGBUF_ENOMEM`: Returned by `ringbuf_init` if memory allocation fails.
