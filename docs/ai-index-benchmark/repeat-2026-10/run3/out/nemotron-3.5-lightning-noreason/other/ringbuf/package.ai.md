### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:45Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A lock-free single-producer/single-consumer ring buffer with power-of-two capacity for deterministic byte-stream throughput.

#### Purpose
Provides a fixed-capacity, power-of-two ring buffer that guarantees lock-free single-producer and single-consumer access for deterministic byte-stream throughput.

#### Responsibilities
- **Buffer management**: Allocate and maintain a fixed-size power-of-two capacity buffer with one slot reserved as an empty flag.
- **Pointer arithmetic**: Manage volatile head/tail indices for producer and consumer using bitmask wrapping.
- **Data movement**: Execute write and read operations that copy data in wrapped chunks when the buffer boundary is crossed.
- **State querying**: Expose occupancy and free-space calculations using bitmasked subtraction.

#### Key units
- `ringbuf_t`: struct containing volatile head/tail indices and a dynamically allocated uint8_t data buffer sized to power-of-two capacity.
- `ringbuf_status_t`: enum with values `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM` returned by initialization and error paths.
- `ringbuf_init(rb, capacity)`: validates power-of-two constraint, allocates the data buffer, resets head and tail to zero.
- `ringbuf_free(rb)`: releases the allocated data buffer and resets internal state.
- `ringbuf_write(rb, src, len)`: copies up to `len` bytes from `src` into the buffer, advancing the head pointer via bitmasked wrap-around; returns actual bytes written.
- `ringbuf_read(rb, dst, len)`: copies up to `len` bytes from the buffer into `dst`, advancing the tail pointer via bitmasked wrap-around; returns actual bytes read.
- `ringbuf_used(rb)`: computes `(head - tail) & (capacity - 1)` to report current occupancy.
- `ringbuf_free_space(rb)`: computes `(capacity - 1) - used` to report available capacity for new writes.

#### Data flow
The producer calls `ringbuf_write` to copy source data into the buffer, which may span two memory chunks at the end and wrapped start of the allocation, then advances the volatile head index. The consumer calls `ringbuf_read` to copy data out of the buffer, which similarly may span two chunks at the tail, then advances the volatile tail index. The difference between head and tail, masked by `capacity - 1`, always reports current occupancy, while the complementary calculation reports free space.

#### Dependencies
- `ringbuf.h`: defines the `ringbuf_t` struct layout, `ringbuf_status_t` enum, and all public function signatures.
- `<stddef.h>`: provides `size_t` used for capacity and byte-count parameters.
- `<stdint.h>`: provides `uint8_t` for the data element type.
- `<stdlib.h>`: used internally by `ringbuf_init` for `malloc` and by `ringbuf_free` for `free`.
- `<string.h>`: used internally by `ringbuf_write` and `ringbuf_read` for `memcpy` operations.
- C11 or later: required for `volatile` keyword semantics on the head and tail index fields.

#### Cross-cutting
- **Power-of-two constraint**: every capacity check, bitmask operation (`& (capacity - 1)`), and usable-byte calculation (`capacity - 1`) stems from the power-of-two requirement; zero or non-power-of-two capacities are rejected via `RINGBUF_EINVAL`.
- **Single-producer/single-consumer model**: the use of `volatile` on head and tail indices is sufficient for serialization without mutexes; the design assumes one writer and one reader.
- **One-slot-empty reservation**: the usable capacity is always `capacity - 1`, with the last slot permanently reserved to distinguish between full and empty states.
- **Error handling**: initialization failures return `RINGBUF_EINVAL` for invalid capacity or `RINGBUF_ENOMEM` for allocation failure; write and read operations return the actual byte count copied, which may be less than the requested `len` if space or data is unavailable.
