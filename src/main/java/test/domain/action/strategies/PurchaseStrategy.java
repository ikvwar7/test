package test.domain.action.strategies;

import test.Result;
import test.db.DBManager;
import test.domain.actions.object.ActionObject;
import test.domain.actions.object.PurchaseOpbject;

public class PurchaseStrategy extends ActionStrategy {

    public PurchaseStrategy(ActionObject actionObject, DBManager dbManager) {
        super(actionObject, dbManager);
    }

    @Override
    public Result process() {
        return dbManager.purchase((PurchaseOpbject) actionObject);
    }
}
