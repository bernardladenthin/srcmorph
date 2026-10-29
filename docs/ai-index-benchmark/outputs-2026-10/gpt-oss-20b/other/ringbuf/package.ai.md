### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:53:59Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Implements a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer single‑consumer data transfer.

#### Purpose
- Provide a reusable, memory‑efficient ring buffer for byte streams.
- Offer a clear API for initialization, cleanup, read/write, and space queries.

#### Responsibilities
- **Buffer management** – allocate, free, and maintain head/tail indices.
- **Data transfer** – copy data into/out of the buffer with wrap‑around handling.
- **Capacity validation** – enforce power‑of‑two sizes and non‑zero capacity.
- **Metrics** – expose used bytes and free space for flow control.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – current byte count.
- `ringbuf_free_space(const ringbuf_t *rb)` – remaining capacity.

#### Data flow
1. **Initialization** – caller supplies a power‑of‑two capacity; `ringbuf_init` allocates memory and zeroes indices.  
2. **Write** – producer calls `ringbuf_write`; data is copied in up to two contiguous chunks to handle wrap‑around; head index advances atomically.  
3. **Read** – consumer calls `ringbuf_read`; data is copied similarly; tail index advances atomically.  
4. **Metrics** – `ringbuf_used` and `ringbuf_free_space` compute values via modulo arithmetic on head/tail.  
5. **Cleanup** – `ringbuf_free` releases the buffer.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- No external libraries; purely self‑contained C99 code.

#### Cross-cutting
- **Power‑of‑two enforcement** – `is_power_of_two` helper validates capacity.  
- **Wrap‑around logic** – both read and write handle two contiguous copy operations.  
- **Error handling** – initialization returns specific status codes (`EINVAL`, `ENOMEM`).  
- **Single‑producer single‑consumer** – design assumes no concurrent writes or reads beyond one producer and one consumer.
