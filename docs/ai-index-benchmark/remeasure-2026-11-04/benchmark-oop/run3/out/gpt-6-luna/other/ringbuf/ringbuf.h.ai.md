### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:52:50Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer.

#### Purpose
- Declares a ring buffer API for writing and reading bytes without exceeding available capacity.
- Uses one reserved slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Type
- C header; declares `ringbuf_t` and `ringbuf_status_t`, with C++ linkage compatibility.
- `ringbuf_t`: `data` byte pointer, `capacity`, producer-owned `head`, and consumer-owned `tail`; indices are `volatile size_t`.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity; capacity must be a nonzero power of two.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; `ringbuf_write` and `ringbuf_read` return bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Declares fixed-capacity storage with head and tail indices for producer and consumer access.
- Write and read operations copy up to the requested length, limited by available space or data.
- Requires power-of-two capacity and reserves one slot, yielding `capacity - 1` usable bytes.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.

#### Constants and configuration
- `RINGBUF_OK = 0` — successful status.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.
- Capacity requirement: nonzero power of two; usable size is capacity minus one.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
