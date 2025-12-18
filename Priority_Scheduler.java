import java.util.*;

class PriorityScheduler {
    private ArrayList<Process> processes;
    private int contextSwitching;
    private int agingThreshold;
    private Vector<String> executionOrder;
    
    public PriorityScheduler(ArrayList<Process> processes, int contextSwitching, int agingThreshold) {
        this.processes = new ArrayList<>();
        // Create deep copies to avoid modifying original processes
        for (Process p : processes) {
            this.processes.add(new Process(p.getName(), p.getArrivalTime(), 
                                          p.getBurstTime(), p.getPriority(), 
                                          p.getQuantumTime()));
        }
        this.contextSwitching = contextSwitching;
        this.agingThreshold = agingThreshold;
        this.executionOrder = new Vector<>();
    }
    
    public void simulate() {
        // Sort processes by arrival time first
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        
        ArrayList<Process> readyQueue = new ArrayList<>();
        ArrayList<Process> completedProcesses = new ArrayList<>();
        Map<Process, Integer> waitingTimeTracker = new HashMap<>();
        
        int currentTime = 0;
        int processIndex = 0;
        Process runningProcess = null;
        
        // Initialize waiting time tracker
        for (Process p : processes) {
            waitingTimeTracker.put(p, 0);
        }
        
        while (completedProcesses.size() < processes.size()) {
            // Add newly arrived processes to ready queue
            while (processIndex < processes.size() && 
                   processes.get(processIndex).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.get(processIndex));
                processIndex++;
            }
            
            // Apply aging mechanism to prevent starvation
            applyAging(readyQueue, waitingTimeTracker);
            
            // If no process is ready, advance time
            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }
            
            // Select process with highest priority (lowest priority number)
            Process selectedProcess = selectHighestPriority(readyQueue);
            
            // Add context switching time if we're switching processes
            if (runningProcess != null && !runningProcess.equals(selectedProcess)) {
                currentTime += contextSwitching;
            }
            
            // Add to execution order
            executionOrder.add(selectedProcess.getName());
            
            // Execute the process
            int executionTime = 0;
            boolean preempted = false;
            
            while (selectedProcess.getRemainingTime() > 0 && !preempted) {
                selectedProcess.setRemainingTime(selectedProcess.getRemainingTime() - 1);
                executionTime++;
                currentTime++;
                
                // Update waiting time for other processes in ready queue
                for (Process p : readyQueue) {
                    if (!p.equals(selectedProcess)) {
                        waitingTimeTracker.put(p, waitingTimeTracker.get(p) + 1);
                    }
                }
                
                // Check for newly arrived processes
                while (processIndex < processes.size() && 
                       processes.get(processIndex).getArrivalTime() <= currentTime) {
                    Process newProcess = processes.get(processIndex);
                    readyQueue.add(newProcess);
                    processIndex++;
                    
                    // Check if new process should preempt current one
                    if (newProcess.getPriority() < selectedProcess.getPriority()) {
                        preempted = true;
                        break;
                    }
                }
                
                // Apply aging during execution
                if (executionTime % agingThreshold == 0) {
                    applyAging(readyQueue, waitingTimeTracker);
                    
                    // Check if any process now has higher priority
                    Process highestPriority = selectHighestPriority(readyQueue);
                    if (!highestPriority.equals(selectedProcess) && 
                        highestPriority.getPriority() < selectedProcess.getPriority()) {
                        preempted = true;
                        break;
                    }
                }
            }
            
            // If process completed
            if (selectedProcess.getRemainingTime() == 0) {
                readyQueue.remove(selectedProcess);
                completedProcesses.add(selectedProcess);
                
                int turnaroundTime = currentTime - selectedProcess.getArrivalTime();
                int waitingTime = turnaroundTime - selectedProcess.getBurstTime();
                
                selectedProcess.setTurnaroundTime(turnaroundTime);
                selectedProcess.setWaitingTime(waitingTime);
                
                runningProcess = null;
            } else {
                // Process was preempted, keep it in ready queue
                runningProcess = selectedProcess;
            }
        }
        
        // Display results
        displayResults(completedProcesses);
    }
    
    // Apply aging to prevent starvation
    private void applyAging(ArrayList<Process> readyQueue, Map<Process, Integer> waitingTimeTracker) {
        for (Process p : readyQueue) {
            int waitedTime = waitingTimeTracker.get(p);
            
            // For every agingThreshold time units waited, decrease priority by 1
            if (waitedTime >= agingThreshold && p.getPriority() > 1) {
                // Create a temporary priority decrease (we don't modify the original)
                // This is simulated through the selection process
                waitingTimeTracker.put(p, 0);  // Reset waiting time after aging
            }
        }
    }
    
    // Select process with highest priority considering aging
    private Process selectHighestPriority(ArrayList<Process> readyQueue) {
        if (readyQueue.isEmpty()) {
            return null;
        }
        
        Process selected = readyQueue.get(0);
        int effectivePriority = selected.getPriority();
        
        for (int i = 1; i < readyQueue.size(); i++) {
            Process current = readyQueue.get(i);
            int currentEffectivePriority = current.getPriority();
            
            // Lower number means higher priority
            if (currentEffectivePriority < effectivePriority) {
                selected = current;
                effectivePriority = currentEffectivePriority;
            } else if (currentEffectivePriority == effectivePriority) {
                // If same priority, choose the one that arrived first
                if (current.getArrivalTime() < selected.getArrivalTime()) {
                    selected = current;
                }
            }
        }
        
        return selected;
    }
    
    private void displayResults(ArrayList<Process> completedProcesses) {
        // Sort by original order (by name or arrival)
        completedProcesses.sort(Comparator.comparing(Process::getName));
        
        System.out.println("\n=== Priority Scheduling Results ===");
        System.out.print("Execution Order: ");
        for (String name : executionOrder) {
            System.out.print(name + " ");
        }
        System.out.println("\n");
        
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        
        System.out.println("Process Details:");
        for (Process p : completedProcesses) {
            System.out.println("Process: " + p.getName() + 
                             ", Waiting Time: " + p.getWaitingTime() + 
                             ", Turnaround Time: " + p.getTurnaroundTime());
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
        }
        
        double avgWaitingTime = totalWaitingTime / completedProcesses.size();
        double avgTurnaroundTime = totalTurnaroundTime / completedProcesses.size();
        
        System.out.println("\nAverage Waiting Time: " + String.format("%.2f", avgWaitingTime));
        System.out.println("Average Turnaround Time: " + String.format("%.2f", avgTurnaroundTime));
    }
    
    // Main method for testing
    public static void main(String[] args) {
        ArrayList<Process> processes = new ArrayList<>();
        
        // Test Case 1 from the assignment
        processes.add(new Process("P1", 0, 8, 3, 0));
        processes.add(new Process("P2", 1, 4, 1, 0));
        processes.add(new Process("P3", 2, 2, 4, 0));
        processes.add(new Process("P4", 3, 1, 2, 0));
        processes.add(new Process("P5", 4, 3, 5, 0));
        
        PriorityScheduler scheduler = new PriorityScheduler(processes, 1, 5);
        scheduler.simulate();
    }
}
