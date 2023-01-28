package com.isaacudy.archunit.example.infrastructure

import kotlinx.coroutines.flow.*

sealed class AsyncState<T: Any> {
    class None<T: Any> : AsyncState<T>()
    class Loading<T: Any> : AsyncState<T>()
    class Success<T: Any>(val value: T) : AsyncState<T>()
    class Error<T: Any>(val throwable: Throwable) : AsyncState<T>()

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun <T: Any> none(): AsyncState<T> = None()
        fun <T: Any> loading(): AsyncState<T> = Loading()
        fun <T: Any> success(value: T): AsyncState<T> = Success(value)
        fun <T: Any> error(throwable: Throwable): AsyncState<T> = Error(throwable)
    }
}

inline fun <T : Any> AsyncState<T>.onNone(block: () -> Unit): AsyncState<T> {
    if(this is AsyncState.None) block()
    return this
}

inline fun <T : Any> AsyncState<T>.onLoading(block: () -> Unit): AsyncState<T> {
    if(this is AsyncState.Loading) block()
    return this
}

inline fun <T : Any> AsyncState<T>.onSuccess(block: (value: T) -> Unit): AsyncState<T> {
    if(this is AsyncState.Success) block(value)
    return this
}

inline fun <T : Any> AsyncState<T>.onError(block: (throwable: Throwable) -> Unit): AsyncState<T> {
    if(this is AsyncState.Error) block(throwable)
    return this
}

fun <T: Any> AsyncState<T>.getOrNull() : T? {
    onSuccess { return it }
    return null
}

fun <T: Any> AsyncState<T>.getOrThrow() : T {
    when(this) {
        is AsyncState.Error -> throw throwable
        is AsyncState.Loading -> throw IllegalStateException()
        is AsyncState.None -> throw IllegalStateException()
        is AsyncState.Success -> return value
    }
}

fun <T: Any> Flow<T>.asAsyncState() : Flow<AsyncState<T>> = flow {
    emit(AsyncState.loading())

    val asyncStates = this@asAsyncState
        .map { AsyncState.success(it) }
        .catch { emit(AsyncState.error(it)) }
    emitAll(asyncStates)
}