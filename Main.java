import java.util.*;

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
        private ArrayList<Process> processes;
        private int currentTime;
        private final int quantum;
        private final int contextSwitching;
        private final double numProcesses;
        private Vector<String> executionOrder;

        public RRScheduler(ArrayList<Process> processes, int quantum, int contextSwitching) {
            this.processes = new ArrayList<>(processes);
            this.executionOrder = new Vector<>();
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
            this.currentTime = 0;
            this.quantum = quantum;
            this.contextSwitching = contextSwitching;
        }
        public void simulate() {
            Queue<Process> finishedProcesses = new LinkedList<>();
            Queue<Process> readyProcesses = new LinkedList<>();
            Process nextProcess;
            Process currentProcess = null;
            for (int i = 0; i < numProcesses; i++) {
                Process temp = processes.get(i);
                temp.setTurnAroundTime(temp.getArrivalTime() * (-1));
                temp.setWaitingTime(temp.getArrivalTime() * (-1));
                processes.set(i, temp);
            }
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).getArrivalTime() <= currentTime) {
                    readyProcesses.add(processes.get(i));
                    processes.remove(i);
                    i--;
                }
            }
            while (readyProcesses.isEmpty()){
                currentTime++;
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).getArrivalTime() <= currentTime) {
                        readyProcesses.add(processes.get(i));
                        processes.remove(i);
                        i--;
                    }
                }
            }

            while (!readyProcesses.isEmpty()) {
                currentProcess = readyProcesses.poll();
                // processing simulation
                int currentProcessTime = Math.min(currentProcess.getBurstTime(), quantum);
                for (int j = 0; j < readyProcesses.size(); j++) {
                    Process temp = readyProcesses.poll();
                    temp.setTurnAroundTime(temp.getTurnAroundTime() + currentProcessTime + contextSwitching);
                    temp.setWaitingTime(temp.getWaitingTime() + currentProcessTime + contextSwitching);
                    readyProcesses.add(temp);
                }
                for (int i = 0; i < currentProcessTime; i++){
                    currentTime++;
                    for (int j = 0; j < processes.size(); j++) {
                        if (processes.get(j).getArrivalTime() <= currentTime) {
                            readyProcesses.add(processes.get(j));
                            processes.remove(j);
                            j--;
                        }
                    }
                }
                executionOrder.add(currentProcess.getId());
                if (currentProcess.getBurstTime() > 0) readyProcesses.add(currentProcess);
                else finishedProcesses.add(currentProcess);
                currentTime += contextSwitching;
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).getArrivalTime() <= currentTime) {
                        readyProcesses.add(processes.get(i));
                        processes.remove(i);
                        i--;
                    }
                }
            }
            double totalWaitingTime = 0;
            double totalTurnAroundTime = 0;
            System.out.print("Execution Order: ");
            for (String s : executionOrder) {
                System.out.print(s + " ");
            }
            System.out.println();
            // sorting the processes
            ArrayList<Process> sortedProcesses = new ArrayList<>(finishedProcesses);
            boolean swapped;
            for (int i = 0; i < numProcesses-1; i++) {
                swapped = false;
                for (int j = 0; j < numProcesses - 1 - i; j++) {
                    if (sortedProcesses.get(j).getArrivalTime() > sortedProcesses.get(j+1).getArrivalTime()) {
                        Process temp = sortedProcesses.get(j);
                        sortedProcesses.set(j, sortedProcesses.get(j+1));
                        sortedProcesses.set(j+1, temp);
                        swapped = true;
                    }
                }
                if (!swapped) break;
            }

            for (int i = 0; i < numProcesses; i++) {
                Process temp = sortedProcesses.get(i);
                totalWaitingTime += temp.getWaitingTime();
                totalTurnAroundTime += temp.getTurnAroundTime();
                System.out.println("Process: " + temp.getId() + ", Waiting time: " + temp.getWaitingTime() + ", Turn around time: " + temp.getTurnAroundTime());
            }
            double avgTurnAroundTime = totalTurnAroundTime / numProcesses;
            double avgWaitingTime = totalWaitingTime / numProcesses;
            System.out.println("AVG Waiting time: " + avgWaitingTime + ", AVG Turn around time: " + avgTurnAroundTime);
        }
    }
    public void main(String[] args) {
        Process p1 = new Process("p1", 8, 0, 1);
        Process p2 = new Process("p2", 4, 1, 1);
        Process p3 = new Process("p3", 2, 2, 1);
        Process p4 = new Process("p4", 1, 3, 1);
        Process p5 = new Process("p5", 3, 4, 1);
        ArrayList<Process> processes = new ArrayList<>();
        processes.add(p1);
        processes.add(p2);
        processes.add(p3);
        processes.add(p4);
        processes.add(p5);
        RRScheduler scheduler = new RRScheduler(processes, 2, 1);
        scheduler.simulate();
    }
}
