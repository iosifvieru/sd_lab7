import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.*
import java.net.ServerSocket
import java.net.SocketTimeoutException
import java.util.LinkedList
import java.util.Queue
import kotlin.system.exitProcess

class Logger {
    private var loggingServerSocket: ServerSocket
    private var loggingObservable: Observable<String>

    private var logQueue: Queue<Message> = LinkedList<Message>()
    // logs
    private lateinit var file: File
    private lateinit var writer: BufferedWriter
    private var path = System.getProperty("user.dir") + "/logs.txt"

    companion object Constants {
        const val LOGGING_PORT = 54545
        const val TIMEOUT = 16000
    }

    init {
        loggingServerSocket = ServerSocket(LOGGING_PORT)
        loggingServerSocket.setSoTimeout(TIMEOUT.toInt())

        file = File(path)
        if(!file.exists()){
            file.createNewFile()
        }

        writer = BufferedWriter(FileWriter(file, true))
        writer.write("\n")
        writer.write(Message.create("Logger", "LoggingMicroservice se executa pe portul: ${loggingServerSocket.localPort}\n").toString())
        writer.write(Message.create("Logger", "astept conexiuni!\n\n").toString())

        println("LoggingMicroservice se executa pe portul: ${loggingServerSocket.localPort}")
        writer.close()

        loggingObservable = Observable.create { emitter ->
            while(true) {
                try {
                    val loggingConnection = loggingServerSocket.accept()
                    val bufferReader = BufferedReader(InputStreamReader(loggingConnection.inputStream))

                    val receivedMessage = bufferReader.readLine()

                    if (receivedMessage == null) {
                        bufferReader.close()
                        loggingConnection.close()
                        break
                    }
                    loggingConnection.close()

                    //println(receivedMessage)
                    emitter.onNext(receivedMessage)
                } catch (e: SocketTimeoutException){
                    emitter.onComplete()
                }
            }
        }

    }

    private fun waitForLogs() {
        println("[LOGGER] ASTEPT !!")
        val logger = loggingObservable.subscribeBy(
            onNext = {
                val message = Message.deserialize(it.toByteArray())

                println("[LOGGER] -> $message")
                logQueue.add(message)
            },
            onError = {
                println("[LOGGER] Eroare: $it")
            },
            onComplete = {
                println("[LOGGER] Urmeaza sa scriu logurile primite...")
                writeLogs()
            }
        )
    }

    private fun writeLogs(){
        file = File(path)
        if(!file.exists()){
            file.createNewFile()
        }
        writer = BufferedWriter(FileWriter(file, true))

        val logs = Observable.fromIterable(logQueue).subscribeBy (
            onNext = {
                println("[AM PROCESAT] $it")

                writer.write(it.toString() + "\n")
            },
            onComplete = {
                println("AM TERMINAT!!!")
                writer.close()
                loggingServerSocket.close()
                exitProcess(1)
            },
            onError = {
                println("Error: $it")
            }
        )

    }

    fun run() {
        this.waitForLogs()
    }
}

fun main(args: Array<String>){
    val logMicroservice = Logger()

    logMicroservice.run()
}
