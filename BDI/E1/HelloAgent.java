package agent;

import bdi4jade.core.SingleCapabilityAgent;
import bdi4jade.goal.Goal;
import bdi4jade.plan.DefaultPlan;
import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.AbstractPlanBody;

public class HelloAgent extends SingleCapabilityAgent {

    public static class HelloGoal implements Goal {
        private final String text;
        public HelloGoal(String text) {
            this.text = text;
        }
        public String getText() {
            return text;
        }
    }

    public static class HelloPlanBody extends AbstractPlanBody {
        int count = 0;
        @Override
        public void action() {
            HelloGoal goal = (HelloGoal) getGoal();
            System.out.println("Hello " + goal.getText() + count);

            if(count ==10)setEndState(Plan.EndState.SUCCESSFUL);
            ++count;
        }
    }

    public HelloAgent() {
        Plan plan = new DefaultPlan(HelloGoal.class, HelloPlanBody.class);
        addGoal(new HelloGoal("world"));
        addGoal(new HelloGoal("world"));
        getCapability().getPlanLibrary().addPlan(plan);
    }
}
