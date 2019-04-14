package edu.gatech.cs7641.assignment4.artifacts;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Analysis {
	private HashMap<Integer, Result> results;

	public Analysis() {
		this.results = new HashMap<Integer, Result>();
	}

	public void add(int episode, List<Double> rewardSequence, int steps, long milliseconds) {
		Result result = new Result(0, steps, milliseconds);
		rewardSequence.forEach(new Consumer<Double>() {

			@Override
			public void accept(Double t) {
				result.reward += t;
			}
		});

		this.results.put(episode, result);
	}

	public void add(int episode, List<Double> rewardSequence, int steps, long milliseconds, double lastDelta) {
		Result result = new Result(0, steps, milliseconds, lastDelta);
		rewardSequence.forEach(new Consumer<Double>() {

			@Override
			public void accept(Double t) {
				result.reward += t;
			}
		});

		this.results.put(episode, result);
	}
	
	public void print() {
		System.out.println("Episode, Steps, Reward, Time (in milliseconds), Convergence");
		double totalReward = 0.0;
		int totalSteps = 0;
		long totalMilliseconds = 0;
		int minSteps = Integer.MAX_VALUE;
		
		for (Integer episodeIndex : this.results.keySet()) {
			Result result = this.results.get(episodeIndex);

			totalReward += result.reward;
			totalSteps += result.steps;
			totalMilliseconds += result.milliseconds;
			
			if (result.steps < minSteps) {
				minSteps = result.steps;
			}
			System.out.println(episodeIndex + ", " + result.steps + ", " + result.reward + ", " + result.milliseconds + ", " + result.lastDelta);
		}
		System.out.println("\nAverage Reward: " + totalReward / this.results.size());
		System.out.println("Average Number of Steps: " + totalSteps / this.results.size());
		System.out.println("Minimum Number of Steps: " + minSteps);
		System.out.println("Average Time (in milliseconds): " + totalMilliseconds / this.results.size());
	}

    public void print(String fileName) {
        String title = "Episode, Steps, Reward, Time (in milliseconds), Convergence";
        List<String> data_arr = new ArrayList<>();
        data_arr.add(title);
        for (Integer episodeIndex : this.results.keySet()) {
            Result result = this.results.get(episodeIndex);
            data_arr.add(episodeIndex + ", " + result.steps + ", " + result.reward + ", " + result.milliseconds + ", " + result.lastDelta);
        }
        writeToFile(fileName, data_arr);
    }

	public void writeToFile(String fileName, List<String> content) {
	    FileWriter fw = null;
	    try {
	        fw = new FileWriter(fileName + ".txt", true);
	        for(String line: content) {
	            fw.write(line);
	            fw.write("\r\n");
            }
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
	        if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	public HashMap<Integer, Result> getResults() {
		return this.results;
	}

	public class Result {
		public double reward;
		public int steps;
		public long milliseconds;
		public double lastDelta;

		public Result(double reward, int steps, long milliseconds) {
			this.reward = reward;
			this.steps = steps;
			this.milliseconds = milliseconds;
		}
		public Result(double reward, int steps, long milliseconds, double lastDelta) {
			this.reward = reward;
			this.steps = steps;
			this.milliseconds = milliseconds;
			this.lastDelta = lastDelta;
		}
	}

}
