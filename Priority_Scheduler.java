import java.util.*;

class PriorityScheduler {
    private final List<Process> referenceList;
    private final List<Process> taskPool;
    private final int contextSwitchPenalty;
    private final int agingInterval;
    private final Vector<String> executionTimeline;

    public PriorityScheduler(ArrayList<Process> processes, int penalty, int interval) {
        this.referenceList = processes;
        this.contextSwitchPenalty = penalty;
        this.agingInterval = interval;
        this.executionTimeline = new Vector<>();
        this.taskPool = new ArrayList<>();

        for (Process p : processes) {
            Process task = new Process(p.getName(), p.getArrivalTime(), p.getBurstTime(), p.getPriority(), p.getQuantumTime());
            task.lastActivityTime = p.getArrivalTime();
            this.taskPool.add(task);
        }
    }

    public void simulate() {
        int clock = 0;
        String currentActiveName = null;

        while (isTaskPending()) {
            List<Process> availableTasks = getAvailableTasks(clock);

            if (availableTasks.isEmpty()) {
                clock++;
                continue;
            }

            applyAgingPolicy(availableTasks, clock);

            Process target = findBestTask(availableTasks);

            if (currentActiveName != null && !currentActiveName.equals(target.getName())) {
                updateTimeline(target.getName());
                target.lastActivityTime = clock;
                clock += contextSwitchPenalty;

                availableTasks = getAvailableTasks(clock);
                applyAgingPolicy(availableTasks, clock);
                Process reEvaluated = findBestTask(availableTasks);

                if (!target.getName().equals(reEvaluated.getName())) {
                    reEvaluated.lastActivityTime = clock;
                    clock += contextSwitchPenalty;

                    availableTasks = getAvailableTasks(clock);
                    applyAgingPolicy(availableTasks, clock);
                    target = findBestTask(availableTasks);
                    currentActiveName = null;
                } else {
                    currentActiveName = target.getName();
                }
            }

            if (currentActiveName == null || !currentActiveName.equals(target.getName())) {
                updateTimeline(target.getName());
            }

            target.setRemainingTime(target.getRemainingTime() - 1);
            clock++;
            target.lastActivityTime = clock;
            currentActiveName = target.getName();

            if (target.getRemainingTime() == 0) {
                recordFinalMetrics(target, clock);
            }
        }
        syncOriginalData();
    }

    private List<Process> getAvailableTasks(int time) {
        List<Process> ready = new ArrayList<>();
        for (Process p : taskPool) {
            if (p.getArrivalTime() <= time && p.getRemainingTime() > 0) {
                ready.add(p);
            }
        }
        return ready;
    }

    private void applyAgingPolicy(List<Process> readyPool, int currentTime) {
        if (agingInterval <= 0) return;
        for (Process p : readyPool) {
            int idleTime = currentTime - p.lastActivityTime;
            if (idleTime != 0 && idleTime % agingInterval == 0) {
                if (p.getPriority() > 1) {
                    p.setPriority(p.getPriority() - 1);
                }
            }
        }
    }

    private Process findBestTask(List<Process> pool) {
        Process best = pool.get(0);
        for (Process p : pool) {
            if (p.getPriority() < best.getPriority()) {
                best = p;
            } else if (p.getPriority() == best.getPriority()) {
                if (p.getArrivalTime() < best.getArrivalTime()) {
                    best = p;
                } else if (p.getArrivalTime() == best.getArrivalTime()) {
                    if (p.getName().compareTo(best.getName()) < 0) {
                        best = p;
                    }
                }
            }
        }
        return best;
    }

    private void updateTimeline(String name) {
        if (executionTimeline.isEmpty() || !executionTimeline.lastElement().equals(name)) {
            executionTimeline.add(name);
        }
    }

    private boolean isTaskPending() {
        for (Process p : taskPool) {
            if (p.getRemainingTime() > 0) return true;
        }
        return false;
    }

    private void recordFinalMetrics(Process task, int completionTime) {
        int ta = completionTime - task.getArrivalTime();
        int wait = ta - task.getBurstTime();
        task.setTurnaroundTime(ta);
        task.setWaitingTime(wait);
    }

    private void syncOriginalData() {
        for (Process original : referenceList) {
            for (Process sim : taskPool) {
                if (original.getName().equals(sim.getName())) {
                    original.setWaitingTime(sim.getWaitingTime());
                    original.setTurnaroundTime(sim.getTurnaroundTime());
                    break;
                }
            }
        }
    }

    public Vector<String> getExecutionOrder() {
        return executionTimeline;
    }
}