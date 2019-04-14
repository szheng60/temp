package edu.gatech.cs7641.assignment4;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.options.EnvironmentOptionOutcome;
import burlap.behavior.singleagent.options.Option;
import burlap.behavior.valuefunction.ConstantValueFunction;
import burlap.behavior.valuefunction.QFunction;
import burlap.behavior.valuefunction.QValue;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;

public class QL extends QLearning {
    private double convergence;

    public QL(SADomain domain, double gamma, HashableStateFactory hashingFactory, double qInit, double learningRate, double epsilon) {
        super(domain, gamma, hashingFactory, qInit, learningRate);
        this.QLInit(domain, gamma, hashingFactory, new ConstantValueFunction(qInit), learningRate, new EpsilonGreedy(this, epsilon), Integer.MAX_VALUE);
    }

    public QL(SADomain domain, double gamma, HashableStateFactory hashingFactory, double qInit, double learningRate, int maxEpisodeSize) {
        super(domain, gamma, hashingFactory, qInit, learningRate, maxEpisodeSize);
    }

    public QL(SADomain domain, double gamma, HashableStateFactory hashingFactory, double qInit, double learningRate, Policy learningPolicy, int maxEpisodeSize) {
        super(domain, gamma, hashingFactory, qInit, learningRate, learningPolicy, maxEpisodeSize);
    }

    public QL(SADomain domain, double gamma, HashableStateFactory hashingFactory, QFunction qInit, double learningRate, Policy learningPolicy, int maxEpisodeSize) {
        super(domain, gamma, hashingFactory, qInit, learningRate, learningPolicy, maxEpisodeSize);
    }

    public double getConvergence() {
        return this.convergence;
    }

    @Override
    public Episode runLearningEpisode(Environment env, int maxSteps) {

        State initialState = env.currentObservation();

        Episode ea = new Episode(initialState);
        HashableState curState = this.stateHash(initialState);
        eStepCounter = 0;

        maxQChangeInLastEpisode = 0.;
        while(!env.isInTerminalState() && (eStepCounter < maxSteps || maxSteps == -1)){

            Action action = learningPolicy.action(curState.s());
            QValue curQ = this.getQ(curState, action);



            EnvironmentOutcome eo;
            if(!(action instanceof Option)){
                eo = env.executeAction(action);
            }
            else{
                eo = ((Option)action).control(env, this.gamma);
            }



            HashableState nextState = this.stateHash(eo.op);
            double maxQ = 0.;

            if(!eo.terminated){
                maxQ = this.getMaxQ(nextState);
            }

            //manage option specifics
            double r = eo.r;
            double discount = eo instanceof EnvironmentOptionOutcome ? ((EnvironmentOptionOutcome)eo).discount : this.gamma;
            int stepInc = eo instanceof EnvironmentOptionOutcome ? ((EnvironmentOptionOutcome)eo).numSteps() : 1;
            eStepCounter += stepInc;

            if(!(action instanceof Option) || !this.shouldDecomposeOptions){
                ea.transition(action, nextState.s(), r);
            }
            else{
                ea.appendAndMergeEpisodeAnalysis(((EnvironmentOptionOutcome)eo).episode);
            }



            double oldQ = curQ.q;

            //update Q-value
            curQ.q = curQ.q + this.learningRate.pollLearningRate(this.totalNumberOfSteps, curState.s(), action) * (r + (discount * maxQ) - curQ.q);

            double deltaQ = Math.abs(oldQ - curQ.q);
            if(deltaQ > maxQChangeInLastEpisode){
                maxQChangeInLastEpisode = deltaQ;
            }

            //move on polling environment for its current state in case it changed during processing
            curState = this.stateHash(env.currentObservation());
            this.totalNumberOfSteps++;

            this.convergence = maxQChangeInLastEpisode;
        }


        return ea;

    }
}
