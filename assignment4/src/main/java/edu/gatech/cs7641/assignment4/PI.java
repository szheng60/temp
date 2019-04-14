package edu.gatech.cs7641.assignment4;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;

import java.util.Set;

public class PI extends PolicyIteration {
    private double convergence;

    public PI(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta, int maxEvaluationIterations, int maxPolicyIterations) {
        super(domain, gamma, hashingFactory, maxDelta, maxEvaluationIterations, maxPolicyIterations);
    }

    public PI(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxPIDelta, double maxEvalDelta, int maxEvaluationIterations, int maxPolicyIterations) {
        super(domain, gamma, hashingFactory, maxPIDelta, maxEvalDelta, maxEvaluationIterations, maxPolicyIterations);
    }

    public double getConvergence() {
        return this.convergence;
    }

    /**
     * Computes the value function under following the current evaluative policy.
     * @return the maximum single iteration change in the value function
     */
    @Override
    public int getTotalValueIterations() {
        return totalValueIterations;
    }

    /**
     * Plans from the input state and then returns a {@link burlap.behavior.policy.GreedyQPolicy} that greedily
     * selects the action with the highest Q-value and breaks ties uniformly randomly.
     * @param initialState the initial state of the planning problem
     * @return a {@link burlap.behavior.policy.GreedyQPolicy}.
     */
    @Override
    public GreedyQPolicy planFromState(State initialState) {

        int iterations = 0;
        if(this.performReachabilityFrom(initialState) || !this.hasRunPlanning){

            double delta;
            do{
                delta = this.evaluatePolicy();
                iterations++;
                this.evaluativePolicy = new GreedyQPolicy(this.getCopyOfValueFunction());
            }while(delta > this.maxPIDelta && iterations < maxPolicyIterations);

            this.hasRunPlanning = true;
            this.convergence = delta;
        }

        DPrint.cl(this.debugCode, "Total policy iterations: " + iterations + ", delta: " + this.convergence);
        this.totalPolicyIterations += iterations;

        return (GreedyQPolicy)this.evaluativePolicy;

    }
}
