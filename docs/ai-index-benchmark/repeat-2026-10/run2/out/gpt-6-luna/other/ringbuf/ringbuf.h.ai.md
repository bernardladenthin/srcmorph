### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:51:06Z
- G: 
- A: 0.0.0
- X: file
---
> Provides fixed-capacity byte buffering for single-producer, single-consumer data streams.

#### Purpose
- Declares a ring buffer API for writing, reading, and measuring buffered bytes.
- Uses one reserved slot, so usable storage is `capacity - 1` bytes.

#### Type
- C header; exposes declarations with C linkage when included from C++.
- `ringbuf_t`: struct containing a byte-data pointer, capacity, and volatile producer/consumer indices.
- `ringbuf_status_t`: enum with `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.

#### Input
- `ringbuf_init`: buffer pointer and capacity; capacity must be a nonzero power of two.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes copied, up to the requested length.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases buffer resources.

#### Core logic
- `ringbuf_t.head` is the next write index and is owned by the producer.
- `ringbuf_t.tail` is the next read index and is owned by the consumer.
- Capacity must be a power of two; one slot remains unused to distinguish full from empty.
- Read and write operations copy up to the requested length.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — write bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — read bytes
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report buffered bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available capacity

#### Dependencies
- `<stddef.h>` — `size_t`
- `<stdint.h>` — `uint8_t`

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM`: allocation failed.
