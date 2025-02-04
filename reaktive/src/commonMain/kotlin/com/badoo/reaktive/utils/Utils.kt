package com.badoo.reaktive.utils

internal fun handleSourceError(error: Throwable, onError: ((Throwable) -> Unit)? = null) {
    try {
        if (onError == null) {
            reaktiveUncaughtErrorHandler(error)
        } else {
            try {
                onError(error)
            } catch (e: Throwable) {
                printError("onError callback failed ($error): $e")
                error.printStack()
                reaktiveUncaughtErrorHandler(e)
            }
        }
    } catch (e: Throwable) {
        printError("Error delivering uncaught error ($error): $e")
        error.printStack()
        e.printStack()
    }
}

internal object Uninitialized