package com.taxi.app;

import org.json.JSONException;

public interface NetworkInterface {
    public void sending(String url) throws JSONException;
}
