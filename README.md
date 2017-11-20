_Disclaimer: this project is experimental, use at your own risk :)_

# VavrTryComprehensions
Monad Comprehensions for Vavr Try

You can have a look at [Kategory documentation](http://kategory.io/docs/patterns/monad_comprehensions/) for a explanation about Monad Comprehensions

# Example
```kotlin
import io.vavr.control.Try

fun main(args: Array<String>) {
    data class LiveIn(val username: String, val countryName: String)

    val liveIn: Try<LiveIn> = tryTo {
        // fun tryFetchUser(id: Int): Try<User>
        // fun tryFetchCity(ref: String): Try<City>
        // fun tryFetchCountry(code: String): Try<Country>
        val user: User = tryFetchUser(0).bind()
        val city: City = tryFetchCity(user.cityRef).bind()
        val country: Country = tryFetchCountry(city.countryCode).bind()

        yield(LiveIn(user.lastname, country.name))
    }

    println(liveIn)
}
```
