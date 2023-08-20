import java.util.concurrent.CompletableFuture

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val future = CompletableFuture<String>()
        future.thenCompose {
            CompletableFuture.completedFuture("$it aaa")
        }.thenAccept {
            println(it)
        }
        future.complete("Abc")

    }

}