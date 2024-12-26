import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import spock.lang.Specification;

import static groovy.util.GroovyTestCase.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PongServiceSpec extends Specification{

    @Autowired
    private WebTestClient webTestClient;


    //Responds with 'World' within rate limit
    @Test
    void shouldRespondWithWorldWithinRateLimit() {
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/pong/respond")
                .exchange();
        // Validate status code
        assertEquals(HttpStatus.OK, response.returnResult(String.class).getStatus());
        // Validate body
        String responseBody = response.returnResult(String.class).getResponseBody().blockFirst();
        assertEquals("World", responseBody);
    }

    //Responds with 429 when rate limit exceeded
    @Test
    void shouldRespondWith429WhenRateLimitExceeded() {
        // Send multiple requests to exceed rate limit
        for (int i = 0; i < 2; i++) {
            webTestClient.get().uri("/pong/respond").exchange();
        }

        webTestClient.get()
                .uri("/pong/respond")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

}
