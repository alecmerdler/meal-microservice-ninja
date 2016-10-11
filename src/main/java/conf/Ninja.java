package conf;

import ninja.Context;
import ninja.NinjaDefault;
import ninja.Result;
import ninja.Results;

/**
 * Created by alec on 10/7/16.
 */
public class Ninja extends NinjaDefault {

    @Override
    public Result getBadRequestResult(Context context, Exception exception) {
        return Results.json()
                .render("error", exception.getMessage())
                .status(400);
    }
}
