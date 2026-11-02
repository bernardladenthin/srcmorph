### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:28:50Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer, allowing each to write or read available bytes without overwriting unread data.

#### Purpose
- Declares a ring buffer that stores up to `capacity - 1` bytes; capacity must be a power of two.
- Provides initialization, cleanup, partial read/write, and occupancy queries.

#### Type
- C header; declares `ringbuf_t` struct and `ringbuf_status_t` enum.
- `ringbuf_t` fields: `data` (`uint8_t *`), `capacity` (`size_t`), and volatile `head` and `tail` indices.

#### Input
- `ringbuf_init`: buffer pointer and requested `capacity`.
- `ringbuf_write`: buffer, source byte array, and requested `len`.
- `ringbuf_read`: buffer, destination byte array, and requested `len`.
- Occupancy queries: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; `ringbuf_write` and `ringbuf_read` return bytes actually copied.
- `ringbuf_used` and `ringbuf_free_space` return occupancy counts.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Reserves one slot to distinguish full from empty, leaving `capacity - 1` usable bytes.
- Assigns `head` as the next write index and `tail` as the next read index.
- Read and write operations copy up to the requested length and report the actual byte count.
- Documents a single-producer, single-consumer usage model.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report bytes stored
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable capacity

#### Constants and configuration
- `RINGBUF_H` — include guard.
- `RINGBUF_OK = 0` — success.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.
- Capacity requirement: power of two; usable capacity is one less than `capacity`.

#### Dependencies
- `<stddef.h>` — `size_t`.
- `<stdint.h>` — `uint8_t`.
- C++ linkage declarations via `extern "C"` when compiled as C++.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
