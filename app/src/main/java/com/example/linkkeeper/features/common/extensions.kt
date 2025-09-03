package com.example.linkkeeper.features.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

internal suspend fun <T, R> ViewModel.runBlocking(
    onBlocking: suspend () -> T,
    onSuccess: suspend (T) -> R,
    onError: (Exception) -> R
): R = try {
    onSuccess(
        withContext(Dispatchers.IO) {
            onBlocking()
        }
    )
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    onError(exception)
}

fun <T> debounce(
    waitMs: Long = 300L,
    coroutineScope: CoroutineScope,
    action: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(waitMs)
            action(param)
        }
    }
}
