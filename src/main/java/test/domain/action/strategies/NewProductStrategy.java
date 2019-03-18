package test.domain.action.strategies;

import test.Result;
import test.db.DBManager;
import test.domain.actions.object.ActionObject;
import test.domain.actions.object.NewProductObject;

public class NewProductStrategy extends ActionStrategy {

    public NewProductStrategy(ActionObject actionObject, DBManager dbManager) {
        super(actionObject, dbManager);
    }

    @Override
    public Result process() {
        return dbManager.addProduct((NewProductObject) actionObject);
    }
}
