package org.bsrserver.data.hitokoto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import org.bsrserver.AltairGreeter;
import org.bsrserver.config.Config;

public class QuotationsManager {
    private final ArrayList<String> quotations = new ArrayList<>();
    private final Logger logger;

    public QuotationsManager(AltairGreeter altairGreeter) {
        this.logger = altairGreeter.getLogger();
        altairGreeter.getScheduledExecutorService().scheduleAtFixedRate(this::scheduledTask, 0, 30, TimeUnit.SECONDS);
    }

    private void scheduledTask() {
        try {
            updateQuotations();
        } catch (Exception e) {
            logger.error("Failed to get quotations", e);
        }
    }

    private void updateQuotations() {
        // clear
        quotations.clear();

        // request
        String authorization = "KeySecuredClient " + Config.getInstance().getBackendSecuredClientKey();
        OkHttpClient client = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url(Config.getInstance().getBackendBaseUrl() + "/v1/hitokoto/quotations")
                .header("Authorization", authorization)
                .build();

        // get quotations
        JSONArray quotationsJSONArray = null;
        try {
            Response response = client.newCall(getRequest).execute();
            if (response.body() != null) {
                quotationsJSONArray = JSONObject
                        .parseObject(response.body().string())
                        .getJSONObject("data")
                        .getJSONArray("quotations");
            }
        } catch (IOException exception) {
            logger.error("Failed to get quotations", exception);
        }

        // save quotations
        if (quotationsJSONArray != null) {
            for (JSONObject quotation : quotationsJSONArray.toArray(JSONObject.class)) {
                quotations.add(quotation.getString("sourceName") + "：" + quotation.getString("content"));
            }
        }
    }

    public String getRandomQuotation() {
        if (!quotations.isEmpty()) {
            return quotations.get((int) (Math.random() * quotations.size()));
        } else {
            return "";
        }
    }
}
