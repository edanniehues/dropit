package dropit.application.discovery

import com.fasterxml.jackson.databind.ObjectMapper
import dropit.application.dto.BroadcastMessage
import dropit.infrastructure.event.AppEvent
import dropit.infrastructure.event.EventBus
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.net.SocketTimeoutException

val DISCOVERY_GROUP = InetAddress.getByName("237.0.0.0")
const val DISCOVERY_PORT = 58993

class DiscoveryClient(private val objectMapper: ObjectMapper, private val eventBus: EventBus) {
    data class ServerBroadcast(val data: BroadcastMessage, val ip: InetAddress)
    data class DiscoveryEvent(override val payload: ServerBroadcast) : AppEvent<ServerBroadcast>

    val buffer = ByteArray(4096)
    val socket = MulticastSocket(DISCOVERY_PORT)
    val runnable = Runnable {
        socket.soTimeout = 500
        socket.joinGroup(DISCOVERY_GROUP)
        while (running) {
            try {
                val broadcast = DatagramPacket(buffer, buffer.size).apply { socket.receive(this) }
                    .let { Pair(String(it.data, 0, it.length), it.address) }
                    .let { (data, address) -> ServerBroadcast(objectMapper.readValue(data, BroadcastMessage::class.java), address) }
                eventBus.broadcast(DiscoveryEvent(broadcast))
            } catch (e: SocketTimeoutException) {
                // nop
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private var running = true
    var receiverThread = Thread(runnable)

    init {
        receiverThread.start()
    }

    fun stop() {
        running = false
        receiverThread.join()
    }
}

