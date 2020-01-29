package com.iquipsys.tracker.phone.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RestGatewayClientV1 extends RestClient {
    private String _baseRoute;

    public RestGatewayClientV1(String baseRoute) {
        _baseRoute = baseRoute + "/api/v1";
    }

    public void updateStatus(StatusMessageV1 message) throws Exception {
        String route = _baseRoute + "/gateway/update_status";

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();
        String body = gson.toJson(message);

        executePost(route, body);
    }

}
