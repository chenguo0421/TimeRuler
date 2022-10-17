package com.siwencat.swui.manager.cache

import java.util.LinkedHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * @Description TODO
 * @Creator ChenGuo
 * @Email wushengyuan1hao@163.com
 * @Date 10-14-2022 周五 13:51
 */
class LRUCache1<K, V> private constructor(
    private val maxCapacity: Int,
    initialCapacity: Int,
    loadFactor: Float,
    accessOrder: Boolean
) : LinkedHashMap<K?, V?>(initialCapacity, loadFactor, accessOrder) {
    private val lock: Lock = ReentrantLock()

    constructor(maxCapacity: Int) : this(
        maxCapacity,
        INITIAL_CAPACITY,
        DEFAULT_LOAD_FACTOR,
        true
    ) {
    }

    override operator fun get(key: K?): V? {
        lock.lock()
        return try {
            super.get(key)
        } finally {
            lock.unlock()
        }
    }

    override fun put(key: K?, value: V?): V? {
        lock.lock()
        return try {
            super.put(key, value)
        } finally {
            lock.unlock()
        }
    }

    override fun remove(key: K?): V? {
        lock.lock()
        return try {
            super.remove(key)
        } finally {
            lock.unlock()
        }
    }

    override fun removeEldestEntry(eldest: Map.Entry<K?, V?>): Boolean {
        return size > maxCapacity
    }

    companion object {
        private const val serialVersionUID = -4407809689385629881L
        private const val DEFAULT_LOAD_FACTOR = 0.75f
        private const val INITIAL_CAPACITY = 16
    }
}