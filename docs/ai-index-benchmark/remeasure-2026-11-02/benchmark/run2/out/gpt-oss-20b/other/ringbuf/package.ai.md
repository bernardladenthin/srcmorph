### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:30Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling non‑blocking single‑producer/single‑consumer data transfer.

#### Purpose
- Offer a fixed‑size ring buffer with efficient memory usage.
- Expose a clean API for initialization, read/write, and resource cleanup.

#### Responsibilities
- **Memory Management** – allocate and free buffer storage.
- **Data Movement** – copy data into/out of the circular buffer with wrap‑around handling.
- **State Querying** – report used bytes and free space.
- **Error Handling** – validate capacity and report allocation failures.

#### Key units
- `ringbuf_t` – opaque struct holding buffer pointer, capacity, head, and tail indices.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) for init status.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – create buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – available capacity.

#### Data flow
1. **Initialization** – caller supplies power‑of‑two capacity; `ringbuf_init` allocates memory and sets indices to zero.
2. **Write** – producer calls `ringbuf_write`; data is copied into the buffer up to available space, head index advanced modulo capacity.
3. **Read** – consumer calls `ringbuf_read`; data is copied out, tail index advanced modulo capacity.
4. **Query** – `ringbuf_used` and `ringbuf_free_space` compute metrics using `(head - tail) & (capacity - 1)`.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- No external libraries; purely self‑contained.

#### Cross-cutting
- **Power‑of‑two invariant** – all size calculations rely on modulo arithmetic; `is_power_of_two` guard in `ringbuf_init`.
- **Lock‑free design** – single‑producer/single‑consumer guarantees no synchronization primitives.
- **Error codes** – consistent `ringbuf_status_t` values for init failures; write/read return byte counts.
- **Memory safety** – bounds checks prevent overruns; `ringbuf_free` resets fields to avoid dangling pointers.
