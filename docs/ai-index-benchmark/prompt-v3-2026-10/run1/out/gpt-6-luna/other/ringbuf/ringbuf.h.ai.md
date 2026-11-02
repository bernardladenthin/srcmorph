### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:50:43Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers byte streams between a single producer and consumer while limiting storage to a fixed capacity.

#### Purpose
- Declares a fixed-capacity byte ring buffer API for single-producer, single-consumer use.
- Uses one unused slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Type
- C header `ringbuf.h`; C++ callers use C linkage.
- `ringbuf_t`: struct holding a data pointer, capacity, and producer-owned `head` and consumer-owned `tail` indices.
- `ringbuf_status_t`: enum of initialization status codes.

#### Input
- Initialization receives a `ringbuf_t *` and capacity.
- Write receives a buffer, source bytes, and requested length.
- Read receives a buffer, destination bytes, and requested length.
- Capacity must be a nonzero power of two.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_init` allocates buffer storage; `ringbuf_free` releases it.

#### Core logic
- Declares state for a fixed-capacity byte ring, with `head` tracking the next write index and `tail` the next read index.
- Provides partial read/write operations: each copies up to the requested length and reports the actual amount.
- Exposes queries for occupied bytes and available space.
- Requires power-of-two capacity and reserves one slot to distinguish full from empty.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer storage.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out of buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_H`: include guard.
- `RINGBUF_OK = 0`: success status.
- `RINGBUF_EINVAL = -1`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2`: allocation failed.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.
- C++ linkage declarations: `extern "C"`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL`.
- `ringbuf_init` reports allocation failure with `RINGBUF_ENOMEM`.
