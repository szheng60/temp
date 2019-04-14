package edu.gatech.cs7641.assignment4;

import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;

import java.util.Set;

public class VI extends ValueIteration {
    private double convergence;
    /**
     * Initializers the valueFunction.
     *
     * @param domain         the domain in which to plan
     * @param gamma          the discount factor
     * @param hashingFactory the state hashing factor to use
     * @param maxDelta       when the maximum change in the value function is smaller than this value, VI will terminate.
     * @param maxIterations  when the number of VI iterations exceeds this value, VI will terminate.
     */
    public VI(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta, int maxIterations) {
        super(domain, gamma, hashingFactory, maxDelta, maxIterations);
        this.convergence = 0.0;
    }

    public double getConvergence() {
        return this.convergence;
    }

    @Override
    public void runVI(){

        if(!this.foundReachableStates){
            throw new RuntimeException("Cannot run VI until the reachable states have been found. Use the planFromState or performReachabilityFrom method at least once before calling runVI.");
        }

        Set<HashableState> states = valueFunction.keySet();

        int i;
//        double convergence = 0.0;
        for(i = 0; i < this.maxIterations; i++){

            double delta = 0.;
            for(HashableState sh : states){

                double v = this.value(sh);
                double maxQ = this.performBellmanUpdateOn(sh);
                delta = Math.max(Math.abs(maxQ - v), delta);

            }
            this.convergence = delta;
            if(delta < this.maxDelta){
                break; //approximated well enough; stop iterating
            }

        }

        DPrint.cl(this.debugCode, "Passes: " + i + ", Convergence: " + this.convergence);

        this.hasRunVI = true;

    }
}
