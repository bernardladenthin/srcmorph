### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:21:28Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Implements a lock‑free, power‑of‑two sized circular buffer for byte streams, exposing a simple API for single‑producer/single‑consumer use.

#### Purpose
- Provide a fixed‑size ring buffer for byte‑level data.
- Offer initialization, read/write, and space‑usage queries.

#### Responsibilities
- **Buffer management** – allocate, free, and maintain head/tail indices.
- **Data transfer** – enqueue and dequeue bytes with wrap‑around handling.
- **Space accounting** – report used and free space, enforce capacity limits.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – create buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – available capacity.

#### Data flow
1. **Initialization** – `ringbuf_init` validates capacity (power of two, non‑zero), allocates data array, sets head/tail to 0.  
2. **Write** – `ringbuf_write` copies up to `len` bytes from source into the buffer, handling up to two contiguous chunks, updates head.  
3. **Read** – `ringbuf_read` copies up to `len` bytes from buffer to destination, handling up to two contiguous chunks, updates tail.  
4. **Query** – `ringbuf_used` and `ringbuf_free_space` compute values via modulo arithmetic on head/tail.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal linkage between `ringbuf.c` and `ringbuf.h` for type and status definitions.

#### Cross-cutting
- **Power‑of‑two enforcement** – `is_power_of_two` helper ensures capacity validity.
- **Modulo arithmetic** – used for wrap‑around in `ringbuf_used` and `ringbuf_free_space`.
- **Error handling** – `ringbuf_init` returns explicit status codes for invalid capacity or allocation failure; other functions return byte counts.
- **Single‑producer/single‑consumer** – design assumes no concurrent writers/readers beyond one of each, enabling lock‑free operation.
