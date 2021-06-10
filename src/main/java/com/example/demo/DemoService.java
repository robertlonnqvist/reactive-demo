package com.example.demo;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class DemoService {

    private final WebClient webClient = WebClient.create();

    public Mono<ResponseEntity<DataBuffer>> get() {
        return webClient.get().uri(URI.create("https://jsonplaceholder.typicode.com/todos"))
                .retrieve()
                .toEntity(DataBuffer.class);
    }
}
