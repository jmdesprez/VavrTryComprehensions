package jm.desprez.vavr

import io.vavr.control.Try
import kotlin.coroutines.experimental.*

class TryComp<T>(override val context: CoroutineContext) : Continuation<Try<T>> {

    lateinit var result: Try<T>

    override fun resume(value: Try<T>) {
        result = value
    }

    override fun resumeWithException(exception: Throwable) {
        result = Try.failure(exception)
    }

    suspend fun <U> Try<U>.bind(): U = suspendCoroutine {
        if (isSuccess) it.resume(get())
        else it.resumeWithException(cause)
        // else result = this as Try<T> // possible but ugly :)
    }

    suspend fun <U> assert(block: () -> Try<U>): U = block().bind()
    suspend fun fail(error: Throwable): Nothing = suspendCoroutine { result = Try.failure(error) }
    suspend fun fail(message: String): Nothing = fail(AssertionError(message))
    fun <U> success(value: U): U = value
    fun success() {}

    fun yield(value: T): Try<T> = Try.success(value)
    @JvmName("__yieldExt")
    fun T.yield() = yield(this)
}

fun <T> tryTo(context: CoroutineContext = EmptyCoroutineContext, block: suspend TryComp<T>.() -> Try<T>): Try<T> {
    val comp = TryComp<T>(context)
    block.startCoroutine(comp, comp)
    return comp.result
}
