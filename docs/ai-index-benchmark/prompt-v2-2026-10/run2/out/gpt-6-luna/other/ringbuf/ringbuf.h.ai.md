### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:46:52Z
- G: 
- A: 0.0.0
- X: file
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer.

#### Purpose
- Declares an SPSC ring buffer that copies bytes into and out of bounded storage.
- Leaves one slot unused, so usable capacity is `capacity - 1` bytes.

#### Type
- C header; declares `ringbuf_t` and `ringbuf_status_t`.
- `ringbuf_t` contains a data pointer, capacity, and volatile producer-owned `head` and consumer-owned `tail` indices.

#### Input
- `ringbuf_init`: buffer pointer and capacity.
- `ringbuf_write`: buffer, source bytes, and requested length.
- `ringbuf_read`: buffer, destination bytes, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; `ringbuf_write` and `ringbuf_read` return bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases the buffer’s resources.

#### Core logic
- Requires capacity to be a nonzero power of two.
- Ring-buffer storage is represented by `data`, `capacity`, `head`, and `tail`.
- Read and write operations copy up to the requested length and report the actual byte count.
- One slot remains unused to distinguish full from empty.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes in
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes

#### Constants and configuration
- `RINGBUF_H`: include-guard macro.
- `RINGBUF_OK = 0`: successful status.
- `RINGBUF_EINVAL = -1`: zero or non-power-of-two capacity.
- `RINGBUF_ENOMEM = -2`: allocation failed.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.
- C++ linkage guards via `__cplusplus` and `extern "C"`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
