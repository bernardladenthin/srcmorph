### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:59:11Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity, power-of-two byte ring buffer for lock-free single-producer single-consumer (SPSC) data exchange.

#### Purpose
- Implements a thread-safe circular buffer for byte-level data transfer between one producer and one consumer.
- Manages allocation, initialization, and deallocation of the underlying byte array.

#### Type
- Header file (`ringbuf.h`) in C.
- Defines `ringbuf_t` struct and `ringbuf_status_t` enum.
- Exports C API with `extern "C"` guards for C++ compatibility.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be power of two).
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`.
- `ringbuf_used` / `ringbuf_free_space`: `const ringbuf_t *rb`.

#### Output
- `ringbuf_init`: `ringbuf_status_t` (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_write` / `ringbuf_read`: `size_t` (actual bytes copied).
- `ringbuf_used` / `ringbuf_free_space`: `size_t` (byte count).
- `ringbuf_free`: `void` (releases allocated memory).

#### Core logic
- Capacity must be a power of two; one slot is reserved to distinguish full from empty, so usable space is `capacity - 1`.
- `head` (next write index) is owned by the producer; `tail` (next read index) is owned by the consumer.
- `head` and `tail` are marked `volatile` to ensure visibility across threads without explicit locks.
- `ringbuf_write` copies up to `len` bytes from `src` into the buffer, wrapping around at capacity.
- `ringbuf_read` copies up to `len` bytes from the buffer into `dst`, wrapping around at capacity.
- `ringbuf_used` computes bytes currently stored; `ringbuf_free_space` computes remaining writable bytes.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — write bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — read bytes from buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — return bytes currently in use.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — return available write space.

#### Dependencies
- `<stddef.h>` — `size_t`.
- `<stdint.h>` — `uint8_t`.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM`: memory allocation failed during `ringbuf_init`.
