### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:42:17Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.

#### Purpose
- Offer a reusable ring buffer API for byte‑level data streams.
- Support initialization, cleanup, read/write, and capacity queries.

#### Responsibilities
- **Buffer Management** – allocate, free, and maintain head/tail indices.
- **Data Transfer** – read/write operations that wrap around the buffer.
- **Capacity Tracking** – expose used bytes and free space metrics.
- **Error Handling** – validate capacity and report allocation failures.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` enum – `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – available space.

#### Data flow
1. **Initialization** – caller supplies a power‑of‑two `capacity`; `ringbuf_init` allocates memory and sets indices to zero.
2. **Writing** – producer calls `ringbuf_write`; data is copied into the buffer, possibly in two contiguous chunks if wrapping occurs; head index advances.
3. **Reading** – consumer calls `ringbuf_read`; data is copied out, possibly in two chunks; tail index advances.
4. **Querying** – `ringbuf_used` and `ringbuf_free_space` compute metrics via modulo arithmetic.
5. **Cleanup** – `ringbuf_free` releases the allocated memory.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` defines types and API; `ringbuf.c` implements the logic.

#### Cross-cutting
- **Power‑of‑two constraint** – all capacity checks enforce this rule.
- **Lock‑free design** – single‑producer/single‑consumer model; no mutexes.
- **Error codes** – consistent `ringbuf_status_t` return values for init failures.
- **Memory management** – explicit allocation/free; caller responsible for lifecycle.
