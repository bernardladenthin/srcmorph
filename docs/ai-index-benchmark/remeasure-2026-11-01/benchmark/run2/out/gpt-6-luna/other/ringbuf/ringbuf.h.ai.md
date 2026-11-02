### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:44:15Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and transfers bytes through a fixed-capacity ring buffer shared by one producer and one consumer.

#### Purpose
- Declares a byte ring buffer API for single-producer, single-consumer use.
- Capacity must be a power of two; usable storage is `capacity - 1` bytes.

#### Type
- C header; declares `ringbuf_t` and `ringbuf_status_t`.
- `ringbuf_t` contains `data`, `capacity`, volatile producer-owned `head`, and volatile consumer-owned `tail`.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte array, and requested length.
- `ringbuf_read`: buffer, destination byte array, and requested length.
- `ringbuf_used` and `ringbuf_free_space`: buffer pointer.

#### Output
- Initialization returns a `ringbuf_status_t`; read/write return the number of bytes copied.
- Read/write transfer up to the requested length. Usage queries return occupied or available byte counts.
- `ringbuf_init` allocates buffer storage; `ringbuf_free` releases it.

#### Core logic
- Declares a fixed-capacity circular byte buffer with producer and consumer indices.
- Leaves one slot unused to distinguish full from empty.
- Requires a power-of-two capacity.
- Exposes operations to initialize/free the buffer, transfer bytes, and query used/free space.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer storage
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes

#### Constants and configuration
- `RINGBUF_OK = 0` — operation succeeded
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two
- `RINGBUF_ENOMEM = -2` — allocation failed

#### Dependencies
- Standard headers: `<stddef.h>`, `<stdint.h>`
- Types: `size_t`, `uint8_t`

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL`.
- `ringbuf_init` reports allocation failure with `RINGBUF_ENOMEM`.
