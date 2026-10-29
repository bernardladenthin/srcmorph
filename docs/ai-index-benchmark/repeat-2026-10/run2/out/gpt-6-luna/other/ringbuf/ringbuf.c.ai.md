### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:50:57Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers and retrieves byte streams in FIFO order using a fixed-capacity circular buffer.

#### Purpose
- Implements initialization, cleanup, usage tracking, and byte reads/writes for `ringbuf_t`.
- Leaves one slot unused to distinguish full from empty.

#### Type
- C implementation file; exports the `ringbuf_*` functions declared in `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source bytes, and requested length.
- `ringbuf_read`: buffer, destination bytes, and requested length.
- Read/write operations consume the buffer’s `head`, `tail`, and `capacity` state.

#### Output
- Initialization returns a `ringbuf_status_t`; reads and writes return the number of bytes transferred.
- `ringbuf_write` copies bytes into allocated storage; `ringbuf_read` copies bytes out.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `is_power_of_two` validates capacity; `ringbuf_init` rejects other capacities with `RINGBUF_EINVAL`, allocates storage, and initializes indices to zero.
- Allocation failure returns `RINGBUF_ENOMEM`; successful initialization returns `RINGBUF_OK`.
- `ringbuf_used` computes occupancy with `(head - tail) & (capacity - 1)`; `ringbuf_free_space` reserves one slot.
- `ringbuf_write` transfers up to available space; `ringbuf_read` transfers up to used bytes.
- Both operations copy in up to two chunks to handle wraparound, then advance the corresponding index using the capacity mask.
- `ringbuf_free` frees `data` and clears `data`, `capacity`, `head`, and `tail`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — retrieve bytes

#### Dependencies
- `ringbuf.h` — buffer and status declarations
- `<stdlib.h>` — `malloc`, `free`
- `<string.h>` — `memcpy`

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
