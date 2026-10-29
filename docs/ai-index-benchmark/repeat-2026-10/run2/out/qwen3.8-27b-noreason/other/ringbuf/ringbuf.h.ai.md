### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:09:03Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity, power-of-two byte ring buffer API for single-producer/single-consumer lock-free data transfer.

#### Purpose
- Defines a thread-safe (single-producer/single-consumer) circular byte buffer structure and its lifecycle/operation functions.
- Enforces power-of-two capacity constraints to enable bitwise masking for index wrapping.

#### Type
- C header file (`ringbuf.h`)
- Defines `ringbuf_t` struct and `ringbuf_status_t` enum
- Exports C API via `extern "C"` guards

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be power of two)
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`
- `ringbuf_used` / `ringbuf_free_space`: `const ringbuf_t *rb`

#### Output
- `ringbuf_init`: `ringbuf_status_t` (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`)
- `ringbuf_write`: `size_t` (bytes actually copied)
- `ringbuf_read`: `size_t` (bytes actually copied)
- `ringbuf_used`: `size_t` (bytes currently stored)
- `ringbuf_free_space`: `size_t` (bytes available for writing)
- `ringbuf_free`: `void` (releases allocated memory)

#### Core logic
- Capacity must be a power of two; effective storage is `capacity - 1` bytes (one slot reserved to distinguish full/empty states).
- `head` index is owned by the producer; `tail` index is owned by the consumer.
- Both `head` and `tail` are marked `volatile` to ensure visibility across threads without explicit locks.
- `ringbuf_write` and `ringbuf_read` copy up to `len` bytes, returning the actual count transferred (may be less than `len` if buffer is full/empty).
- `ringbuf_init` validates capacity (non-zero, power of two) and allocates the `data` buffer.
- `ringbuf_free` deallocates the `data` buffer.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — Initialize buffer with validated capacity
- `ringbuf_free(ringbuf_t *rb) -> void` — Release allocated buffer memory
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — Copy bytes into buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — Copy bytes out of buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — Return current byte count
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — Return available byte count

#### Dependencies
- `<stddef.h>` (for `size_t`)
- `<stdint.h>` (for `uint8_t`)

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Returned by `ringbuf_init` if capacity is zero or not a power of two
- `RINGBUF_ENOMEM`: Returned by `ringbuf_init` if memory allocation fails
