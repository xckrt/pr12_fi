import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select
suspend fun simulateButtonClick(buttonId: Int): String {
    delay(1000)
    return "Кнопка номер $buttonId нажата"
}
suspend fun fetchDataFromSource(sourceId: Int): String {
    delay(2000)
    return "Информация из источника $sourceId"
}
suspend fun main() = coroutineScope {
    println("Введите количество функций (n): ")
    val n = readLine()?.toIntOrNull() ?: return@coroutineScope
    val buttonClickChannel = Channel<String>()
    val dataChannel = Channel<String>()
    val buttonJob = launch {
        repeat(n) { buttonId ->
            val result = simulateButtonClick(buttonId)
            buttonClickChannel.send(result)
        }
    }
    val dataJobs = List(n) { sourceId ->
        launch {
            val result = fetchDataFromSource(sourceId)
            dataChannel.send(result)
        }
    }
    val combinedResults = mutableListOf<String>()
    repeat(n * 2) {
        val result = select<String> {
            buttonClickChannel.onReceive { it }
            dataChannel.onReceive { it }
        }
        combinedResults.add(result)
    }
    combinedResults.forEach { println(it) }
    buttonJob.join()
    dataJobs.forEach { it.join() }
}


