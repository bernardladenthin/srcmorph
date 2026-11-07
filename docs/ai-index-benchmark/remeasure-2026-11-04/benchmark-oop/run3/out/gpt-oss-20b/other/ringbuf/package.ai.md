### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:56:44Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A lock‑free, power‑of‑two ring buffer for single‑producer/single‑consumer byte streams.

#### Purpose
- Provide a circular FIFO buffer that avoids dynamic reallocation.
- Enable efficient, contiguous read/write with minimal copying.

#### Responsibilities
- **Buffer lifecycle** – initialization, allocation, and cleanup.
- **Data movement** – write to head, read from tail, handling wrap‑around.
- **Space accounting** – expose used bytes and free space.
- **Error handling** – validate capacity and report allocation failures.

#### Key units
- **`ringbuf_t`** – struct holding `data`, `capacity`, `head`, `tail`.
- **`ringbuf_status_t`** – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- **`ringbuf_init`** – allocates buffer, validates power‑of‑two capacity.
- **`ringbuf_free`** – releases allocated memory.
- **`ringbuf_write`** – copies up to `len` bytes, updates `head`.
- **`ringbuf_read`** – copies up to `len` bytes, updates `tail`.
- **`ringbuf_used`** – returns `(head - tail) & (capacity - 1)`.
- **`ringbuf_free_space`** – returns `capacity - 1 - ringbuf_used`.

#### Data flow
1. **Init** → allocate `capacity` bytes, set indices to 0.  
2. **Write** → copy from source to buffer, wrap at `capacity`, advance `head`.  
3. **Read** → copy from buffer to destination, wrap at `capacity`, advance `tail`.  
4. **Query** → `ringbuf_used`/`ringbuf_free_space` compute counts via modulo arithmetic.  
5. **Free** → deallocate buffer memory.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Uses `malloc`, `free`, `memcpy` for memory management and copying.

#### Cross-cutting
- **Power‑of‑two constraint** – all operations rely on bitwise modulo via `capacity - 1`.  
- **Single‑producer/single‑consumer** – no locking; indices are updated atomically by one thread each.  
- **Error codes** – consistent `ringbuf_status_t` for init failures; write/read return actual byte counts.
