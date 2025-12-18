import java.util.*;

public class RRScheduler {
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

    public Vector<String> getExecutionOrder() {
        return executionOrder;
    }


    public void simulate() {
        Queue<Process> finishedProcesses = new LinkedList<>();
        Queue<Process> readyProcesses = new LinkedList<>();
        Process currentProcess = null;
        // could be replaced with a do-while
        //==============================================
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
        //================================================

        while (!readyProcesses.isEmpty()) {
            currentProcess = readyProcesses.poll();
            // processing simulation
            int currentProcessTime = Math.min(currentProcess.getRemainingTime(), quantum);
            for (int j = 0; j < readyProcesses.size(); j++) {
                Process temp = readyProcesses.poll();
                temp.setTurnaroundTime(temp.getTurnaroundTime() + currentProcessTime + contextSwitching);
                temp.setWaitingTime(temp.getWaitingTime() + currentProcessTime + contextSwitching);
                readyProcesses.add(temp);
            }
            for (int i = 0; i < currentProcessTime; i++){
                currentTime++;
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                for (int j = 0; j < processes.size(); j++) {
                    if (processes.get(j).getArrivalTime() <= currentTime) {
                        readyProcesses.add(processes.get(j));
                        processes.remove(j);
                        j--;
                    }
                }
            }
            executionOrder.add(currentProcess.getName());
            if (currentProcess.getRemainingTime() > 0) readyProcesses.add(currentProcess);
            else {
                currentProcess.setTurnaroundTime(currentTime - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                finishedProcesses.add(currentProcess);
            }
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
        System.out.print("    Execution Order: ");
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

        double avgTurnAroundTime = totalTurnAroundTime / numProcesses;
        double avgWaitingTime = totalWaitingTime / numProcesses;

    }
}


