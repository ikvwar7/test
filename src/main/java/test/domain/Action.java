package test.domain;

import test.Result;
import test.domain.action.strategies.ActionStrategy;

public class Action {
    private ActionStrategy actionStrategy;

    public Action(ActionStrategy actionStrategy) {
        this.actionStrategy = actionStrategy;
    }

    public Result process() {
       return actionStrategy.process();
    };
}