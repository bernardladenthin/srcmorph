### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:34:38Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A lock‑free, power‑of‑two sized circular buffer for byte streams, exposing a minimal API for initialization, bulk read/write, and usage metrics.

#### Purpose
- Provide a single‑producer/single‑consumer ring buffer.
- Offer non‑blocking read/write with minimal overhead.

#### Responsibilities
- **Memory management** – allocate/deallocate the underlying byte array.  
- **Data movement** – copy data into/out of the buffer, handling wrap‑around.  
- **State tracking** – expose used bytes and free space via helper functions.  
- **Error handling** – validate capacity and report allocation failures.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.  
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).  
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – create buffer.  
- `ringbuf_free(ringbuf_t *rb)` – release resources.  
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – bulk write.  
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – bulk read.  
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.  
- `ringbuf_free_space(const ringbuf_t *rb)` – available capacity.

#### Data flow
1. **Initialization** – caller supplies capacity; `ringbuf_init` allocates array, sets indices to zero.  
2. **Write** – producer calls `ringbuf_write`; data copied into buffer, head advanced modulo capacity.  
3. **Read** – consumer calls `ringbuf_read`; data copied out, tail advanced modulo capacity.  
4. **Metrics** – `ringbuf_used` and `ringbuf_free_space` compute values from head/tail.  
5. **Cleanup** – `ringbuf_free` frees the array.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.  
- Internal: `ringbuf.h` defines types and constants used by `ringbuf.c`.

#### Cross-cutting
- **Power‑of‑two constraint** – all size calculations use bitwise masking for efficiency.  
- **Single‑threaded assumption** – no locks; relies on single‑producer/single‑consumer usage.  
- **Error codes** – consistent `ringbuf_status_t` return values for init failures.  
- **Minimal API surface** – only essential functions exposed, keeping the interface lightweight.
