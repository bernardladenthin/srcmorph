### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:36:46Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and retrieves byte streams in a bounded circular buffer while preserving FIFO order.

#### Purpose
- Implements byte-oriented ring buffer initialization, cleanup, capacity tracking, writes, and reads.
- Uses one reserved slot to distinguish full from empty.

#### Type
- C implementation module; declares public functions through `ringbuf.h`.

#### Input
- `ringbuf_init`: ring buffer pointer and power-of-two capacity.
- `ringbuf_write`: buffer, source bytes, and requested length.
- `ringbuf_read`: buffer, destination bytes, and requested length.
- Buffer state: `data`, `capacity`, `head`, and `tail`.

#### Output
- Initialization status; read/write return the number of bytes processed.
- Updates buffer allocation and indices; `ringbuf_free` releases storage and resets state.

#### Core logic
- `is_power_of_two` validates capacities; `min_size` bounds requested operations.
- `ringbuf_init` allocates storage and initializes indices, returning `RINGBUF_EINVAL` for invalid capacity or `RINGBUF_ENOMEM` on allocation failure.
- `ringbuf_used` computes occupancy using masked index difference; `ringbuf_free_space` subtracts occupancy and the reserved slot from capacity.
- `ringbuf_write` copies up to available space in at most two chunks, wraps `head` with a capacity mask, and returns bytes written.
- `ringbuf_read` copies up to available data in at most two chunks, wraps `tail` with a capacity mask, and returns bytes read.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(rb) -> void` — release and reset buffer
- `ringbuf_used(rb) -> size_t` — report stored bytes
- `ringbuf_free_space(rb) -> size_t` — report writable bytes
- `ringbuf_write(rb, src, len) -> size_t` — append bytes
- `ringbuf_read(rb, dst, len) -> size_t` — retrieve bytes

#### Dependencies
- `ringbuf.h` — buffer types, status codes, and declarations
- `<stdlib.h>` — `malloc`, `free`
- `<string.h>` — `memcpy`

#### Exceptions / Errors
- Invalid non-power-of-two or zero capacity returns `RINGBUF_EINVAL`.
- Allocation failure returns `RINGBUF_ENOMEM`.
- Reads and writes process only the available bytes; no error status is returned.
