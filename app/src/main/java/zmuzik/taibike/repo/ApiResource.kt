package zmuzik.taibike.repo

sealed class ApiResource<out T> {
    class Loading<out T> : ApiResource<T>()
    data class Success<out T>(val data: T?) : ApiResource<T>()
    data class Failure<out T>(val throwable: Throwable) : ApiResource<T>()
}