### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:45:10Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Provides bounded byte buffering for producers and consumers, preserving FIFO order across wraparound.

#### Purpose
- Implements a dynamically allocated byte ring buffer with capacity limited to a power of two.
- Supports partial writes and reads when available space or buffered data is less than requested.

#### Type
- C implementation file; exports ring-buffer operations declared in `ringbuf.h`.
- Uses `ringbuf_t` and `ringbuf_status_t`.

#### Input
- `ringbuf_init`: ring-buffer pointer `rb` and requested `capacity`.
- `ringbuf_write`: buffer `rb`, source bytes `src`, requested length `len`.
- `ringbuf_read`: buffer `rb`, destination `dst`, requested length `len`.
- `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free`: buffer pointer `rb`.

#### Output
- `ringbuf_init` returns `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, or `RINGBUF_OK`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases `rb->data` and resets the buffer fields.

#### Core logic
- `is_power_of_two` accepts nonzero capacities with exactly one set bit; initialization rejects other capacities.
- `ringbuf_init` allocates `capacity` bytes and initializes `head` and `tail` to zero.
- `ringbuf_used` computes occupancy using masked head-tail distance; `ringbuf_free_space` reserves one slot to distinguish full from empty.
- `ringbuf_write` copies up to the available space in at most two chunks, wrapping from the array end to index zero, then advances `head` with a capacity mask.
- `ringbuf_read` copies up to the used byte count in at most two chunks, wrapping as needed, then advances `tail` with a capacity mask.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report buffered bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue bytes.
- Internal helpers: `is_power_of_two(size_t n)`, `min_size(size_t a, size_t b)`.

#### Constants and configuration
- `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` — initialization status codes referenced from `ringbuf.h`; values not specified here.
- Capacity must be a nonzero power of two; one slot is reserved, so usable capacity is `capacity - 1`.

#### Dependencies
- `"ringbuf.h"` — ring-buffer types and status codes.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.
- `uint8_t`, `size_t`.

#### Exceptions / Errors
- Invalid capacity returns `RINGBUF_EINVAL`.
- Allocation failure returns `RINGBUF_ENOMEM`.
- No null-pointer checks are performed.
