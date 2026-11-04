### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:22:18Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Stores and retrieves byte streams in a fixed-capacity circular buffer, preserving FIFO order across wraparound.

#### Purpose
- Implements ring-buffer initialization, cleanup, occupancy queries, and bounded byte reads and writes.
- Reserves one byte to distinguish full from empty.

#### Type
- C implementation file; exports `ringbuf_*` functions declared via `ringbuf.h`; internal helpers are `static`.

#### Input
- `ringbuf_init`: ring-buffer pointer `rb`, requested `capacity`.
- `ringbuf_write`: buffer `rb`, source bytes `src`, requested length `len`.
- `ringbuf_read`: buffer `rb`, destination `dst`, requested length `len`.
- Occupancy queries and `ringbuf_free`: ring-buffer pointer `rb`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; reads, writes, and occupancy queries return `size_t`.
- Writes bytes into or reads bytes from `rb->data`; updates `head` and `tail`.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities, allocates `capacity` bytes, and initializes `head` and `tail` to zero.
- `ringbuf_used` computes occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` reports `capacity - 1 - used`.
- `ringbuf_write` copies up to available space in at most two chunks, wrapping at the array end; advances `head` with a capacity mask.
- `ringbuf_read` copies up to used bytes in at most two chunks, wrapping at the array end; advances `tail` with a capacity mask.
- `ringbuf_free` frees storage and zeros the pointer, capacity, and indices.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(rb) -> void` — release and reset buffer.
- `ringbuf_used(rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(rb) -> size_t` — report writable bytes.
- `ringbuf_write(rb, src, len) -> size_t` — append available bytes.
- `ringbuf_read(rb, dst, len) -> size_t` — retrieve available bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- `RINGBUF_EINVAL` — returned for invalid capacity.
- `RINGBUF_ENOMEM` — returned if allocation fails.
- `RINGBUF_OK` — returned on successful initialization.

#### Dependencies
- `"ringbuf.h"` — ring-buffer types and status codes.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- `uint8_t`, `size_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` when capacity is zero or not a power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` when `malloc` fails.
- No other parameter validation is performed.
