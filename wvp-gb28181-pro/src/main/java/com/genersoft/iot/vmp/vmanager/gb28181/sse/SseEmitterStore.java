package com.genersoft.iot.vmp.vmanager.gb28181.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterStore {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void addEmitter(String browserId, SseEmitter emitter) {
        emitters.put(browserId, emitter);
    }

    public SseEmitter getEmitter(String browserId) {
        return emitters.get(browserId);
    }

    public void removeEmitter(String browserId) {
        emitters.remove(browserId);
    }

    public Map<String, SseEmitter> getAllEmitters() {
        return emitters;
    }
}
