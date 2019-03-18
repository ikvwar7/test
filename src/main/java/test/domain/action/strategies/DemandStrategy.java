package test.domain.action.strategies;

import test.Result;
import test.db.DBManager;
import test.domain.actions.object.ActionObject;
import test.domain.actions.object.DemandObject;

public class DemandStrategy extends ActionStrategy {

    public DemandStrategy(ActionObject actionObject, DBManager dbManager) {
        super(actionObject, dbManager);
    }

    @Override
    public Result process() {
        return dbManager.demand((DemandObject) actionObject);
    }
}
