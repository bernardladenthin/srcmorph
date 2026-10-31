### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:55:29Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.

#### Purpose
- Offer a reusable, memory‑efficient ring buffer API in C.
- Support initialization, cleanup, bulk read/write, and space queries.

#### Responsibilities
- **Buffer management** – allocate, free, and maintain ring buffer state.  
- **Data transfer** – read/write operations that wrap around the buffer.  
- **Space accounting** – expose used and free space metrics.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.  
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).  
- `ringbuf_init()` – allocates and validates buffer.  
- `ringbuf_free()` – releases memory.  
- `ringbuf_write()` – copies up to `len` bytes, updates head.  
- `ringbuf_read()` – copies up to `len` bytes, updates tail.  
- `ringbuf_used()` – returns current byte count.  
- `ringbuf_free_space()` – returns available capacity.

#### Data flow
1. **Initialization**: `ringbuf_init(rb, capacity)` validates capacity (power of two), allocates memory, sets head/tail to 0.  
2. **Writing**: Producer calls `ringbuf_write(rb, src, len)`; data is copied in one or two chunks to handle wrap‑around; head index updated.  
3. **Reading**: Consumer calls `ringbuf_read(rb, dst, len)`; data is copied similarly; tail index updated.  
4. **Querying**: `ringbuf_used()` and `ringbuf_free_space()` compute values via modulo arithmetic on head/tail.  
5. **Cleanup**: `ringbuf_free(rb)` frees buffer and resets fields.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.  
- No external libraries; purely self‑contained.

#### Cross‑cutting
- **Power‑of‑two constraint**: All capacity checks enforce this rule; one slot is intentionally left unused to distinguish full/empty states.  
- **Modulo arithmetic**: Head and tail indices wrap using `& (capacity - 1)`.  
- **Error handling**: Functions return status codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) instead of exceptions.  
- **Thread safety**: Designed for single‑producer/single‑consumer scenarios without locks.
