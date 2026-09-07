package com.lynqo.backend.presence.service;

public interface PresenceService {

    void setUserStatus(int userId, String status);

    String getUserStatus(int userId);

}
