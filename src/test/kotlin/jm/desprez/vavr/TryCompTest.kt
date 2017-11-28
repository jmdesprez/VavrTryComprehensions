package jm.desprez.vavr

import com.natpryce.hamkrest.assertion.assert
import io.vavr.control.Try
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.lang.Thread.yield
import java.util.function.Predicate
import java.util.function.Supplier

fun tryGetA(): Try<Int> = Try.success(0)
fun tryGetB(): Try<Int> = Try.success(1)
fun tryFail(): Try<Int> = Try.failure(Exception("Test exception"))

object TryComprehensionSpek : Spek({
    given("a simple addition") {
        on("yield value using yield(value)") {
            val tryTo: Try<String> = tryTo {
                val a: Int = tryGetA().bind()
                val b: Int = tryGetB().bind()
                yield("${a + b}")
            }
            it("should succeed and contains the sum") {
                assert.that(tryTo, contains("1"))
            }
        }
        on("yield value using extension function") {
            val tryTo: Try<String> = tryTo {
                val a: Int = tryGetA().bind()
                val b: Int = tryGetB().bind()
                "${a + b}".yield()
            }
            it("should succeed and contains the sum") {
                assert.that(tryTo, contains("1"))
            }
        }
    }

    given("an operation that throw an exception") {
        val tryTo: Try<Int> = tryTo {
            val a: Int = tryGetA().bind()
            val b: Int = tryGetB().bind()
            yield(b / a)
        }
        it("should fail and preserve the original exception") {
            assert.that(tryTo, failWith<ArithmeticException>())
        }
    }

    given("a block with a failing try") {
        var shouldNotReachMe = true
        val tryTo: Try<String> = tryTo {
            val fail: Int = tryFail().bind()
            shouldNotReachMe = false
            yield("$fail :)")
        }
        it("should fail") {
            assert.that(tryTo, isFailure)
        }
        it("should stop execution of the block") {
            assert.that(shouldNotReachMe, isTrue)
        }
    }

    given("a block with an assert (using 'assert' method)") {
        on("assert success") {
            val tryTo: Try<Int> = tryTo {
                assert {
                    tryGetA().filter(Predicate { it >= 0 }, Supplier { ArithmeticException("Must be positive or zero") })
                }.yield()
            }
            it("should succeed and contains the value") {
                assert.that(tryTo, contains(0))
            }
        }
        on("assert fail") {
            val tryTo: Try<Int> = tryTo {
                assert {
                    tryGetA().filter(Predicate { it > 0 }, Supplier { ArithmeticException("Must be strictly positive") })
                }.yield()
            }
            it("should fail and preserve the assert exception") {
                assert.that(tryTo, failWithMessage<ArithmeticException>("Must be strictly positive"))
            }
        }
    }

    given("a block with an assert (using 'success'/'fail' methods)") {
        on("assert success") {
            val tryTo: Try<Unit> = tryTo {
                val a: Int = tryGetA().bind()
                if (a >= 0) success()
                else fail("Must be positive or zero")
                yield(Unit)
            }
            it("should succeed") {
                assert.that(tryTo, isSuccess)
            }
        }
        on("assert fail with fail(Throwable)") {
            val tryTo: Try<Int> = tryTo {
                val a = tryGetA().bind()
                if (a > 0) success()
                else fail(ArithmeticException("Must be strictly positive"))
                yield(a)
            }
            it("should fail and preserve the exception") {
                assert.that(tryTo, failWithMessage<ArithmeticException>("Must be strictly positive"))
            }
        }
        on("assert fail with fail(String)") {
            val tryTo: Try<Int> = tryTo {
                val a = tryGetA().bind()
                if (a > 0) success()
                else fail("Must be strictly positive")
                yield(a)
            }
            it("should fail and create an AssertionError with the message") {
                assert.that(tryTo, failWithMessage<AssertionError>("Must be strictly positive"))
            }
        }
    }
})
