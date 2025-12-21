import java.util.*;

public class AGScheduler {

    List<String> execution_Order = new ArrayList<>();
    List<Process> finished_Processes = new ArrayList<>();

    // Sort by arrival time
    private Process[] sortTHEArrivalTime(Process[] processes) {

        Arrays.sort(processes, Comparator.comparingInt(Process::getArrivalTime));
        return processes;

    }

    //  Phase 2
    private Process get_Best_Priority(List<Process> waitingQueue) {

        if (waitingQueue.isEmpty()) return null;

        Process best_Process = waitingQueue.get(0);

        for (Process process : waitingQueue) {
            if (process.getPriority() < best_Process.getPriority()) {
                best_Process = process;
            }
        }

        return best_Process;
    }

    // Phase 3
    private Process getShortestJob(List<Process> waitingQueue) {

        if (waitingQueue.isEmpty()) return null;

        Process shortestJob = waitingQueue.get(0);

        for (Process process : waitingQueue) {

            if (process.getRemainingTime() < shortestJob.getRemainingTime()) {

                shortestJob = process;

            }
        }

        return shortestJob;
    }

    private void printINFO(List<Process> finishedProcesses, List<String> executionOrder) {

        finishedProcesses.sort(Comparator.comparing(Process::getName));

        double avgWaitingTime = 0, avgTurnaroundTime = 0;

        for (Process process : finishedProcesses) {

            avgWaitingTime += process.getWaitingTime();
            avgTurnaroundTime += process.getTurnaroundTime();

        }


        double final_Avg_Wait = avgWaitingTime / finishedProcesses.size();
        double final_Avg_Turn = avgTurnaroundTime / finishedProcesses.size();

    }

    public void updateQuantum(Process p, int time_executed, int scenario) {

        int remainingQuantum = p.getQuantumTime() - time_executed;
        int newQuantum;

        newQuantum = switch (scenario) {

            case 1 -> p.getQuantumTime() + 2;
            case 2 -> p.getQuantumTime() + (int) Math.ceil(remainingQuantum / 2.0);
            case 3 -> p.getQuantumTime() + remainingQuantum;
            case 4 -> 0;
            default -> p.getQuantumTime();

        };
        p.addQuantumToHistory(newQuantum);


        p.setQuantumTime(newQuantum);
    }

    public void StartSimulation(Process[] processes) {

        int currentTIME = 0;
        List<Process> readyQueue = new ArrayList<>();
        sortTHEArrivalTime(processes);
        int i = 0;

        Process nextProcess = null;
        for(Process p : processes ){
            p.addQuantumToHistory(p.getQuantumTime());
        }

        while (finished_Processes.size() < processes.length) {
            while (i < processes.length && processes[i].getArrivalTime() <= currentTIME) {

                readyQueue.add(processes[i]);
                i++;

            }

            if (readyQueue.isEmpty()) { // to check if the first arrival time not = 0 so we will start from it

                if (i < processes.length) {

                    currentTIME = processes[i].getArrivalTime();
                }

                continue;
            }

            Process p;

            if(nextProcess != null){

                p = nextProcess;
                nextProcess = null;

            }

            else{

                p = readyQueue.remove(0);
            }

            execution_Order.add(p.getName());

            int q = p.getQuantumTime();
            int q1 = (int) Math.ceil(0.25 * q); //calc the phase 1 n 2 times for the curr process
            int q2 = (int) Math.ceil(0.25 * q);

            Boolean is_preempted = false;

            int runtime = 0;                   // track the run time for each phase

            while (runtime < q1 && p.getRemainingTime() > 0) {

                p.adjustRemainingTime(); // decrement the p's remaining time by 1 _> p--
                currentTIME++;
                runtime++;

                while (i < processes.length && processes[i].getArrivalTime() <= currentTIME) { // to check if another process comes when the first process being excuted

                    readyQueue.add(processes[i]);
                    i++;

                }

            }

            if (p.getRemainingTime() == 0) {

                updateQuantum(p, runtime, 4); // set quantum time to 0 if its finished
                finished_Processes.add(p);
                p.turnaroundTime = currentTIME - p.getArrivalTime();
                p.waitingTime = p.turnaroundTime - p.burstTime;
                continue;

            }




            Process higherpriority = get_Best_Priority(readyQueue); // get the p's less priority value and it return null if the RQ was empty
            while (runtime < q1 + q2 && p.remainingTime > 0) {

                if (higherpriority != null && higherpriority.priority < p.priority) { // if there is a process has priority value less than the excuted then adjust its quantum time and add it to RQ

                    updateQuantum(p, runtime, 2);
                    readyQueue.add(p);

                    if(readyQueue.get(0) != higherpriority) {

                        readyQueue.remove(higherpriority); //remove the process that will executed from the RQ
                        nextProcess = higherpriority;

                    }

                    is_preempted = true;
                    break;
                }


                p.remainingTime--; // because the condition in loop runtime < q1 so there still a 1 time not executed
                currentTIME++;
                runtime++;


                while (i < processes.length && processes[i].arrivalTime <= currentTIME) {

                    readyQueue.add(processes[i]);
                    i++;

                }
            }

            if(is_preempted) continue;

            if (p.remainingTime == 0) {

                updateQuantum(p, runtime, 4);
                finished_Processes.add(p);
                p.turnaroundTime = currentTIME - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
                continue;

            }

            while (runtime < q && runtime >= q1 + q2 && p.remainingTime > 0) { // phase 3 starting check

                Process shortest = getShortestJob(readyQueue);
                if (shortest != null && shortest.remainingTime < p.remainingTime) {

                    updateQuantum(p, runtime, 3);
                    readyQueue.add(p);

                    if(readyQueue.get(0) != shortest) {

                        readyQueue.remove(shortest);
                        nextProcess = shortest;

                    }

                    is_preempted = true;
                    break;


                }

                p.remainingTime--;
                currentTIME++;
                runtime++;


                while (i < processes.length && processes[i].arrivalTime <= currentTIME) {
                    readyQueue.add(processes[i]);
                    i++;
                }
            }

            if(is_preempted) continue;

            if (p.remainingTime == 0) {

                updateQuantum(p, runtime, 4);
                finished_Processes.add(p);
                p.turnaroundTime = currentTIME - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;

            }
            else if (runtime == q && p.remainingTime > 0) {

                updateQuantum(p, runtime, 1);
                readyQueue.add(p);
            }
        }

    }

    public List<String> getExecutionOrder() {
        return execution_Order;
    }

    public List<Process> getFinishedProcesses() {
        return finished_Processes;
    }
}
