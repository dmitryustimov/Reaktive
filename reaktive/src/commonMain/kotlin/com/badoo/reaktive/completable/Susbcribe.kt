package com.badoo.reaktive.completable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.handleSourceError

@UseReturnValue
fun Completable.subscribe(
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
): Disposable {
    val disposableWrapper = DisposableWrapper()
    onSubscribe?.invoke(disposableWrapper)

    subscribeSafe(
        object : CompletableObserver {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onComplete() {
                try {
                    onComplete?.invoke()
                } finally {
                    disposableWrapper.dispose()
                }
            }

            override fun onError(error: Throwable) {
                try {
                    handleSourceError(error, onError)
                } finally {
                    disposableWrapper.dispose()
                }
            }
        }
    )

    return disposableWrapper
}