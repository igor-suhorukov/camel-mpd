package com.github.igorsuhorukov.mpd;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ProducerTest extends CamelTestSupport {
    @Test
    public void testMpdProducer() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("mpd:playlist?host=192.168.1.68&port=6600")
                        .to("mock:result");
            }
        };
    }
}
