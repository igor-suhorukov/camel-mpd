package com.github.igorsuhorukov.mpd;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Arrays;

public class ConsumerTest extends CamelTestSupport {
    @Test
    public void testSendCommands() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        sendBody("direct:entryPoint", Arrays.asList("https://mp3d.jamendo.com/?trackid=472893&format=mp33&from=app-97dab294", "https://mp3d.jamendo.com/?trackid=472893&format=mp33&from=app-97dab294"));
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:entryPoint")
                        .to("mpd:song?host=192.168.1.68&port=6600")
                        .to("mock:result");
            }
        };
    }
}
