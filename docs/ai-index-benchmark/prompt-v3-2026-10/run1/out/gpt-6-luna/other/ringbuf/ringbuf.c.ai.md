### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:50:35Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and retrieves byte streams through a fixed-capacity circular buffer, preserving FIFO order across wraparound.

#### Purpose
- Implements initialization, cleanup, capacity queries, and byte reads/writes for a ring buffer.
- Reserves one slot to distinguish full from empty.

#### Type
- C implementation file; defines exported `ringbuf_*` functions and static helpers.
- Uses `ringbuf_t` and `ringbuf_status_t` from `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer `rb`, requested byte `capacity`.
- `ringbuf_write`: buffer `rb`, source bytes `src`, requested length `len`.
- `ringbuf_read`: buffer `rb`, destination `dst`, requested length `len`.
- `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free`: buffer pointer `rb`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; allocates `capacity` bytes and resets indices on success.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred, capped by available space or data.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- Requires capacity to be nonzero and a power of two; uses `capacity - 1` as a wraparound mask.
- Tracks positions with `head` (write position) and `tail` (read position); used bytes are `(head - tail) & (capacity - 1)`.
- Computes free space as `capacity - 1 - used`, leaving one slot unused.
- Reads and writes in up to two `memcpy` chunks: from the current index to the array end, then from index zero.
- Advances indices modulo capacity using the power-of-two mask.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append available bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — retrieve available bytes.
- Internal static helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- `RINGBUF_EINVAL` — returned when capacity is not a nonzero power of two.
- `RINGBUF_ENOMEM` — returned when allocation fails.
- `RINGBUF_OK` — returned on successful initialization.
- No configuration or environment variables.

#### Dependencies
- `"ringbuf.h"` — buffer and status types, status values, API declarations.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- `uint8_t`, `size_t`.

#### Exceptions / Errors
- Initialization returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- Initialization returns `RINGBUF_ENOMEM` if `malloc` fails.
