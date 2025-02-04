package com.badoo.reaktive.looperthread

import com.badoo.reaktive.test.waitForOrFail
import com.badoo.reaktive.utils.Lock
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.synchronized
import platform.posix.usleep
import kotlin.system.getTimeMillis
import kotlin.system.getTimeNanos
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LooperThreadTest {

    @Test
    fun executes_task() {
        val isExecuted = AtomicBoolean()
        val lock = Lock()
        val condition = lock.newCondition()
        val thread = LooperThread()
        val startTime = getTimeMillis() + 200L

        thread.schedule(Unit, startTime) {
            lock.synchronized {
                isExecuted.value = true
                condition.signal()
            }
        }

        lock.synchronized {
            condition.waitForOrFail(5_000_000_000L, isExecuted::value) // Wait for 5 secs
        }

        assertTrue(getTimeNanos() >= startTime)
    }

    @Test
    fun does_not_execute_task_after_destroy() {
        val isExecuted = AtomicBoolean()
        val lock = Lock()
        val condition = lock.newCondition()
        val thread = LooperThread()

        thread.schedule(Unit, getTimeMillis()) {
            lock.synchronized {
                isExecuted.value = true
                condition.signal()
            }
        }

        lock.synchronized {
            condition.waitForOrFail(5_000_000_000L, isExecuted::value) // Wait for 5 secs
        }

        thread.destroy()

        isExecuted.value = false
        thread.schedule(Unit, getTimeMillis()) {
            isExecuted.value = true
        }

        usleep(200_000)

        assertFalse(isExecuted.value)
    }
}