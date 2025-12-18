import java.util.*;

public class SJFScheduler {
    public ArrayList<String> executionOrder = new ArrayList<>();

    public void schedule(ArrayList<Process> processes, int contextSwitchingTime) {
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(Process::getRemainingTime)
                        .thenComparingInt(Process::getArrivalTime)
        );

        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        Process lastProcess = null;
        int processIndex = 0;

        executionOrder.clear();

        while (completed < n) {
            while (processIndex < n && processes.get(processIndex).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.get(processIndex));
                processIndex++;
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process currentProcess = readyQueue.peek();

            if (lastProcess != null && currentProcess != lastProcess) {
                currentTime += contextSwitchingTime;
                while (processIndex < n && processes.get(processIndex).getArrivalTime() <= currentTime) {
                    readyQueue.add(processes.get(processIndex));
                    processIndex++;
                }
            }

            readyQueue.remove(currentProcess);

            if (executionOrder.isEmpty() || !executionOrder.get(executionOrder.size() - 1).equals(currentProcess.getName())) {
                executionOrder.add(currentProcess.getName());
            }


            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
            currentTime++;
            lastProcess = currentProcess;

            if (currentProcess.getRemainingTime() == 0) {
                completed++;
                int finishTime = currentTime;
                int turnAround = finishTime - currentProcess.getArrivalTime();
                int waiting = turnAround - currentProcess.getBurstTime();

                currentProcess.setTurnaroundTime(turnAround);
                currentProcess.setWaitingTime(waiting);
            } else {
                readyQueue.add(currentProcess);
            }
        }
    }

    public ArrayList<String> getExecutionOrder() {
        return executionOrder;
    }
}