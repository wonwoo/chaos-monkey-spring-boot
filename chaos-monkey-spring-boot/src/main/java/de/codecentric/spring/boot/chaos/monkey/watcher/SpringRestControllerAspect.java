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

package de.codecentric.spring.boot.chaos.monkey.watcher;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkey;
import de.codecentric.spring.boot.chaos.monkey.component.MetricType;
import de.codecentric.spring.boot.chaos.monkey.component.Metrics;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author Benjamin Wilms
 */

@Aspect
public class SpringRestControllerAspect extends ChaosMonkeyBaseAspect {

    private final ChaosMonkey chaosMonkey;
    private final Metrics metrics;

    public SpringRestControllerAspect(ChaosMonkey chaosMonkey, Metrics metrics) {
        this.chaosMonkey = chaosMonkey;
        this.metrics = metrics;
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void classAnnotatedWithControllerPointcut() {
    }

    @Around("classAnnotatedWithControllerPointcut() && allPublicMethodPointcut() && !classInChaosMonkeyPackage()")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {

        // metrics
        if (metrics != null)
            metrics.counterWatcher(MetricType.RESTCONTROLLER, calculatePointcut(pjp.toShortString())).increment();

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        chaosMonkey.callChaosMonkey(createSignature(signature));

        return pjp.proceed();
    }

}
