### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:12:57Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and retrieves bytes through a fixed-capacity ring buffer shared by one producer and one consumer.

#### Purpose
- Declares a byte ring buffer API for bounded writes and reads.
- Uses one unused slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Type
- C header `ringbuf.h`; declares `ringbuf_t` and `ringbuf_status_t`.
- `ringbuf_t` fields: `data` (`uint8_t *`), `capacity` (`size_t`), `head` and `tail` (`volatile size_t`).
- Capacity must be a nonzero power of two.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a status; read/write return the number of bytes actually copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_init` allocates buffer storage; `ringbuf_free` releases it.

#### Core logic
- Declares storage and indices for a fixed-capacity circular byte buffer.
- Assigns `head` as the producer’s next write index and `tail` as the consumer’s next read index.
- Read/write operations copy up to the requested length.
- Reserves one slot, giving the buffer `capacity - 1` usable bytes.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer storage.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report bytes in use.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available space.

#### Constants and configuration
- `RINGBUF_OK = 0` — success.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.

#### Dependencies
- Includes `<stddef.h>` for `size_t`.
- Includes `<stdint.h>` for `uint8_t`.
- Wraps declarations in `extern "C"` when compiled as C++.

#### Exceptions / Errors
- `ringbuf_init` can report invalid capacity with `RINGBUF_EINVAL` or allocation failure with `RINGBUF_ENOMEM`.
