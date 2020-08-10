package cash.just.atm.extension

class ImmutableList<T>(list: MutableList<T>) : List<T> by list
fun <T> MutableList<T>.toImmutable() =
    ImmutableList(this)