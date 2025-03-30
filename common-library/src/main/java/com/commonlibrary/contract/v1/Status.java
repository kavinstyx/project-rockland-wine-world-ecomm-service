package com.commonlibrary.contract.v1;

public enum Status {
    SUCCESS("success"),
    ERROR("error"),
    WARNING("warning");

    final String status;

    Status(String status)
    {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}