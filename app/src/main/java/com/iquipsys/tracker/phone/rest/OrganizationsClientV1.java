package com.iquipsys.tracker.phone.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URLEncoder;

public class OrganizationsClientV1 extends RestClient {
    private String _baseRoute;

    public OrganizationsClientV1(String baseRoute) {
        _baseRoute = baseRoute + "/api/v1";
    }

    public OrganizationV1 findOrganizationByCode(String organizationCode) throws Exception {
        String route = _baseRoute
            + "/organizations/find_by_code?code=" + URLEncoder.encode(organizationCode, "UTF-8");

        String result = executeGet(route);

        Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();
        OrganizationV1 organization = gson.fromJson(result, OrganizationV1.class);
        return organization;
    }

}
