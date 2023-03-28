package com.codestates.config;

//@Slf4j
//@Component
//public class H2ServerConfig {
//    @Value("${h2.console.port}")
//    private Integer port;
//    private Server webServer;
//
//    @EventListener(ContextRefreshedEvent.class)
//    public void start() throws java.sql.SQLException {
//        log.info("started h2 console at port {}.", port);
//        this.webServer = Server.createWebServer("-webPort", port.toString()).start();
//    }
//
//    @EventListener(ContextClosedEvent.class)
//    public void stop() {
//        log.info("stopped h2 console at port {}.", port); this.webServer.stop();
//    }
//}
