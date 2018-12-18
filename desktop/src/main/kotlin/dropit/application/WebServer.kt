package dropit.application

import com.fasterxml.jackson.databind.ObjectMapper
import dropit.application.dto.TokenRequest
import dropit.application.dto.TransferRequest
import dropit.application.security.TokenService
import dropit.application.settings.AppSettings
import dropit.domain.entity.Phone
import dropit.domain.service.PhoneService
import dropit.domain.service.TransferService
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.json.JavalinJackson
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebServer @Inject constructor(
    val appSettings: AppSettings,
    val phoneService: PhoneService,
    val transferService: TransferService,
    val token: TokenService,
    val objectMapper: ObjectMapper
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    val javalin: Javalin
    init {
        JavalinJackson.configure(objectMapper)
        javalin = Javalin.create()
                .requestLogger { ctx, ms ->
                    val phone = ctx.attribute<Phone>("phone")
                    logger.info("[${phone?.name}] ${ctx.method()} ${ctx.path()} took $ms ms")
                }
                .server {
                    val server = Server()
                    val connector = ServerConnector(server, getSslContextFactory())
                    connector.port = appSettings.settings.serverPort
                    server.connectors = arrayOf(connector)
                    server
                }
                .start(appSettings.settings.serverPort)
                .routes {
                    get("/") {
                        it.result("0.1")
                    }
                    path("token") {
                        post {
                            it.json(phoneService.requestToken(it.bodyAsClass(TokenRequest::class.java)))
                        }

                        get {
                            it.attribute("phone", token.getPendingPhone(it))
                            val phone = token.getPendingPhone(it)
                            it.json(phoneService.getTokenStatus(phone.token.toString()))
                        }
                    }
                    post("transfers") {
                        it.attribute("phone", token.getApprovedPhone(it))
                        it.json(transferService.createTransfer(
                                it.attribute<Phone>("phone")!!,
                                it.bodyAsClass(TransferRequest::class.java)))
                    }
                    post("files/:id") {
                        it.attribute("phone", token.getApprovedPhone(it))
                        transferService.uploadFile(
                                it.attribute<Phone>("phone")!!,
                                it.pathParam("id"),
                                it.uploadedFile("file")!!.content
                        )
                        it.status(201)
                    }
                }
    }

    private fun getSslContextFactory(): SslContextFactory {
        val sslContextFactory = SslContextFactory()
        sslContextFactory.keyStorePath = this::class.java.getResource("/ssl/dropit.jks").toExternalForm()
        sslContextFactory.setKeyStorePassword("""C<9/wg${"$"}uxV2nCBMT""")
        return sslContextFactory
    }
}