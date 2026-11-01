### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:22:44Z
- G: 
- A: 0.0.0
- X: file
---
> Provides bounded byte buffering between a single producer and a single consumer.

#### Purpose
- Declares a fixed-capacity ring buffer API for writing, reading, and querying buffered bytes.
- Capacity must be a power of two; one slot remains unused, so usable capacity is `capacity - 1`.

#### Type
- C header `ringbuf.h`; declarations use `extern "C"` when included from C++.
- `ringbuf_t`: struct containing the data pointer, capacity, and producer/consumer indices.
- `ringbuf_status_t`: enum of initialization status codes.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity`.
- `ringbuf_write`: buffer, source byte pointer, requested length.
- `ringbuf_read`: buffer, destination byte pointer, requested length.
- `ringbuf_used` and `ringbuf_free_space`: buffer pointer.
- `ringbuf_free`: buffer pointer.

#### Output
- `ringbuf_init` returns `ringbuf_status_t`.
- Read/write and space-query functions return `size_t`.
- `ringbuf_free` returns no value; the header declares no other side effects.

#### Core logic
- Exposes producer-owned `head` as the next write index and consumer-owned `tail` as the next read index; both are `volatile size_t`.
- Declares read/write operations that copy up to the requested length and report the number of bytes copied.
- Declares queries for used bytes and free space.
- Declares initialization validation for zero or non-power-of-two capacity and allocation failure; implementation is not present in this header.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available space.

#### Constants and configuration
- `RINGBUF_H`: include guard.
- `RINGBUF_OK = 0`: successful status.
- `RINGBUF_EINVAL = -1`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2`: allocation failed.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.
- C++ linkage macros: `__cplusplus`, `extern "C"`.

#### Exceptions / Errors
- Initialization status codes: `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
