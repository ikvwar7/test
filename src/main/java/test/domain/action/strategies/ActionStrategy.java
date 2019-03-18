package test.domain.action.strategies;

import test.domain.Result;
import test.db.DBManager;
import test.domain.actions.object.ActionObject;

public abstract class ActionStrategy {
    protected ActionObject actionObject;
    protected DBManager dbManager;

    public ActionStrategy(ActionObject actionObject, DBManager dbManager) {
        this.actionObject = actionObject;
        this.dbManager = dbManager;
    }

    public abstract Result process();
}
