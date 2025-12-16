public class Process {
    private String name;
    private int arrivalTime;
    private int burstTime;
    private int priority;

    private int remainingTime;
    private int waitingTime;
    private int turnaroundTime;
    private int completionTime;
    private int currentQuantum;

    public Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.currentQuantum = 0;
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

    public String toString() {
        return "Process{" + "name='" + name + '\'' + ", arrival=" + arrivalTime +
                ", burst=" + burstTime + ", priority=" + priority + '}';
    }
}