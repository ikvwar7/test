package test.domain.action.strategies;

import test.domain.Result;
import test.db.DBManager;
import test.domain.actions.object.ActionObject;
import test.domain.actions.object.SalesReportObject;


public class SalesReportStrategy extends ActionStrategy {

    public SalesReportStrategy(ActionObject actionObject, DBManager dbManager) {
        super(actionObject, dbManager);
    }

    @Override
    public Result process() {
        return dbManager.salesReport((SalesReportObject) actionObject);
    }
}
