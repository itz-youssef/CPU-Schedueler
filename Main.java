import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Main {
    private class Process {
        private String id;
        private int burstTime;
        private int arrivalTime;
        private int priority;
        private int turnAroundTime;
        private int waitingTime;
        public Process(String id, int burstTime, int arrivalTime, int priority) {
            this.id = id;
            this.burstTime = burstTime;
            this.arrivalTime = arrivalTime;
            this.priority = priority;
            this.turnAroundTime = 0;
            this.waitingTime = 0;
        }
        public String getId() {return id;}
        public int getBurstTime() {return burstTime;}
        public int getArrivalTime() {return arrivalTime;}
        public int getPriority() {return priority;}
        public int getTurnAroundTime() {return turnAroundTime;}
        public int getWaitingTime() {return waitingTime;}
        public void setBurstTime(int burstTime) {this.burstTime = burstTime;}
        public void setTurnAroundTime(int turnAroundTime) {this.turnAroundTime = turnAroundTime;}
        public void setWaitingTime(int waitingTime) {this.waitingTime = waitingTime;}
        public void setArrivalTime(int arrivalTime) {this.arrivalTime = arrivalTime;}
        public void setPriority(int priority) {this.priority = priority;}
    }

    private class RRScheduler {
        private Queue<Process> processQueue;
        private int currentTime;
        private final int quantum;
        private int numProcesses;
        private int totalWaitingTime;
        public RRScheduler(ArrayList<Process> processes, int quantum) {
            this.processQueue = new LinkedList<>();
            this.totalWaitingTime = 0;
            // sorting on process arrival time
            this.numProcesses = processes.size();
            boolean swapped;
            for (int i = 0; i < numProcesses-1; i++) {
                swapped = false;
                for (int j = 0; j < numProcesses - 1 - i; j++) {
                    if (processes.get(j).getArrivalTime() > processes.get(j+1).getArrivalTime()) {
                        Process temp = processes.get(j);
                        processes.set(j, processes.get(j+1));
                        processes.set(j+1, temp);
                        swapped = true;
                    }
                }
                if (!swapped) break;
            }
            processQueue.addAll(processes);
            this.currentTime = 0;
            this.quantum = quantum;
        }
        public void simulate() {
            Process nextProcess;
            Process currentProcess = null;
            while (!processQueue.isEmpty()) {
                nextProcess = processQueue.peek();
                if (nextProcess.getArrivalTime() <= currentTime) {
                    currentProcess = processQueue.poll();
                }
                else if (currentProcess != null) {
                    currentTime = nextProcess.getArrivalTime();
                    continue;
                }
                int currentBurstTime = currentProcess.getBurstTime();
                for (int i = 0; i < Math.min(quantum, currentBurstTime); i++) {
                    System.out.println("Time: " + currentTime + ", processing " + currentProcess.getId());
                    currentTime++;
                    currentProcess.setBurstTime(currentProcess.getBurstTime()-1);
                }
                if (!processQueue.isEmpty()) System.out.println("Time: " + currentTime + ", Switching to Process " + processQueue.peek().getId());;
                if (currentProcess.getBurstTime() > 0) processQueue.add(currentProcess);
            }
            System.out.println("Time: " + currentTime + ", Finished All Processes!!!");
        }
    }
}
