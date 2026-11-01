### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:01:53Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and retrieves bytes in a bounded circular buffer, preserving FIFO order across wraparound.

#### Purpose
- Implements initialization, cleanup, capacity queries, and byte reads/writes for a ring buffer.
- Accepts only power-of-two capacities; reserves one slot to distinguish full from empty.

#### Type
- C implementation file; defines exported `ringbuf_*` functions and static helpers.
- Uses `ringbuf_t` and `ringbuf_status_t` from `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer `rb`, requested `capacity`.
- `ringbuf_write`: buffer `rb`, source bytes `src`, requested length `len`.
- `ringbuf_read`: buffer `rb`, destination bytes `dst`, requested length `len`.
- Query and cleanup functions consume `rb`.

#### Output
- Initialization returns `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`; successful initialization allocates storage and resets indices.
- Read/write return the number of bytes transferred, limited by available data or free space.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- Validates capacity with `is_power_of_two`; allocates `capacity` bytes with `malloc`.
- Computes used bytes as `(head - tail) & (capacity - 1)`; free space is capacity minus one minus used bytes.
- Writes up to available free space and reads up to used bytes.
- Splits each transfer into at most two `memcpy` calls to handle wrapping at the end of the allocation.
- Advances `head` or `tail` modulo capacity using a bitmask.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report buffered bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- `RINGBUF_EINVAL` — returned for capacity that is not a power of two; value defined elsewhere.
- `RINGBUF_ENOMEM` — returned when allocation fails; value defined elsewhere.
- `RINGBUF_OK` — returned on successful initialization; value defined elsewhere.
- No configuration or environment variables are read.

#### Dependencies
- `"ringbuf.h"` — buffer and status type declarations.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- `uint8_t`, `size_t`.

#### Exceptions / Errors
- Invalid capacity returns `RINGBUF_EINVAL`.
- Allocation failure returns `RINGBUF_ENOMEM`.
- No exceptions are used.
