### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:16:13Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers bytes between a single producer and consumer with fixed capacity and partial reads or writes.

#### Purpose
- Declares a fixed-capacity byte ring buffer API for single-producer, single-consumer use.
- Uses one reserved slot, so usable capacity is `capacity - 1`.

#### Type
- C header; declares `ringbuf_t` struct and `ringbuf_status_t` enum.
- `ringbuf_t` contains `uint8_t *data`, `size_t capacity`, and volatile `size_t head` and `tail`.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used` and `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- Read/write return the number of bytes actually copied.
- Used/free-space queries return `size_t`.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Requires capacity to be a nonzero power of two.
- Reserves one slot to distinguish full from empty; effective storage is `capacity - 1` bytes.
- `head` is the producer’s next write index; `tail` is the consumer’s next read index.
- Read/write operations copy up to the requested length and report the actual byte count.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize ring buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release ring buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_OK = 0` — success.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.

#### Dependencies
- Includes `<stddef.h>` and `<stdint.h>`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL`.
- `ringbuf_init` reports allocation failure with `RINGBUF_ENOMEM`.
