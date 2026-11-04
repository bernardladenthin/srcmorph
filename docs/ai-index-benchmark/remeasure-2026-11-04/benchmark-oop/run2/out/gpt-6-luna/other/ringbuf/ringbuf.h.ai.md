### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:37:55Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer, with partial reads and writes.

#### Purpose
- Declares the `ringbuf_t` API for storing and transferring bytes through a bounded ring buffer.
- Reserves one slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Type
- C header; C++ callers receive C linkage.
- `ringbuf_t`: struct containing the byte storage pointer, capacity, and producer-owned `head` and consumer-owned `tail` indices.
- `ringbuf_status_t`: enum of initialization status codes.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer pointer, source byte pointer, and requested length.
- `ringbuf_read`: buffer pointer, destination byte pointer, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.
- Capacity must be a nonzero power of two.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; `ringbuf_write` and `ringbuf_read` return bytes actually copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_init` may allocate storage; `ringbuf_free` releases buffer storage.

#### Core logic
- Declares a fixed-capacity, single-producer/single-consumer byte ring buffer.
- Uses `head` as the next write index and `tail` as the next read index.
- Leaves one slot unused; the header does not specify the index-update or synchronization implementation.
- Read and write operations copy up to the requested length and report the actual count.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_OK = 0` — success.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.
- Capacity requirement: power of two; usable capacity is one less than configured capacity.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
