### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:30:44Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Stores and retrieves byte streams through a fixed-capacity buffer shared by one producer and one consumer.

#### Purpose
- Declares the API and data types for a single-producer, single-consumer byte ring buffer.
- Supports bounded writes and reads, plus queries for used and free capacity.

#### Type
- C header `ringbuf.h`, with C++ linkage guards.
- `ringbuf_t`: struct containing the byte storage pointer, capacity, and producer/consumer indices.
- `ringbuf_status_t`: enum of initialization status codes.

#### Input
- `ringbuf_init`: ring buffer pointer and requested capacity.
- `ringbuf_write`: ring buffer, source byte pointer, and requested length.
- `ringbuf_read`: ring buffer, destination byte pointer, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: ring buffer pointer.
- Capacity must be a power of two; one slot is reserved, so usable capacity is `capacity - 1`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes actually copied.
- Usage queries return byte counts.
- `ringbuf_free` releases the buffer’s resources.

#### Core logic
- Declares fixed-capacity byte storage with `head` as the next producer write index and `tail` as the next consumer read index.
- Provides partial read/write operations that copy up to the requested length.
- Exposes queries for occupied and available space.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — write available bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — read available bytes
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes

#### Constants and configuration
- `RINGBUF_H`: include guard.
- `RINGBUF_OK = 0`: successful status.
- `RINGBUF_EINVAL = -1`: zero or non-power-of-two capacity.
- `RINGBUF_ENOMEM = -2`: allocation failure.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.
- C++ linkage guards: `__cplusplus`, `extern "C"`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL`.
- `ringbuf_init` reports allocation failure with `RINGBUF_ENOMEM`.
