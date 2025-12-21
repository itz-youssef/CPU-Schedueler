import java.util.*;

public class Main {

    // =============================================================
    // Helper Methods (Validation & Printing)
    // =============================================================

    public static boolean validateResults(String testName, List<String> actualOrder, ArrayList<Process> actualProcesses, Output expectedOutput) {
        boolean passed = true;
        System.out.println("     Validating Results for: " + testName);

        // 1. (Execution Order)
        if (!actualOrder.equals(expectedOutput.getProcessesOrder())) {
            System.out.println("         Order Mismatch!");
            System.out.println("         Expected: " + expectedOutput.getProcessesOrder());
            System.out.println("         Actual:   " + actualOrder);
            passed = false;
        }

        // 2. (Process Details)
        for (Process act : actualProcesses) {
            outputprocess exp = null;
            for (outputprocess op : expectedOutput.getValidProcesses()) {
                if (op.getName().equals(act.getName())) {
                    exp = op;
                    break;
                }
            }

            if (exp != null) {
                // Check Waiting Time and Turnaround Time
                if (act.getWaitingTime() != exp.getWaitingTime() || act.getTurnaroundTime() != exp.getTurnaroundTime()) {
                    System.out.println("         Data Mismatch for " + act.getName() + ":");
                    System.out.println("         Expected -> WT: " + exp.getWaitingTime() + ", TAT: " + exp.getTurnaroundTime());
                    System.out.println("         Actual   -> WT: " + act.getWaitingTime() + ", TAT: " + act.getTurnaroundTime());
                    passed = false;
                }

                // Check Quantum History (Specific for AG)
                if (exp.get_Quantum_History() != null && !exp.get_Quantum_History().isEmpty()) {
                    if (!act.getQuantumHistory().equals(exp.get_Quantum_History())) {
                        System.out.println("         Quantum History Mismatch for " + act.getName() + ":");
                        System.out.println("         Expected: " + exp.get_Quantum_History());
                        System.out.println("         Actual:   " + act.getQuantumHistory());
                        passed = false;
                    }
                }
            }
        }

        // 3. (Averages)
        double totalWT = 0, totalTAT = 0;
        for (Process p : actualProcesses) {
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }

        double actualAvgWT = actualProcesses.isEmpty() ? 0 : totalWT / actualProcesses.size();
        double actualAvgTAT = actualProcesses.isEmpty() ? 0 : totalTAT / actualProcesses.size();

        if (Math.abs(actualAvgWT - expectedOutput.getAverageWaitingTime()) > 0.01) {
            System.out.printf("      Avg Waiting Time Mismatch! Expected: %.2f, Actual: %.2f%n",
                    expectedOutput.getAverageWaitingTime(), actualAvgWT);
            passed = false;
        }

        if (Math.abs(actualAvgTAT - expectedOutput.getAverageTurnaroundTime()) > 0.01) {
            System.out.printf("      Avg Turnaround Time Mismatch! Expected: %.2f, Actual: %.2f%n",
                    expectedOutput.getAverageTurnaroundTime(), actualAvgTAT);
            passed = false;
        }

        return passed;
    }

    public static void printStats(ArrayList<Process> processes) {
        if (processes.isEmpty()) return;

        ArrayList<Process> sortedList = new ArrayList<>(processes);
        sortedList.sort(Comparator.comparing(Process::getName));

        double totalWaiting = 0;
        double totalTurnaround = 0;

        System.out.println("\n   📊 Process Statistics:");
        System.out.println("   +------+--------------+-----------------+---------------------------+");
        System.out.println("   | Name | Waiting Time | Turnaround Time | Quantum History           |");
        System.out.println("   +------+--------------+-----------------+---------------------------+");

        for (Process p : sortedList) {
            String history = (p.getQuantumHistory() != null) ? p.getQuantumHistory().toString() : "[]";
            System.out.printf("   | %-4s | %-12d | %-15d | %-25s |%n",
                    p.getName(), p.getWaitingTime(), p.getTurnaroundTime(), history);

            totalWaiting += p.getWaitingTime();
            totalTurnaround += p.getTurnaroundTime();
        }
        System.out.println("   +------+--------------+-----------------+---------------------------+");

        double avgWaiting = totalWaiting / processes.size();
        double avgTurnaround = totalTurnaround / processes.size();
        System.out.printf("   ➤ Average Waiting Time    : %.2f%n", avgWaiting);
        System.out.printf("   ➤ Average Turnaround Time : %.2f%n", avgTurnaround);
    }

    // =============================================================
    // Scheduler Specific Runners
    // =============================================================

    public static void runSJF(List<String> fileNames) {
        System.out.println("\n>>> Running Scheduler: SJF <<<");
        int sjfPassed = 0;

        for (String fileName : fileNames) {
            System.out.println("\n Processing: " + fileName);
            Input currentInput = new Input();
            currentInput.ReadInput(fileName);

            if (currentInput.getProcesses().isEmpty()) continue;

            Output expectedOutput = new Output();
            expectedOutput.ReadOutput(fileName, "SJF");

            SJFScheduler sjf = new SJFScheduler();
            sjf.schedule(currentInput.getProcesses(), currentInput.getContextSwitch());

            boolean isSuccess = validateResults("SJF", sjf.getExecutionOrder(), currentInput.getProcesses(), expectedOutput);

            if (isSuccess) {
                System.out.println("     RESULT: PASS");
                sjfPassed++;
            } else {
                System.out.println("     RESULT: FAIL");
            }
            printStats(currentInput.getProcesses());
            System.out.println("-------------------------------------------------");
        }
        System.out.println("🏁 SJF Summary: " + sjfPassed + "/" + fileNames.size() + " Passed.");
    }

    public static void runRR(List<String> fileNames) {
        System.out.println("\n>>> Running Scheduler: RR (Round Robin) <<<");
        int rrPassed = 0;

        for (String fileName : fileNames) {
            System.out.println("\n Processing: " + fileName);
            Input rrInput = new Input();
            rrInput.ReadInput(fileName);
            if (rrInput.getProcesses().isEmpty()) continue;

            Output expectedOutput = new Output();
            expectedOutput.ReadOutput(fileName, "RR");

            RRScheduler rr = new RRScheduler(rrInput.getProcesses(), rrInput.getRrQuantum(), rrInput.getContextSwitch());
            rr.simulate();

            boolean isSuccess = validateResults("RR", rr.getExecutionOrder(), rrInput.getProcesses(), expectedOutput);

            if (isSuccess) {
                System.out.println("    RESULT: PASS");
                rrPassed++;
            } else {
                System.out.println("     RESULT: FAIL");
            }
            printStats(rrInput.getProcesses());
            System.out.println("-------------------------------------------------");
        }
        System.out.println("🏁 RR Summary: " + rrPassed + "/" + fileNames.size() + " Passed.");
    }

    public static void runPriority(List<String> fileNames) {
        System.out.println("\n>>> Running Scheduler: Priority (Preemptive + Aging) <<<");
        int priorityPassed = 0;

        for (String fileName : fileNames) {
            System.out.println("\n Processing: " + fileName);
            Input priorityInput = new Input();
            priorityInput.ReadInput(fileName);

            if (priorityInput.getProcesses().isEmpty()) continue;

            Output expectedOutput = new Output();
            expectedOutput.ReadOutput(fileName, "Priority");

            PriorityScheduler priority = new PriorityScheduler(
                    priorityInput.getProcesses(),
                    priorityInput.getContextSwitch(),
                    priorityInput.getAgingInterval()
            );

            priority.simulate();

            boolean isSuccess = validateResults("Priority", priority.getExecutionOrder(), priorityInput.getProcesses(), expectedOutput);

            if (isSuccess) {
                System.out.println("    RESULT: PASS");
                priorityPassed++;
            } else {
                System.out.println("     RESULT: FAIL");
            }
            printStats(priorityInput.getProcesses());
            System.out.println("-------------------------------------------------");
        }
        System.out.println("🏁 Priority Summary: " + priorityPassed + "/" + fileNames.size() + " Passed.");
    }

    public static void runAG(List<String> fileNames) {
        System.out.println("\n>>> Running Scheduler: AG Scheduling <<<");
        int agPassed = 0;

        for (String fileName : fileNames) {
            System.out.println("\n Processing: " + fileName);
            Input agInput = new Input();
            agInput.ReadAGInput(fileName);

            if (agInput.getProcesses().isEmpty()) continue;

            Output expectedOutput = new Output();
            expectedOutput.ReadAGOutput(fileName);

            AGScheduler ag = new AGScheduler();
            Process[] processesArray = agInput.getProcesses().toArray(new Process[0]);

            ag.StartSimulation(processesArray);

            ArrayList<Process> agFinished = new ArrayList<>(ag.getFinishedProcesses());
            List<String> agOrder = ag.getExecutionOrder();

            boolean isSuccess = validateResults("AG", agOrder, agFinished, expectedOutput);

            if (isSuccess) {
                System.out.println("    RESULT: PASS");
                agPassed++;
            } else {
                System.out.println("     RESULT: FAIL");
            }
            printStats(agFinished);
            System.out.println("-------------------------------------------------");
        }
        System.out.println("🏁 AG Summary: " + agPassed + "/" + fileNames.size() + " Passed.");
    }

    // =============================================================
    // Main Method
    // =============================================================

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> TestFileNames = List.of("test_1.json", "test_2.json", "test_3.json", "test_4.json", "test_5.json", "test_6.json");
        List<String> AGTestFileNames = List.of("AG_test1.json", "AG_test2.json", "AG_test3.json", "AG_test4.json", "AG_test5.json", "AG_test6.json");

        System.out.println("\n+-----------------------------------------------+");
        System.out.println("|           🕵️  OS SCHEDULER UNIT TESTS         |");
        System.out.println("+-----------------------------------------------+");

        while (true) {
            System.out.println("\nSelect which scheduler to test:");
            System.out.println("1. SJF (Shortest Job First)");
            System.out.println("2. RR (Round Robin)");
            System.out.println("3. Priority (Preemptive)");
            System.out.println("4. AG Scheduling");
            System.out.println("5. Run ALL Tests");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    runSJF(TestFileNames);
                    break;
                case 2:
                    runRR(TestFileNames);
                    break;
                case 3:
                    runPriority(TestFileNames);
                    break;
                case 4:
                    runAG(AGTestFileNames);
                    break;
                case 5:
                    runSJF(TestFileNames);
                    runRR(TestFileNames);
                    runPriority(TestFileNames);
                    runAG(AGTestFileNames);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please select 0-5.");
            }
        }
    }
}