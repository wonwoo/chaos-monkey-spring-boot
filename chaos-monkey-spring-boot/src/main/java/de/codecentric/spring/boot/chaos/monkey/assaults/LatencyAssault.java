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
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thorsten Deelmann
 */
public class LatencyAssault implements ChaosMonkeyAssault {

    private static final Logger LOGGER = LoggerFactory.getLogger(LatencyAssault.class);
    private final ChaosMonkeySettings settings;
    private final Metrics metrics;
    private AtomicInteger atomicTimeoutGauge;

    public LatencyAssault(ChaosMonkeySettings settings, Metrics metrics) {
        this.settings = settings;
        this.metrics = metrics;
        this.atomicTimeoutGauge = new AtomicInteger(0);
    }

    @Override
    public boolean isActive() {
        return settings.getAssaultProperties().isLatencyActive();
    }

    @Override
    public void attack() {
        LOGGER.debug("Chaos Monkey - timeout");
        int timeout = RandomUtils.nextInt(settings.getAssaultProperties().getLatencyRangeStart(), settings.getAssaultProperties().getLatencyRangeEnd());
        atomicTimeoutGauge.set(timeout);
        if (metrics != null) {
            // metrics
            metrics.counter(MetricType.LATENCY_ASSAULT).increment();
            metrics.gauge(MetricType.LATENCY_ASSAULT, atomicTimeoutGauge);
        }

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
