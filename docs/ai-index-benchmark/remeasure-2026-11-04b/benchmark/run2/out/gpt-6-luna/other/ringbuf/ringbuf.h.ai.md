### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:59:06Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Provides a fixed-capacity byte queue for transferring data between one producer and one consumer.

#### Purpose
- Declares the `ringbuf_t` API for initializing, writing, reading, and querying a byte ring buffer.
- Uses one unused slot to distinguish full from empty, so usable capacity is `capacity - 1` bytes.

#### Type
- C header with C++ linkage guards; declares `ringbuf_t` and `ringbuf_status_t`.
- `ringbuf_t`: byte storage pointer, capacity, and volatile producer-owned `head` and consumer-owned `tail` indices.
- Capacity must be a power of two.

#### Input
- Initialization: `ringbuf_t *rb`, `size_t capacity`.
- Write: `ringbuf_t *rb`, source bytes `const uint8_t *src`, and requested `size_t len`.
- Read: `ringbuf_t *rb`, destination bytes `uint8_t *dst`, and requested `size_t len`.
- Queries: `const ringbuf_t *rb`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; the other declared operations return `size_t`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes actually copied.
- Declares `ringbuf_free` to release buffer resources.

#### Core logic
- Specifies a fixed-capacity, single-producer/single-consumer byte buffer.
- Reserves one slot; capacity must be nonzero and a power of two.
- Declares operations to copy up to a requested length and query used or free space; implementation is not present in this header.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes in
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report bytes stored
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes

#### Constants and configuration
- `RINGBUF_H`: include guard.
- `RINGBUF_OK = 0`: successful status.
- `RINGBUF_EINVAL = -1`: zero or non-power-of-two capacity.
- `RINGBUF_ENOMEM = -2`: allocation failure.

#### Dependencies
- Standard headers: `<stddef.h>`, `<stdint.h>`.
- Uses `size_t` and `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` declares `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` for allocation failure.
