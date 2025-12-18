import java.util.*;
import java.io.*;
import com.google.gson.*;

public class PrioritySchedulerTest {
    
    static class TestCase {
        String name;
        Input input;
        ExpectedOutput expectedOutput;
        
        static class Input {
            int contextSwitch;
            int rrQuantum;
            int agingInterval;
            List<ProcessInput> processes;
        }
        
        static class ProcessInput {
            String name;
            int arrival;
            int burst;
            int priority;
        }
        
        static class ExpectedOutput {
            PriorityOutput Priority;
        }
        
        static class PriorityOutput {
            List<String> executionOrder;
            List<ProcessResult> processResults;
            double averageWaitingTime;
            double averageTurnaroundTime;
        }
        
        static class ProcessResult {
            String name;
            int waitingTime;
            int turnaroundTime;
        }
    }
    
    public static void runTest(String testFilePath) {
        try {
            // Read JSON file
            Gson gson = new Gson();
            FileReader reader = new FileReader(testFilePath);
            TestCase testCase = gson.fromJson(reader, TestCase.class);
            reader.close();
            
            System.out.println("\n========================================");
            System.out.println("Running Test: " + testCase.name);
            System.out.println("========================================");
            
            // Create processes from test input
            ArrayList<Process> processes = new ArrayList<>();
            for (TestCase.ProcessInput pi : testCase.input.processes) {
                processes.add(new Process(pi.name, pi.arrival, pi.burst, pi.priority, 0));
            }
            
            // Run scheduler
            PriorityScheduler scheduler = new PriorityScheduler(
                processes, 
                testCase.input.contextSwitch, 
                testCase.input.agingInterval
            );
            
            // Capture output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);
            
            scheduler.simulate();
            
            System.out.flush();
            System.setOut(old);
            String output = baos.toString();
            
            // Parse output and compare with expected
            boolean passed = validateOutput(output, testCase.expectedOutput.Priority, processes);
            
            if (passed) {
                System.out.println("✓ TEST PASSED: " + testFilePath);
            } else {
                System.out.println("✗ TEST FAILED: " + testFilePath);
            }
            
        } catch (Exception e) {
            System.out.println("Error running test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean validateOutput(String output, TestCase.PriorityOutput expected, 
                                          ArrayList<Process> actualProcesses) {
        boolean allPassed = true;
        
        // Sort processes by name for comparison
        actualProcesses.sort(Comparator.comparing(Process::getName));
        
        System.out.println("\n--- Validation Results ---");
        
        // Validate waiting times
        for (int i = 0; i < expected.processResults.size(); i++) {
            TestCase.ProcessResult expectedResult = expected.processResults.get(i);
            
            // Find matching process
            Process actualProcess = null;
            for (Process p : actualProcesses) {
                if (p.getName().equals(expectedResult.name)) {
                    actualProcess = p;
                    break;
                }
            }
            
            if (actualProcess == null) {
                System.out.println("✗ Process " + expectedResult.name + " not found");
                allPassed = false;
                continue;
            }
            
            // Check waiting time
            if (actualProcess.getWaitingTime() == expectedResult.waitingTime) {
                System.out.println("✓ " + expectedResult.name + " Waiting Time: " + 
                                 actualProcess.getWaitingTime() + " (Expected: " + 
                                 expectedResult.waitingTime + ")");
            } else {
                System.out.println("✗ " + expectedResult.name + " Waiting Time: " + 
                                 actualProcess.getWaitingTime() + " (Expected: " + 
                                 expectedResult.waitingTime + ")");
                allPassed = false;
            }
            
            // Check turnaround time
            if (actualProcess.getTurnaroundTime() == expectedResult.turnaroundTime) {
                System.out.println("✓ " + expectedResult.name + " Turnaround Time: " + 
                                 actualProcess.getTurnaroundTime() + " (Expected: " + 
                                 expectedResult.turnaroundTime + ")");
            } else {
                System.out.println("✗ " + expectedResult.name + " Turnaround Time: " + 
                                 actualProcess.getTurnaroundTime() + " (Expected: " + 
                                 expectedResult.turnaroundTime + ")");
                allPassed = false;
            }
        }
        
        // Calculate actual averages
        double actualAvgWaiting = 0;
        double actualAvgTurnaround = 0;
        for (Process p : actualProcesses) {
            actualAvgWaiting += p.getWaitingTime();
            actualAvgTurnaround += p.getTurnaroundTime();
        }
        actualAvgWaiting /= actualProcesses.size();
        actualAvgTurnaround /= actualProcesses.size();
        
        // Check averages (with tolerance for floating point)
        if (Math.abs(actualAvgWaiting - expected.averageWaitingTime) < 0.1) {
            System.out.println("✓ Average Waiting Time: " + 
                             String.format("%.2f", actualAvgWaiting) + 
                             " (Expected: " + expected.averageWaitingTime + ")");
        } else {
            System.out.println("✗ Average Waiting Time: " + 
                             String.format("%.2f", actualAvgWaiting) + 
                             " (Expected: " + expected.averageWaitingTime + ")");
            allPassed = false;
        }
        
        if (Math.abs(actualAvgTurnaround - expected.averageTurnaroundTime) < 0.1) {
            System.out.println("✓ Average Turnaround Time: " + 
                             String.format("%.2f", actualAvgTurnaround) + 
                             " (Expected: " + expected.averageTurnaroundTime + ")");
        } else {
            System.out.println("✗ Average Turnaround Time: " + 
                             String.format("%.2f", actualAvgTurnaround) + 
                             " (Expected: " + expected.averageTurnaroundTime + ")");
            allPassed = false;
        }
        
        return allPassed;
    }
    
    public static void main(String[] args) {
        // Run all test cases
        String[] testFiles = {
            "test_1.json",
            "test_2.json",
            "test_3.json",
            "test_4.json",
            "test_5.json",
            "test_6.json"
        };
        
        int passed = 0;
        int total = testFiles.length;
        
        for (String testFile : testFiles) {
            try {
                runTest(testFile);
                passed++;
            } catch (Exception e) {
                System.out.println("Failed to run test: " + testFile);
            }
        }
        System.out.println("Final Results: " + passed + "/" + total + " tests passed");
        
    }
}
