package jm.desprez.vavr

import com.natpryce.hamkrest.*
import io.vavr.control.Try

val isSuccess = Matcher(Try<*>::isSuccess)
val isFailure = Matcher(Try<*>::isFailure)
fun <T> contains(value: T) = isSuccess and Matcher(Try<T>::contains, value)
inline fun <reified T : Throwable> failWith() = isFailure and has(Try<*>::getCause, isA<T>())
inline fun <reified T : Throwable> failWithMessage(message: String) =
        isFailure and has(Try<*>::getCause, isA<T>(has(Throwable::message, equalTo(message))))

val isTrue = equalTo(true)
