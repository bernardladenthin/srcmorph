### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:22:01Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and transfers bytes through a fixed-capacity, single-producer/single-consumer ring buffer.

#### Purpose
- Declares the `ringbuf_t` byte-buffer interface with capacity-minus-one usable bytes.
- Defines status codes for initialization and allocation outcomes.

#### Type
- C header; exposes C-compatible declarations inside `extern "C"` when included from C++.
- `ringbuf_t`: struct containing a data pointer, capacity, and volatile producer-owned `head` and consumer-owned `tail` indices.
- `ringbuf_status_t`: enum of ring-buffer status codes.

#### Input
- `ringbuf_init`: buffer pointer and capacity; capacity must be a nonzero power of two.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.

#### Output
- Initialization returns `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`.
- Read and write return the number of bytes actually transferred.
- Used and free-space queries return byte counts.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Declares a fixed-capacity ring buffer for one producer and one consumer.
- Reserves one slot to distinguish full from empty; usable capacity is `capacity - 1`.
- Exposes operations to initialize, free, write, read, and query occupancy.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer resources
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes from buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — query occupied bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — query available bytes

#### Dependencies
- `<stddef.h>` — `size_t`
- `<stdint.h>` — `uint8_t`

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM`: allocation failed during initialization.
