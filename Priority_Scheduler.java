import java.util.*;

class PriorityScheduler {
    private ArrayList<Process> originalProcesses;
    private ArrayList<Process> simulationProcesses;
    private int contextSwitching;
    private int agingThreshold;
    private Vector<String> executionOrder;

    public PriorityScheduler(ArrayList<Process> processes,
                             int contextSwitching,
                             int agingThreshold) {

        this.originalProcesses = processes;
        this.contextSwitching = contextSwitching;
        this.agingThreshold = agingThreshold;
        this.executionOrder = new Vector<>();

        this.simulationProcesses = new ArrayList<>();
        for (Process p : processes) {
            this.simulationProcesses.add(new Process(
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getPriority(),
                    p.getQuantumTime()
            ));
        }
    }

    public void simulate() {
        simulationProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        ArrayList<Process> readyQueue = new ArrayList<>();
        ArrayList<Process> completed = new ArrayList<>();
        Map<Process, Integer> waitingTracker = new HashMap<>();

        int currentTime = 0;
        int index = 0;
        Process lastRunningProcess = null;

        for (Process p : simulationProcesses) {
            waitingTracker.put(p, 0);
        }

        while (completed.size() < simulationProcesses.size()) {

            while (index < simulationProcesses.size() && simulationProcesses.get(index).getArrivalTime() <= currentTime) {
                readyQueue.add(simulationProcesses.get(index));
                index++;
            }

            applyAging(readyQueue, waitingTracker);

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process selected = selectHighestPriority(readyQueue);

            if (lastRunningProcess != null && selected != lastRunningProcess) {
                currentTime += contextSwitching;
                updateWaitingTimes(readyQueue, null, contextSwitching, waitingTracker);

                while (index < simulationProcesses.size() && simulationProcesses.get(index).getArrivalTime() <= currentTime) {
                    readyQueue.add(simulationProcesses.get(index));
                    index++;
                }
            }

            if (lastRunningProcess != selected) {
                executionOrder.add(selected.getName());
            }

            selected.setRemainingTime(selected.getRemainingTime() - 1);
            currentTime++;

            updateWaitingTimes(readyQueue, selected, 1, waitingTracker);

            if (selected.getRemainingTime() == 0) {
                readyQueue.remove(selected);
                completed.add(selected);

                int turnaround = currentTime - selected.getArrivalTime();
                int waiting = turnaround - selected.getBurstTime();

                selected.setTurnaroundTime(turnaround);
                selected.setWaitingTime(waiting);
            }

            lastRunningProcess = selected;
        }

        for (Process original : originalProcesses) {
            for (Process sim : completed) {
                if (original.getName().equals(sim.getName())) {
                    original.setWaitingTime(sim.getWaitingTime());
                    original.setTurnaroundTime(sim.getTurnaroundTime());
                    break;
                }
            }
        }
    }

    private void updateWaitingTimes(ArrayList<Process> readyQueue, Process currentRunning, int timeAmount, Map<Process, Integer> waitingTracker) {
        for (Process p : readyQueue) {
            if (p != currentRunning) {
                waitingTracker.put(p, waitingTracker.get(p) + timeAmount);
            }
        }
    }

    private void applyAging(ArrayList<Process> readyQueue, Map<Process, Integer> waitingTracker) {
        for (Process p : readyQueue) {
            int waited = waitingTracker.get(p);
            if (waited >= agingThreshold) {
                if (p.getPriority() > 1) {
                    p.setPriority(p.getPriority() - 1);
                    waitingTracker.put(p, 0);
                }
            }
        }
    }

    private Process selectHighestPriority(ArrayList<Process> readyQueue) {
        Process best = readyQueue.get(0);
        for (Process p : readyQueue) {
            if (p.getPriority() < best.getPriority()) {
                best = p;
            } else if (p.getPriority() == best.getPriority()) {
                if (p.getArrivalTime() < best.getArrivalTime()) {
                    best = p;
                }
            }
        }
        return best;
    }

    public Vector<String> getExecutionOrder() {
        return executionOrder;
    }
}