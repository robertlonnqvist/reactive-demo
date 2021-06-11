package com.robertlonnqvist.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
class DemoControllerTest {

    static final int DATA_LENGTH = 8_000_000;

    @MockBean
    DemoService demoService;

    @LocalServerPort
    int port;

    @Autowired
    WebClient.Builder webClientBuilder;

    WebClient webClient;

    @BeforeEach
    void each() {
        webClient = webClientBuilder
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void test() {
        ResponseEntity<DataBuffer> entity = ResponseEntity
                .ok()
                .contentLength(8_000_000)
                .body(buffer());

        when(demoService.get()).thenReturn(Mono.just(entity));

        Mono<ResponseEntity<DataBuffer>> objectMono = webClient.get()
                .exchangeToMono((r) -> r.toEntity(DataBuffer.class));

        ResponseEntity<DataBuffer> block = objectMono.block();

        assertNotNull(block);
        assertEquals(HttpStatus.OK, block.getStatusCode());
        assertEquals(DATA_LENGTH, block.getHeaders().getContentLength());
    }

    private static DataBuffer buffer() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        Random random = new Random();
        byte[] bytes = new byte[DATA_LENGTH];
        random.nextBytes(bytes);
        return factory.wrap(bytes);
    }
}