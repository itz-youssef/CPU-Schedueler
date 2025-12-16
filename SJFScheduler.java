import java.util.*;
public class SJFScheduler {
    public void schedule(ArrayList<Process> processes, int contextSwitchingTime){
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(Process::getRemainingTime)
        );

        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        Process lastProcess = null;
        int processIndex = 0;

        while (completed < n){
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
            }
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
            currentTime++;
            lastProcess = currentProcess;

            if (currentProcess.getRemainingTime() == 0) {
                readyQueue.poll();
                completed++;

                int finishTime = currentTime;
                int turnAround = finishTime - currentProcess.getArrivalTime();
                int waiting = turnAround - currentProcess.getBurstTime();

                currentProcess.setTurnaroundTime(turnAround);
                currentProcess.setWaitingTime(waiting);

                System.out.print(currentProcess.getName() + " -> ");
            }
        }
    }
}
