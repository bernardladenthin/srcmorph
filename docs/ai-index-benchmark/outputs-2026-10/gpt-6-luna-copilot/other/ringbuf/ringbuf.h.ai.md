### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:36:52Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and transfers bytes through a fixed-capacity buffer shared by one producer and one consumer.

#### Purpose
- Declares the ring buffer data structure, status codes, and operations.
- Uses one unused slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Type
- C header; declares `ringbuf_t` and `ringbuf_status_t`.
- `ringbuf_t` contains a data pointer, capacity, and volatile producer-owned `head` and consumer-owned `tail`.
- Declares functions with C linkage when included from C++.

#### Input
- Initialization: buffer pointer and capacity.
- Writes: buffer, source byte array, and requested length.
- Reads: buffer, destination byte array, and requested length.
- Queries: buffer pointer.

#### Output
- Initialization returns a `ringbuf_status_t`.
- Read and write return the number of bytes transferred.
- Usage queries return used bytes or free space.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Requires a nonzero power-of-two capacity.
- Exposes single-producer/single-consumer byte-buffer operations.
- Read and write copy up to the requested length.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — write available bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — read available bytes
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report bytes used
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available space

#### Dependencies
- `<stddef.h>` — `size_t`
- `<stdint.h>` — `uint8_t`

#### Exceptions / Errors
- `RINGBUF_EINVAL` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM` — allocation failed.
