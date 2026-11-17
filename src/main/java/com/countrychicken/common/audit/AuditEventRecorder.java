package com.countrychicken.common.audit;

public interface AuditEventRecorder {

    void record(
            AuditEventType eventType,
            String resourceType,
            String resourceId,
            String details
    );
}
