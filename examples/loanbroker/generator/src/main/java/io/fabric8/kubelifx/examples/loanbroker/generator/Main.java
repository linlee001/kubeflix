/*
 * Copyright (C) 2015 Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.kubelifx.examples.loanbroker.generator;

import org.apache.camel.Exchange;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@SpringBootApplication
public class Main extends FatJarRouter {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        FatJarRouter.main(args);
    }

    @Override
    public void configure() throws Exception {
        from("timer://foo?period=5000")
            .setHeader("ssn", method(SsnGenerator.class, "generate"))
            .setHeader("duration", method(DurationGenerator.class, "generate"))
            .setHeader("amount", method(AmountGenerator.class, "generate"))
            .setHeader(Exchange.HTTP_QUERY, simple("ssn=${header.ssn}&duration=${header.duration}&amount=${header.amount}"))
            .to("http://loanbroker-broker/quote")
            .log(">>> ${body}");
    }


    @Bean
    public AmountGenerator amount() {
        return new AmountGenerator();
    }

    @Bean
    public SsnGenerator ssn() {
        return new SsnGenerator();
    }

    @Bean
    public DurationGenerator duration() {
        return new DurationGenerator();
    }
}