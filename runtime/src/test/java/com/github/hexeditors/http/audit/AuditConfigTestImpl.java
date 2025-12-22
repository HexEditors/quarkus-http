package com.github.hexeditors.http.audit;

public class AuditConfigTestImpl implements AuditConfig {

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public boolean gdprEnabled() {
        return true;
    }

    @Override
    public boolean pciEnabled() {
        return true;
    }

    @Override
    public String serviceName() {
        return "test-service";
    }
}
