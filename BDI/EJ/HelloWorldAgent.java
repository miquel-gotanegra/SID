package bdi4jade.examples.helloworld;

import bdi4jade.core.SingleCapabilityAgent;
import bdi4jade.goal.Goal;
import bdi4jade.plan.DefaultPlan;
import bdi4jade.plan.Plan.EndState;
import bdi4jade.plan.planbody.AbstractPlanBody;

/**
 * @author Ingrid Nunes
 */
public class HelloWorldAgent extends SingleCapabilityAgent {

	public static class HelloWorldGoal implements Goal {
		private static final long serialVersionUID = -9039447524062487795L;

		private String name;

		public HelloWorldGoal(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static class HelloWorldPlanBody extends AbstractPlanBody {
		private static final long serialVersionUID = -9039447524062487795L;

		public void action() {
			System.out.println("Hello, "
					+ ((HelloWorldGoal) getGoal()).getName() + "!");
			setEndState(EndState.SUCCESSFUL);
		}
	}

	private static final long serialVersionUID = 2712019445290687786L;

	public HelloWorldAgent() {
		getCapability().getPlanLibrary()
				.addPlan(
						new DefaultPlan(HelloWorldGoal.class,
								HelloWorldPlanBody.class));
	}

}