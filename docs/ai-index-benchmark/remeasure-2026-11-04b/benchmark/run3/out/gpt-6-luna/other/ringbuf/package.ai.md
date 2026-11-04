### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:08:05Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded byte-stream buffering for single-producer/single-consumer transfer, accepting and returning data as space and queued bytes allow.

#### Purpose
- Implements and declares a fixed-capacity circular byte buffer with partial reads and writes.
- Reserves one slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Responsibilities
- **Buffer lifecycle and status:** initialize storage, validate capacity, and free/reset buffer state.
- **Occupancy and transfer:** report used/free space and copy bytes into or out of the circular queue, including wraparound.

#### Key units
- `ringbuf_t` — stores the byte array, capacity, and volatile `head` and `tail` indices.
- `ringbuf_status_t` — reports initialization status via `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free` — allocate and initialize the buffer, or release and reset it.
- `ringbuf_used`, `ringbuf_free_space` — report queued bytes and writable capacity.
- `ringbuf_write`, `ringbuf_read` — enqueue or dequeue up to the requested number of bytes.
- `is_power_of_two`, `min_size` — internal validation and length-selection helpers.

#### Data flow
- `ringbuf_init` accepts a nonzero power-of-two capacity and allocates storage; invalid capacity or allocation failure returns an error status.
- `ringbuf_write` copies up to available space from the source, while `ringbuf_read` copies up to the queued amount to the destination; both handle wraparound and return bytes transferred.
- Occupancy queries derive used bytes and available space from the circular indices; `ringbuf_free` releases storage and resets the fields.

#### Dependencies
- `ringbuf.h` declares the API, structure, and status codes used by `ringbuf.c`.
- Standard C headers provide `size_t`, `uint8_t`, allocation, and copying facilities; the header supports C++ inclusion via `extern "C"`.

#### Cross-cutting
- Capacity must be a nonzero power of two; index advancement uses a capacity mask.
- `head` is producer-owned and `tail` consumer-owned for single-producer/single-consumer use; no broader concurrency guarantee is stated.
