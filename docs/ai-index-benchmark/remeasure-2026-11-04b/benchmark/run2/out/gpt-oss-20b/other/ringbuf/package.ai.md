### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:03:28Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, fixed‑size ring buffer for byte stream handling in C.

#### Purpose
- Implements a single‑producer/single‑consumer FIFO buffer.
- Enables efficient wrap‑around using power‑of‑two capacity and bit masking.

#### Responsibilities
- **Buffer management**: allocate, free, and maintain head/tail indices.
- **Data transfer**: write to and read from the circular buffer with wrap‑around support.
- **Space queries**: report used bytes and available free space.
- **Validation**: enforce power‑of‑two capacity and handle allocation errors.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `ringbuf_free(ringbuf_t *rb)` – deallocate resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – bytes available for writing.

#### Data flow
1. **Initialization** – `ringbuf_init` validates capacity, allocates memory, sets indices to zero.
2. **Writing** – `ringbuf_write` copies up to `len` bytes, handling wrap‑around, updates head.
3. **Reading** – `ringbuf_read` copies up to `len` bytes, handling wrap‑around, updates tail.
4. **Status** – `ringbuf_used` and `ringbuf_free_space` compute values via modulo arithmetic.
5. **Cleanup** – `ringbuf_free` releases allocated memory.

#### Dependencies
- Internal: `ringbuf.h` (declarations), `ringbuf.c` (implementation).
- Standard library: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.

#### Cross-cutting
- **Power‑of‑two enforcement**: `is_power_of_two` helper ensures capacity validity.
- **Bit‑masking**: `(head - tail) & (capacity - 1)` for wrap‑around calculations.
- **Error handling**: consistent return codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- **Single‑producer/single‑consumer assumption**: no locking required, relies on atomic index updates.
