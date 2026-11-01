### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:38:36Z
- G: 
- A: 0.0.0
- X: file
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer.

#### Purpose
- Declares an SPSC ring buffer that copies bytes in and out without exceeding available capacity.
- Reserves one slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Type
- C header: `ringbuf.h`; declarations have C linkage when included from C++.
- `ringbuf_t`: struct holding the byte buffer, capacity, and producer/consumer indices.
- `ringbuf_status_t`: initialization status enum.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used`, `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns `ringbuf_status_t`; read/write return the number of bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_init` and `ringbuf_free` are declared to update or release ring-buffer state; implementation is not present.

#### Core logic
- Requires capacity to be a nonzero power of two.
- Uses `head` as the next producer write index and `tail` as the next consumer read index.
- Leaves one slot unused, giving a maximum stored size of `capacity - 1` bytes.
- Read and write operations copy up to the requested length and report the amount actually copied.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report stored bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_H`: include guard.
- `RINGBUF_OK = 0`: successful status.
- `RINGBUF_EINVAL = -1`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2`: allocation failed.
- Capacity requirement: power of two; one slot is reserved.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` declares `RINGBUF_EINVAL` for zero or non-power-of-two capacity and `RINGBUF_ENOMEM` for allocation failure.
