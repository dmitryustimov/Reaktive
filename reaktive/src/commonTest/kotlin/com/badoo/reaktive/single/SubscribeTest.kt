package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.assertSuccess
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscribeTest {

    private val upstream = TestSingle<Int?>()
    private val observer = TestSingleObserver<Int?>()

    @Test
    fun returned_disposable_is_not_disposed() {
        assertFalse(upstream.subscribe().isDisposed)
    }

    @Test
    fun disposes_upstream_WHEN_disposed() {
        upstream.subscribe().dispose()

        assertTrue(upstream.isDisposed)
    }

    @Test
    fun calls_onSubscribe() {
        upstream.subscribe(onSubscribe = observer::onSubscribe)

        observer.assertSubscribed()
    }

    @Test
    fun calls_onSuccess_WHEN_upstream_succeeded_with_non_null_value() {
        observer.onSubscribe(disposable())
        upstream.subscribe(onSuccess = observer::onSuccess)

        upstream.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun calls_onSuccess_WHEN_upstream_succeeded_with_null_value() {
        observer.onSubscribe(disposable())
        upstream.subscribe(onSuccess = observer::onSuccess)

        upstream.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun disposes_disposable_WHEN_upstream_is_succeeded() {
        val disposable = upstream.subscribe()

        upstream.onSuccess(0)

        assertTrue(disposable.isDisposed)
    }


    @Test
    fun calls_onError_WHEN_upstream_produced_an_error() {
        observer.onSubscribe(disposable())
        upstream.subscribe(onError = observer::onError)
        val error = Throwable()

        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun disposes_disposable_WHEN_upstream_produced_an_error() {
        val disposable = upstream.subscribe(onError = {})

        upstream.onError(Throwable())

        assertTrue(disposable.isDisposed)
    }
}