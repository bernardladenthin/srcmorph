### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:45:52Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer.

#### Purpose
- Declares an API for initializing, writing to, reading from, querying, and freeing a byte ring buffer.
- Uses one reserved slot, so usable capacity is `capacity - 1`.

#### Type
- C header `ringbuf.h`, with C++ linkage compatibility.
- `ringbuf_t`: struct containing a data pointer, capacity, and volatile producer-owned `head` and consumer-owned `tail` indices.
- `ringbuf_status_t`: enum of initialization status codes.
- Buffer capacity must be a power of two.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes copied, up to the requested length.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Declares a fixed-capacity byte ring buffer intended for single-producer, single-consumer use.
- Tracks the next write and read positions with `head` and `tail`.
- Reserves one slot to distinguish full from empty; usable storage is one byte less than capacity.
- Declares partial-copy semantics for reads and writes.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out of buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report bytes currently used.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available byte space.

#### Constants and configuration
- `RINGBUF_H`: include guard.
- Capacity constraint: nonzero power of two; usable capacity is `capacity - 1`.
- `RINGBUF_OK = 0`: success.
- `RINGBUF_EINVAL = -1`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2`: allocation failed.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.
- C++ linkage declarations via `extern "C"`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL`.
- `ringbuf_init` reports allocation failure with `RINGBUF_ENOMEM`.
