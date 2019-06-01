package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.getAndUpdate

/**
 * Thread-safe container of one [Disposable]
 */
class DisposableWrapper : Disposable {

    private val ref = AtomicReference<Holder?>(Holder(null), true)
    override val isDisposed: Boolean get() = ref.value == null

    override fun dispose() {
        setHolder(null)
    }

    /**
     * Atomically either replaces any existing [Disposable] with the specified one or disposes it if wrapper is already disposed.
     * Also disposes any replaced [Disposable].
     */
    fun set(disposable: Disposable?) {
        setHolder(Holder(disposable))
    }

    private fun setHolder(holder: Holder?) {
        ref
            .getAndUpdate { oldHolder ->
                if (oldHolder == null) {
                    holder?.dispose()
                }

                holder
            }
            ?.dispose()
    }

    private class Holder(val disposable: Disposable?)

    private companion object {
        private fun Holder.dispose() {
            disposable?.dispose()
        }
    }
}