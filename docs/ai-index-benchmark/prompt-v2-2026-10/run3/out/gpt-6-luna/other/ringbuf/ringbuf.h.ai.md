### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:59:58Z
- G: 
- A: 0.0.0
- X: file
---
> Provides fixed-capacity byte buffering for transferring data between a single producer and a single consumer.

#### Purpose
- Declares an SPSC ring buffer that stores up to `capacity - 1` bytes.
- Exposes initialization, cleanup, byte transfer, and occupancy operations.

#### Type
- C header; declarations use C linkage when included from C++.
- `ringbuf_t`: structure containing the data pointer, capacity, and producer/consumer indices.
- `ringbuf_status_t`: initialization status enum.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source bytes, and requested length.
- `ringbuf_read`: buffer, destination bytes, and requested length.
- `ringbuf_used` and `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases buffer resources; implementation details are not shown.

#### Core logic
- Capacity must be a nonzero power of two.
- One slot is reserved, so usable capacity is `capacity - 1`.
- `head` tracks the next write index and is owned by the producer; `tail` tracks the next read index and is owned by the consumer.
- Read and write operations copy up to the requested length and report the amount actually copied.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out of buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_H`: header include guard.
- `RINGBUF_OK = 0`: successful status.
- `RINGBUF_EINVAL = -1`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2`: allocation failed.

#### Dependencies
- Standard headers: `<stddef.h>`, `<stdint.h>`.
- Types: `size_t`, `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` can return `RINGBUF_EINVAL` for invalid capacity or `RINGBUF_ENOMEM` if allocation fails.
