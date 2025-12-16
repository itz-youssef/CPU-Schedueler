import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Process> processes = new ArrayList<>();

        System.out.println("--- CPU Scheduler Simulator ---");

        System.out.print("Enter Number of Processes: ");
        int numProcesses = scanner.nextInt();

        System.out.print("Enter Round Robin Time Quantum: ");
        int timeQuantum = scanner.nextInt();

        System.out.print("Enter Context Switching Time: ");
        int contextSwitching = scanner.nextInt();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("\n--- Enter details for Process " + (i + 1) + " ---");

            System.out.print("Process Name: ");
            String name = scanner.next();

            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();

            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            System.out.print("Priority (Number): ");
            int priority = scanner.nextInt();

            processes.add(new Process(name, arrivalTime, burstTime, priority));
        }


        System.out.println("\n--- Starting SJF Scheduler ---");

        SJFScheduler sjf = new SJFScheduler();

        sjf.schedule(processes, contextSwitching);

        System.out.println("\n--- Final Statistics ---");
        System.out.println("Process\tWaiting\tTurnaround");

        float totalWaiting = 0;
        float totalTurnaround = 0;

        for (Process p : processes) {
            System.out.println(p.getName() + "\t" + p.getWaitingTime() + "\t" + p.getTurnaroundTime());

            totalWaiting += p.getWaitingTime();
            totalTurnaround += p.getTurnaroundTime();
        }

        System.out.println("\nAverage Waiting Time: " + (totalWaiting / numProcesses));
        System.out.println("Average Turnaround Time: " + (totalTurnaround / numProcesses));


    }
}