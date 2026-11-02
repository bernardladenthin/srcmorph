### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:59:13Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Buffers and retrieves byte streams in FIFO order while reusing a fixed-size circular storage area.

#### Purpose
- Implements ring-buffer initialization, cleanup, byte writes, byte reads, and capacity queries.
- Reserves one slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Type
- C implementation file defining functions for `ringbuf_t` and `ringbuf_status_t`, declared in `ringbuf.h`.

#### Input
- `ringbuf_init`: ring-buffer pointer `rb` and requested `capacity`.
- `ringbuf_write`: buffer `rb`, source bytes `src`, and requested length `len`.
- `ringbuf_read`: buffer `rb`, destination `dst`, and requested length `len`.
- `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free`: ring-buffer pointer `rb`.

#### Output
- `ringbuf_init` returns `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred, capped by available space or stored data.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_init` allocates storage and initializes buffer state; `ringbuf_free` releases storage and resets fields.

#### Core logic
- `is_power_of_two` accepts nonzero capacities with exactly one bit set; initialization rejects other capacities.
- `ringbuf_init` allocates `capacity` bytes, then sets `capacity`, `head`, and `tail`; allocation failure returns `RINGBUF_ENOMEM`.
- `ringbuf_used` computes wrapped distance as `(head - tail) & (capacity - 1)`; free space is `capacity - 1 - used`.
- `ringbuf_write` copies up to free space, splitting the copy at the array boundary when needed, then advances `head` with a capacity mask.
- `ringbuf_read` copies up to the stored byte count, splitting at the array boundary when needed, then advances `tail` with a capacity mask.
- `ringbuf_free` frees the data allocation and resets `data`, `capacity`, `head`, and `tail`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report stored bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append available bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — retrieve available bytes
- Internal helpers: `is_power_of_two`, `min_size`

#### Constants and configuration
- `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM` — status codes returned by initialization; values are defined outside this file.
- Capacity must be a nonzero power of two; one allocated slot is reserved.

#### Dependencies
- `"ringbuf.h"` — ring-buffer types and status codes.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` when `malloc` fails.
- No null-pointer checks are performed.
