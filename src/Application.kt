package ch.fabio.projects.main

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openWriteChannel
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.url
import kotlinx.coroutines.*
import kotlinx.coroutines.io.writeStringUtf8
import kotlinx.css.*
import kotlinx.css.Float
import kotlinx.html.*
import java.net.InetSocketAddress


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
    }

    routing {
        get("/") {
            call.respondHtml {
                head {
                    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
                    link(rel = "stylesheet", href = "/static/fontface.css", type = "text/css")
                    meta(name = "viewport", content = "width=device-width,initial-scale=1")
                }
                body {
                    script {
                        src = "/static/api-calls.js"
                    }
                    img(classes = "background", src = "/static/background.png")
                    div("main_container") {
                        h1 { +"Pi Piano" }
                        div("button_container") {
                            button(classes = "selection_button") {
                                onClick = "send_sound_call('piano')"
                                +"Piano"
                            }
                            button(classes = "selection_button") {
                                onClick = "send_sound_call('organ')"
                                +"Organ"
                            }
                            button(classes = "selection_button") {
                                onClick = "send_sound_call('e_piano')"
                                +"E-Piano"
                            }
                        }
                    }
                    img(classes = "power_off_button shutdown", src = "/static/shutdown.png")

                }
            }
        }

        get("/styles.css") {
            call.respondCss {

                body {
                    margin = "0 0 0 0"
                }

                h1 {
                    fontFamily = "GreatVibes"
                    textAlign = TextAlign.center
                    fontSize = 8.em
                    color = Color("#ffdede")
                    margin = "80px 0 60px 0"
                    fontWeight = FontWeight("550")
                    userSelect = UserSelect.none
                }
                rule("img.background") {
                    height = 480.px
                    width = 800.px
                    position = Position.absolute
                }
                rule("img.shutdown") {
                    height = 52.px
                    width = 52.px
                    float = Float.right
                    margin = "12px"
                }
                p {
                    fontSize = 2.em
                }

                button {
                    border = "4px solid"
                    borderRadius = 16.px
                    borderColor = Color("#9F606C")
                    backgroundColor = Color.transparent

                    fontFamily = "GreatVibes"
                    textAlign = TextAlign.center
                    fontSize = 2.em
                    color = Color("#ffdede")
                }

                rule("button:focus") {
                    outline = Outline.none
                    border = "4px solid"
                    borderRadius = 16.px
                    borderColor = Color("#FFC0CC")
                }
                rule("button.selection_button") {
                    padding = "30px"
                    margin = "0 30px 0 30px"
                }
                rule("div.button_container") {
                    display = Display.flex
                    justifyContent = JustifyContent.center
                }
                rule("div.main_container") {
                    position = Position.absolute
                    height = 480.px
                    width = 800.px
                    backgroundColor = blackAlpha(0.15)
                }
            }
        }

        post("/change_sound") {
            when (call.receive<String>()) {
                "piano" -> {
                    println("Got Piano")
                    sendFluidSynthCommand("prog 0 1")
                }
                "organ" -> {
                    println("Got Organ")
                    sendFluidSynthCommand("prog 0 17")
                }
                "e_piano" -> {
                    println("Got E-Piano")
                    sendFluidSynthCommand("prog 0 4")
                }
                "shutdown" -> withContext(Dispatchers.IO) {
                    Runtime.getRuntime().exec("poweroff")
                }
                else -> println("Invalid sound name")
            }
        }

        // Static feature. Access any resource with the /static/ prefix path
        static("/static") {
            resources("static")
        }
    }
}

@KtorExperimentalAPI
fun sendFluidSynthCommand(command: String) = runBlocking {
    val socket =
        aSocket(ActorSelectorManager(Dispatchers.IO))
            .tcp()
            .connect(
                InetSocketAddress("127.0.0.1", 9800)
            )
    val output = socket.openWriteChannel(autoFlush = true)

    output.writeStringUtf8(command)

    socket.dispose()
}


fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
