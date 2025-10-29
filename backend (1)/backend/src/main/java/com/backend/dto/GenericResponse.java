package com.backend.dto;

public class GenericResponse {
    private boolean success;
    private String message;
    private String role;
    private Object data;

    public GenericResponse() {
    }

    public GenericResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public GenericResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public GenericResponse(boolean success, String message, String role, Object data) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.data = data;
    }

    // getters/setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
