### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:23:03Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity byte queue for passing data between a single producer and a single consumer.

#### Purpose
- Declares the ring buffer data structure, status codes, and operations for initializing, freeing, reading, writing, and checking available bytes.
- Reserves one slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Type
- C header; exposes declarations with C linkage when included from C++.
- `ringbuf_t`: struct containing a byte buffer, capacity, and producer-owned `head` / consumer-owned `tail` indices.
- `ringbuf_status_t`: enum for success, invalid capacity, and allocation failure.

#### Input
- `ringbuf_init`: ring buffer pointer and capacity, which must be a nonzero power of two.
- `ringbuf_write`: ring buffer, source byte pointer, and requested length.
- `ringbuf_read`: ring buffer, destination byte pointer, and requested length.
- Usage queries: ring buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; `ringbuf_write` and `ringbuf_read` return the number of bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases the ring buffer resources.

#### Core logic
- Defines fixed-capacity byte storage with wraparound indices represented by `head` and `tail`.
- Requires power-of-two capacity and leaves one slot unused.
- Read and write operations copy up to the requested length and report the actual amount copied.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report queued bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes

#### Dependencies
- `<stddef.h>` for `size_t`.
- `<stdint.h>` for `uint8_t`.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM`: allocation failed.
