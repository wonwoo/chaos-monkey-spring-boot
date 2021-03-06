/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.spring.boot.chaos.monkey.assaults;

import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.component.Metrics;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Thorsten Deelmann
 */
public class KillAppAssault implements ChaosMonkeyAssault, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(KillAppAssault.class);
    private ApplicationContext context;
    private final ChaosMonkeySettings settings;
    private final Metrics metrics;

    public KillAppAssault(ChaosMonkeySettings settings, Metrics metrics) {
        this.settings = settings;
        this.metrics = metrics;
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isKillApplicationActive();
    }

    @Override
    public void attack() {
        try {
            LOGGER.info("Chaos Monkey - I am killing your Application!");

            if (metrics != null)
            {
                metrics.counter(MetricType.KILLAPP_ASSAULT).increment();
            }
            int exit = SpringApplication.exit(context, (ExitCodeGenerator) () -> 0);
            Thread.sleep(5000); // wait befor kill to deliver some metrics

            System.exit(exit);
        } catch (Exception e) {
            LOGGER.info("Chaos Monkey - Unable to kill the App, I am not the BOSS!");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
