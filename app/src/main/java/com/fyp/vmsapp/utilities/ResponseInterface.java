package com.fyp.vmsapp.utilities;

import org.json.JSONException;
import org.json.JSONObject;

public interface ResponseInterface {
    void response(JSONObject response) throws JSONException;

    void failure(String message);
}