### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:37:34Z
- G: 
- A: 0.0.0
- X: file
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer, allowing writes and reads up to available space or data.

#### Purpose
- Declares a ring buffer that stores bytes in a power-of-two-capacity buffer, with one slot reserved to distinguish full from empty.

#### Type
- C header `ringbuf.h`; declarations use C linkage when included from C++.
- `ringbuf_t`: struct containing the data pointer, capacity, producer-owned write index `head`, and consumer-owned read index `tail`.
- `ringbuf_status_t`: enum of initialization status codes.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer pointer, source byte pointer, and requested length.
- `ringbuf_read`: buffer pointer, destination byte pointer, and requested length.
- `ringbuf_used` and `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Declares fixed-capacity storage with capacity required to be a power of two.
- Usable storage is `capacity - 1` bytes.
- `head` marks the next producer write index; `tail` marks the next consumer read index.
- Read and write operations copy up to the requested length and report the amount copied.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize ring buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release ring buffer resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out of buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report bytes in use.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_OK = 0` — successful status.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.

#### Dependencies
- Standard headers: `<stddef.h>`, `<stdint.h>`.
- Types: `size_t`, `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` status codes declare invalid-capacity and allocation-failure conditions.
