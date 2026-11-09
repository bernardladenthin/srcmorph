### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:07:16Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Provides fixed-capacity byte buffering between a single producer and single consumer, allowing each side to write or read available bytes without exceeding capacity.

#### Purpose
- Declares the ring buffer API and data structure for storing and transferring bytes.
- Uses one unused slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Type
- C header; declares `ringbuf_t` and `ringbuf_status_t`.
- `ringbuf_t` contains a byte-array pointer, capacity, and volatile producer-owned `head` and consumer-owned `tail` indices.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte array, and requested length.
- `ringbuf_read`: buffer, destination byte array, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a status; `ringbuf_write` and `ringbuf_read` return the number of bytes copied.
- Read/write operations update the buffer’s indices and byte storage; `ringbuf_free` releases buffer storage.
- `ringbuf_used` and `ringbuf_free_space` return occupancy and available space.

#### Core logic
- Requires capacity to be a nonzero power of two; the ring buffer holds at most `capacity - 1` bytes.
- Exposes partial reads and writes: each copies up to the requested length and reports the actual byte count.
- Separates producer and consumer index ownership for single-producer/single-consumer use.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer resources
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes

#### Constants and configuration
- `RINGBUF_OK = 0` — success.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.
- `RINGBUF_H` — include guard.

#### Dependencies
- `<stddef.h>` — `size_t`.
- `<stdint.h>` — `uint8_t`.
- C++ compatibility via `extern "C"`.

#### Exceptions / Errors
- Initialization reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
