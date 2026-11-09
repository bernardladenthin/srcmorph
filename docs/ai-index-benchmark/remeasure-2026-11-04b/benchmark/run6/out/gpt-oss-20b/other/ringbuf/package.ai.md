### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:58:12Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A lock‑free, power‑of‑two sized byte ring buffer for single‑producer/single‑consumer scenarios.

#### Purpose
- Provide a fixed‑size circular buffer for byte streams.
- Expose a clean API for initialization, deallocation, read/write, and space queries.

#### Responsibilities
- **Buffer management** – allocate, free, and maintain head/tail indices.
- **Data transfer** – copy data to/from the internal array with wrap‑around.
- **Space accounting** – compute used bytes and available free space.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` enum – `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – bytes available for writing.

#### Data flow
1. **Initialization**: `ringbuf_init` allocates a power‑of‑two buffer, sets `head = tail = 0`.
2. **Writing**: `ringbuf_write` copies up to `free_space` bytes, updates `head` modulo capacity.
3. **Reading**: `ringbuf_read` copies up to `used` bytes, updates `tail` modulo capacity.
4. **Querying**: `ringbuf_used` and `ringbuf_free_space` compute values via modulo arithmetic.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` declares types and API used by `ringbuf.c`.

#### Cross-cutting
- **Power‑of‑two constraint**: capacity must be a power of two; one slot left unused to distinguish full vs empty.
- **Modulo arithmetic**: head and tail wrap using `& (capacity - 1)` for efficiency.
- **Error handling**: `ringbuf_init` returns `RINGBUF_EINVAL` for invalid capacity, `RINGBUF_ENOMEM` for allocation failure. All other functions return the actual number of bytes processed.
