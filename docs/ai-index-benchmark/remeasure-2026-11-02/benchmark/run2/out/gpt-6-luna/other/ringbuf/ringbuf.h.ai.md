### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:45:18Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Stores and transfers bytes between a single producer and consumer using a fixed-capacity ring buffer.

#### Purpose
- Declares the `ringbuf_t` byte-buffer API for bounded writes, reads, and occupancy queries.
- Supports single-producer/single-consumer use; usable capacity is one less than the configured capacity.

#### Type
- C header; public declarations use `extern "C"` linkage when included from C++.
- `ringbuf_t`: struct holding `data`, `capacity`, producer-owned `head`, and consumer-owned `tail`.
- `ringbuf_status_t`: enum of initialization status codes.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.
- Capacity must be a nonzero power of two.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; buffer state includes allocated data and initialized indices.
- `ringbuf_write` and `ringbuf_read` return the number of bytes actually copied, up to the requested length.
- Occupancy queries return used or free byte counts.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Capacity is constrained to a power of two; one slot remains unused to distinguish full from empty.
- Producer advances `head` for writes; consumer advances `tail` for reads.
- The declared transfer operations copy only the bytes that fit or are available.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_H`: include guard.
- `RINGBUF_OK = 0`: successful status.
- `RINGBUF_EINVAL = -1`: zero or non-power-of-two capacity.
- `RINGBUF_ENOMEM = -2`: allocation failure.
- `ringbuf_t.capacity`: configured capacity; usable storage is `capacity - 1`.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.

#### Exceptions / Errors
- Initialization reports invalid capacity with `RINGBUF_EINVAL`.
- Initialization reports allocation failure with `RINGBUF_ENOMEM`.
