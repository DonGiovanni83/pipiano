package ch.fabio.projects.main

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.css.*
import kotlinx.html.*
import org.intellij.lang.annotations.JdkConstants

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
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
                    img(src = "/static/background.png")
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
                                onClick = "send_sound_call('jazz_organ')"
                                +"Jazz Organ"
                            }
                        }
                    }
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
                }
                img {
                    height = 480.px
                    width = 800.px
                    position = Position.absolute
                }
                p {
                    fontSize = 2.em
                }

                button {
                    border = "4px solid"
                    borderRadius = 16.px
                    borderColor = Color("#FFC0CC")
                    backgroundColor = Color.transparent

                    fontFamily = "GreatVibes"
                    textAlign = TextAlign.center
                    fontSize = 2.em
                    color = Color("#ffdede")
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
                    backgroundColor = blackAlpha(0.2)
                }
            }
        }

        post("/change_sound") {
            when (call.receive<String>()) {
                "piano" -> println("Got Piano")
                "organ" -> println("Got Organ")
                "jazz_organ" -> println("Got Jazz Organ")
                else -> print("Invalid sound name")
            }
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }
    }
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
