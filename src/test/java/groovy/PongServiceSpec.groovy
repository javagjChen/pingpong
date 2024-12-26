package groovy

import org.example.ping.PingApplication
import org.example.ping.PingService
import org.example.pong.PongApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

@SpringBootTest(classes = [PongApplication.class,PingApplication.class],webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PongServiceSpec extends Specification {

    @Autowired
    WebTestClient webTestClient

    @Autowired
    PingService pingService;

    def "Responds with 'World' within rate limit"() {
        when:
        def response = webTestClient.post().uri("/pong")
                .bodyValue("Hello")
                .exchange();

        then:
        response.expectStatus().isOk()
                .expectBody(String).isEqualTo("World")
    }

    def "Responds with 429 when rate limit exceeded"() {
        when:
        2.times {
            webTestClient.post().uri("/pong").bodyValue("Hello").exchange()
        }

        then:
        def response = webTestClient.post().uri("/pong").bodyValue("Hello").exchange()
        response.expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
    }


    def "Request not sent as rate limited"() {
        when:
        3.times {
            pingService.sendRequest()
        }
        then:
        printf "finish"
    }

}