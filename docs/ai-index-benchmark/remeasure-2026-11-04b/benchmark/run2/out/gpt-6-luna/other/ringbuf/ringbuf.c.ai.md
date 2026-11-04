### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:58:59Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Stores and retrieves byte streams in a bounded circular buffer, preserving FIFO order while wrapping reads and writes across the buffer boundary.

#### Purpose
- Implements byte-oriented ring-buffer allocation, capacity tracking, writes, reads, and cleanup.
- Reserves one slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Type
- C implementation file; exports ring-buffer functions declared in `ringbuf.h`.
- Uses `ringbuf_t` and `ringbuf_status_t` from `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer `rb`, requested `capacity`.
- `ringbuf_write`: buffer `rb`, source bytes `src`, requested length `len`.
- `ringbuf_read`: buffer `rb`, destination `dst`, requested length `len`.
- `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free`: buffer pointer `rb`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; allocates `rb->data` and initializes buffer state on success.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred, limited by available space or data.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities; allocates `capacity` bytes and sets `head` and `tail` to zero.
- Tracks occupancy with `(head - tail) & (capacity - 1)`; free space is `capacity - 1 - used`.
- `ringbuf_write` copies up to available space in at most two chunks, wrapping from the array end to index zero; advances `head` with a capacity mask.
- `ringbuf_read` copies up to available data in at most two chunks, wrapping as needed; advances `tail` with a capacity mask.
- `ringbuf_free` frees storage and zeros `data`, `capacity`, `head`, and `tail`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — consume bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` — initialization status codes; values are defined outside this file.
- No configuration or environment variables.

#### Dependencies
- `"ringbuf.h"` — ring-buffer types and status codes.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.
- `size_t`, `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
