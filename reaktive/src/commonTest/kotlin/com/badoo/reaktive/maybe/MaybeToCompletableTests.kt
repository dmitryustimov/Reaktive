package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.maybe.TestMaybe
import kotlin.test.Test
import kotlin.test.assertTrue

interface MaybeToCompletableTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun completes_WHEN_upstream_is_completed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun disposes_upstream_WHEN_disposed()

    companion object {
        operator fun invoke(transform: Maybe<*>.() -> Completable): MaybeToCompletableTests =
            object : MaybeToCompletableTests {
                private val upstream = TestMaybe<Nothing>()
                private val observer = upstream.transform().test()

                override fun calls_onSubscribe_only_once_WHEN_subscribed() {
                    observer.assertSubscribed()
                }

                override fun completes_WHEN_upstream_is_completed() {
                    upstream.onComplete()

                    observer.assertComplete()
                }

                override fun produces_error_WHEN_upstream_produced_error() {
                    val error = Throwable()

                    upstream.onError(error)

                    observer.assertError(error)
                }

                override fun disposes_upstream_WHEN_disposed() {
                    observer.dispose()

                    assertTrue(upstream.isDisposed)
                }
            }
    }
}