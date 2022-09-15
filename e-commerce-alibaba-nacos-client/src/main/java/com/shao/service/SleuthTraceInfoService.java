package com.shao.service;

import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : SH35856
 * @Description: TODO
 * @date: 2022/9/14 9:30
 */
@Service
@Slf4j
public class SleuthTraceInfoService {
    private final Tracer tracer;

    public SleuthTraceInfoService(Tracer tracer) {
        this.tracer = tracer;
    }

    public void logCurrentTraceInfo() {
        log.info("sleuth trace id: [{}]", tracer.currentSpan().context().traceId());
        log.info("sleuth span id: [{}]", tracer.currentSpan().context().spanId());
    }
}
