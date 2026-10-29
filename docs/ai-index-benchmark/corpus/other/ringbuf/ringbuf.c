// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

#include "ringbuf.h"

#include <stdlib.h>
#include <string.h>

static int is_power_of_two(size_t n)
{
    return n != 0 && (n & (n - 1)) == 0;
}

static size_t min_size(size_t a, size_t b)
{
    return a < b ? a : b;
}

ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)
{
    if (!is_power_of_two(capacity)) {
        return RINGBUF_EINVAL;
    }
    rb->data = malloc(capacity);
    if (rb->data == NULL) {
        return RINGBUF_ENOMEM;
    }
    rb->capacity = capacity;
    rb->head = 0;
    rb->tail = 0;
    return RINGBUF_OK;
}

void ringbuf_free(ringbuf_t *rb)
{
    free(rb->data);
    rb->data = NULL;
    rb->capacity = 0;
    rb->head = 0;
    rb->tail = 0;
}

size_t ringbuf_used(const ringbuf_t *rb)
{
    return (rb->head - rb->tail) & (rb->capacity - 1);
}

size_t ringbuf_free_space(const ringbuf_t *rb)
{
    return rb->capacity - 1 - ringbuf_used(rb);
}

/* Copies in at most two chunks: up to the end of the array, then from index 0. */
size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)
{
    size_t n = min_size(len, ringbuf_free_space(rb));
    size_t head = rb->head;
    size_t first = min_size(n, rb->capacity - head);

    memcpy(rb->data + head, src, first);
    memcpy(rb->data, src + first, n - first);
    rb->head = (head + n) & (rb->capacity - 1);
    return n;
}

size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)
{
    size_t n = min_size(len, ringbuf_used(rb));
    size_t tail = rb->tail;
    size_t first = min_size(n, rb->capacity - tail);

    memcpy(dst, rb->data + tail, first);
    memcpy(dst + first, rb->data, n - first);
    rb->tail = (tail + n) & (rb->capacity - 1);
    return n;
}
