public class Process {
    private String name;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int quantumTime;
    private int quantumUsed;


    private int remainingTime;
    private int waitingTime;
    private int turnaroundTime;
    private int completionTime;
    private int currentQuantum;
    private int phase;

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantumTime ) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.currentQuantum = 0;
        this.quantumTime = quantumTime;
        this.quantumUsed = 0;
        this.phase = 1;

    }

    public void updatePhase() {
        if (quantumUsed >= 0.25 * quantumTime && quantumUsed < 0.50 * quantumTime) {
            this.phase = 2;  // Switch to Non-Preemptive Priority
        } else if (quantumUsed >= 0.50 * quantumTime) {
            this.phase = 3;  // Switch to Preemptive SJF
        }
    }


    public void adjustQuantumTime(int remainingQuantumTime) {
//        int remainingQuantumTime = quantumTime - quantumUsed;
        if (this.phase == 1) {
        } else if (this.phase == 2) {
            this.quantumTime = quantumTime +(int) Math.ceil(remainingQuantumTime / 2.0);
        } else if (this.phase == 3) {
            // Preemptive SJF: Set quantum time to remaining time
            this.quantumTime = quantumTime +remainingQuantumTime;
        }
    }
    public void adjustBurstTime(int quantumUsed) {
        this.remainingTime = remainingTime - quantumUsed;
    }


    public String getName() { return name; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }

    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }

    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }

    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }

    public int getCurrentQuantum() { return currentQuantum; }
    public void setCurrentQuantum(int q) { this.currentQuantum = q; }

    public int getQuantumTime() { return quantumTime;}
    public void setQuantumTime(int quantumTime) {this.quantumTime = quantumTime;}

    public int getQuantumUsed() { return quantumUsed; }
    public void setQuantumUsed(int q) { this.quantumUsed = q; }


    public String toString() {
        return "Process{" + "name='" + name + '\'' + ", arrival=" + arrivalTime +
                ", burst=" + burstTime + ", priority=" + priority + '}';
    }

    public void executeProcess(int time) {
        this.quantumUsed += time;  // Add executed time to quantumUsed
        this.remainingTime -= time;  // Reduce remaining burst time
    }

}


