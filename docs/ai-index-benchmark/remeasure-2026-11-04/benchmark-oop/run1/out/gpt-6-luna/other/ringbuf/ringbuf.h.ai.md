### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:22:26Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer.

#### Purpose
- Defines the `ringbuf_t` data structure and API for storing and retrieving bytes in a bounded ring buffer.
- Reserves one slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Type
- C header; declares `ringbuf_t` struct and `ringbuf_status_t` enum.
- Exposes declarations with C linkage when included from C++.

#### Input
- `ringbuf_init`: buffer pointer and requested `capacity`.
- `ringbuf_write`: buffer pointer, source byte array, and requested `len`.
- `ringbuf_read`: buffer pointer, destination byte array, and requested `len`.
- `ringbuf_used` and `ringbuf_free_space`: buffer pointer.
- Capacity must be a nonzero power of two.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases buffer resources; implementation details are not present.

#### Core logic
- `ringbuf_t` stores a byte-array pointer, capacity, producer-owned next-write index (`head`), and consumer-owned next-read index (`tail`).
- Declares partial writes and reads: each copies up to the requested length and reports the actual byte count.
- Uses a power-of-two capacity requirement and leaves one slot unused.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report used bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_H`: include guard.
- `RINGBUF_OK = 0`: success status.
- `RINGBUF_EINVAL = -1`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2`: allocation failed.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
