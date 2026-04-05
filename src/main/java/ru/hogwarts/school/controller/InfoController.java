package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/port")
    public int getPort() {
        logger.info("Was invoked method for get server port");
        logger.debug("Returning port: {}", serverPort);
        return serverPort;
    }
}