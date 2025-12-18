import java.util.*;

public class Main {

    public static boolean validateResults(String testName, List<String> actualOrder, ArrayList<Process> actualProcesses, Output expectedOutput) {        boolean passed = true;
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
                if (act.getWaitingTime() != exp.getWaitingTime() || act.getTurnaroundTime() != exp.getTurnaroundTime()) {
                    System.out.println("         Data Mismatch for " + act.getName() + ":");
                    System.out.println("         Expected -> WT: " + exp.getWaitingTime() + ", TAT: " + exp.getTurnaroundTime());
                    System.out.println("         Actual   -> WT: " + act.getWaitingTime() + ", TAT: " + act.getTurnaroundTime());
                    passed = false;
                }
            }
        }

        // 3.  (Averages)
        double totalWT = 0, totalTAT = 0;
        for (Process p : actualProcesses) {
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }
        double actualAvgWT = totalWT / actualProcesses.size();
        double actualAvgTAT = totalTAT / actualProcesses.size();

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
        System.out.println("   +------+--------------+-----------------+");
        System.out.println("   | Name | Waiting Time | Turnaround Time |");
        System.out.println("   +------+--------------+-----------------+");

        for (Process p : sortedList) {
            System.out.printf("   | %-4s | %-12d | %-15d |%n",
                    p.getName(), p.getWaitingTime(), p.getTurnaroundTime());

            totalWaiting += p.getWaitingTime();
            totalTurnaround += p.getTurnaroundTime();
        }
        System.out.println("   +------+--------------+-----------------+");

        double avgWaiting = totalWaiting / processes.size();
        double avgTurnaround = totalTurnaround / processes.size();
        System.out.printf("   ➤ Average Waiting Time    : %.2f%n", avgWaiting);
        System.out.printf("   ➤ Average Turnaround Time : %.2f%n", avgTurnaround);
    }

    // =============================================================

    public static void main(String[] args) {
        List<String> TestFileNames = List.of("test_1.json", "test_2.json", "test_3.json", "test_4.json", "test_5.json", "test_6.json");

        System.out.println("\n+-----------------------------------------------+");
        System.out.println("|           🕵️  UNIT TEST RUNNER               |");
        System.out.println("+-----------------------------------------------+");

        // ==========================================
        // SECTION 1: SJF SCHEDULER
        // ==========================================
        System.out.println("\n>>> Running Scheduler: SJF <<<");
        int sjfPassed = 0;

        for (String fileName : TestFileNames) {
            System.out.println("\n Processing: " + fileName);
            Input currentInput = new Input();
            currentInput.ReadInput(fileName);

            if (currentInput.getProcesses().isEmpty()) continue;

            Output expectedOutput = new Output();
            expectedOutput.ReadOutput(fileName, "SJF");

            SJFScheduler sjf = new SJFScheduler();
            sjf.schedule(currentInput.getProcesses(), currentInput.getContextSwitch());
            System.out.println("     Execution Order:" + sjf.getExecutionOrder());

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


        // ==========================================
        // SECTION 2: ROUND ROBIN SCHEDULER
        // ==========================================
        System.out.println("\n\n>>> Running Scheduler: RR (Round Robin) <<<");
        int rrPassed = 0;

        for (String fileName : TestFileNames) {
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

        System.out.println("\n🏁 Summary:");
        System.out.println("   ➤ SJF Passed: " + sjfPassed + "/" + TestFileNames.size());
        System.out.println("   ➤ RR  Passed: " + rrPassed + "/" + TestFileNames.size());
    }
}
