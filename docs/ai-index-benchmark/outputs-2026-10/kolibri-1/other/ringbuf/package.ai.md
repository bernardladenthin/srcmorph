### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:40:25Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A fixed-capacity, single-producer single-consumer byte ring buffer for lock-free byte streaming between producer and consumer

#### Purpose
- Provides bounded byte-level buffering between a single producer and a single consumer
- Enables efficient streaming of byte data without blocking

#### Responsibilities
- Manages a fixed-size circular buffer with power-of-two capacity
- Handles byte-level write and read operations with two-chunk copying
- Validates buffer capacity and allocation during initialization

#### Key units
- ringbuf_t struct with data, capacity, head, tail fields
- ringbuf_status_t enum with RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM
- ringbuf_init, ringbuf_free, ringbuf_write, ringbuf_read, ringbuf_used, ringbuf_free_space functions

#### Data flow
- Producer writes bytes from source pointer into buffer at head position
- Consumer reads bytes from buffer at tail position into destination pointer
- Head and tail indices advance using bitwise masking with capacity minus one

#### Dependencies
- stdlib.h, string.h, stddef.h, stdint.h

#### Cross-cutting
- Power-of-two capacity validation using bitmask checks
- One slot reserved so usable capacity is capacity minus one
- Two-chunk copy logic for reads and writes spanning buffer boundaries
- Single-producer single-consumer ownership model for head and tail indices
