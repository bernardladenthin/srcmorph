### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:49:39Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Stores and transfers bytes between a single producer and consumer through a fixed-capacity ring buffer.

#### Purpose
- Declares the `ringbuf_t` byte buffer API for bounded writes, reads, and occupancy queries.
- Supports single-producer/single-consumer use; one slot remains unused to distinguish full from empty.

#### Type
- C header with C++ linkage guards.
- `ringbuf_t`: struct containing a data pointer, capacity, and volatile producer-owned `head` and consumer-owned `tail` indices.
- `ringbuf_status_t`: status enum.

#### Input
- Buffer pointer and power-of-two `capacity` for initialization.
- Source/destination byte pointers and requested `len` for transfers.
- `ringbuf_t` pointer for lifecycle and occupancy operations.

#### Output
- `ringbuf_init` returns a status code.
- `ringbuf_write` and `ringbuf_read` return bytes actually copied, up to the requested length.
- `ringbuf_used` and `ringbuf_free_space` return occupancy measures.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Requires capacity to be a nonzero power of two; usable storage is `capacity - 1` bytes.
- Exposes producer write and consumer read operations that may copy fewer bytes than requested.
- Exposes queries for used bytes and available space.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — query occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — query available bytes.
- Internal helpers: none declared.

#### Constants and configuration
- `RINGBUF_H`: include guard.
- `RINGBUF_OK = 0`: success.
- `RINGBUF_EINVAL = -1`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2`: allocation failed.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.
- C++ linkage guards: `__cplusplus`, `extern "C"`.

#### Exceptions / Errors
- Initialization reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
