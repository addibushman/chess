package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.ClearService;
import results.ClearResult;

public class ClearHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();

        try {
            ClearService clearService = new ClearService();
            ClearResult result = clearService.clear();

            res.type("application/json");
            if (result.isSuccess()) {
                res.status(200);
            } else {
                res.status(500);
            }
            return gson.toJson(result);

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ClearResult(false, "Error: " + e.getMessage()));
        }
    }
}