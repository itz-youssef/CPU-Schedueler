import java.util.*;
import java.io.*;
import com.google.gson.*;

class Input {
    private ArrayList<Process> processes = new ArrayList<>();
    private String name;
    private int contextSwitch;
    private int rrQuantum;
    private int agingInterval;

    public Input ReadInput(String filename) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(filename);
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            this.name = root.get("name").getAsString();
            JsonObject inputObj = root.get("input").getAsJsonObject();
            this.rrQuantum = inputObj.get("rrQuantum").getAsInt();
            this.contextSwitch = inputObj.get("contextSwitch").getAsInt();
            this.agingInterval = inputObj.get("agingInterval").getAsInt();
            JsonArray processesArray = inputObj.get("processes").getAsJsonArray();
            this.processes.clear();

            for (JsonElement element : processesArray) {
                JsonObject p = element.getAsJsonObject();
                String pName = p.get("name").getAsString();
                int pArrival = p.get("arrival").getAsInt();
                int pBurst = p.get("burst").getAsInt();
                int pPriority = p.get("priority").getAsInt();
                processes.add(new Process(pName, pArrival, pBurst, pPriority, this.rrQuantum));
            }

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found! Check the path: " + filename);
        } catch (Exception e) {
            System.out.println("Error reading JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    public Input ReadAGInput(String filename) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(filename);
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            JsonObject inputObj = root.get("input").getAsJsonObject();
            JsonArray processesArray = inputObj.get("processes").getAsJsonArray();
            this.processes.clear();

            for (JsonElement element : processesArray) {
                JsonObject p = element.getAsJsonObject();
                String pName = p.get("name").getAsString();
                int pArrival = p.get("arrival").getAsInt();
                int pBurst = p.get("burst").getAsInt();
                int pPriority = p.get("priority").getAsInt();
                int pquantum = p.get("quantum").getAsInt();
                processes.add(new Process(pName, pArrival, pBurst, pPriority, pquantum));
            }

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found! Check the path: " + filename);
        } catch (Exception e) {
            System.out.println("Error reading JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    public ArrayList<Process> getProcesses() { return processes; }
    public String getName() { return name; }
    public int getContextSwitch() { return contextSwitch; }
    public int getRrQuantum() { return rrQuantum; }
    public int getAgingInterval() { return agingInterval; }
}


class outputprocess {
    private String name;
    private int waitingTime;
    private int turnaroundTime;
    private List<Integer> quantumHistory;

    public outputprocess(String name, int waitingTime, int turnaroundTime, List<Integer> quantumHistory) {
        this.name = name;
        this.waitingTime = waitingTime;
        this.turnaroundTime = turnaroundTime;
        this.quantumHistory = quantumHistory;
    }

    public outputprocess(String name, int waitingTime, int turnaroundTime) {
        this(name, waitingTime, turnaroundTime, new ArrayList<>());
    }

    public String getName() { return name; }
    public int getWaitingTime() { return waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public List<Integer> get_Quantum_History() { return quantumHistory; }
}


class Output {
    private ArrayList<outputprocess> valid_processes = new ArrayList<>();
    private ArrayList<String> executionOrder = new ArrayList<>();

    private double averageWaitingTime;
    private double averageTurnaroundTime;

    public void ReadOutput(String filename, String Schedule_Name) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(filename);
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            JsonObject OutputObject = root.get("expectedOutput").getAsJsonObject();

            if (!OutputObject.has(Schedule_Name)) {
                System.out.println("Error: Schedule " + Schedule_Name + " not found in JSON!");
                return;
            }
            JsonObject ScheduleObject = OutputObject.get(Schedule_Name).getAsJsonObject();

            JsonArray ExecutionArray = ScheduleObject.get("executionOrder").getAsJsonArray();
            executionOrder.clear();
            for (JsonElement element : ExecutionArray) {
                executionOrder.add(element.getAsString());
            }

            JsonArray Results = ScheduleObject.get("processResults").getAsJsonArray();
            valid_processes.clear();
            for (JsonElement res : Results) {
                JsonObject p = res.getAsJsonObject();
                String pName = p.get("name").getAsString();
                int waiting_Time = p.get("waitingTime").getAsInt();
                int turnaround_Time = p.get("turnaroundTime").getAsInt();
                valid_processes.add(new outputprocess(pName, waiting_Time, turnaround_Time));
            }

            averageWaitingTime = ScheduleObject.get("averageWaitingTime").getAsDouble();
            averageTurnaroundTime = ScheduleObject.get("averageTurnaroundTime").getAsDouble();

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found! Check the path: " + filename);
        } catch (Exception e) {
            System.out.println("Error reading JSON Output: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void ReadAGOutput(String filename) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(filename);
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            if (!root.has("expectedOutput")) {
                return;
            }
            JsonObject OutputObject = root.get("expectedOutput").getAsJsonObject();

            if (OutputObject.has("executionOrder")) {
                JsonArray ExecutionArray = OutputObject.get("executionOrder").getAsJsonArray();
                executionOrder.clear();
                for (JsonElement element : ExecutionArray) {
                    executionOrder.add(element.getAsString());
                }
            }

            if (OutputObject.has("processResults")) {
                JsonArray Results = OutputObject.get("processResults").getAsJsonArray();
                valid_processes.clear();
                for (JsonElement res : Results) {
                    JsonObject p = res.getAsJsonObject();
                    String pName = p.get("name").getAsString();
                    int waiting_Time = p.get("waitingTime").getAsInt();
                    int turnaround_Time = p.get("turnaroundTime").getAsInt();

                    List<Integer> quantumHistory = new ArrayList<>();
                    if (p.has("quantumHistory")) {
                        JsonArray quantumHistoryArray = p.get("quantumHistory").getAsJsonArray();
                        for (JsonElement element : quantumHistoryArray) {
                            quantumHistory.add(element.getAsInt());
                        }
                    }
                    valid_processes.add(new outputprocess(pName, waiting_Time, turnaround_Time, quantumHistory));
                }
            }

            if (OutputObject.has("averageWaitingTime")) {
                averageWaitingTime = OutputObject.get("averageWaitingTime").getAsDouble();
            }
            if (OutputObject.has("averageTurnaroundTime")) {
                averageTurnaroundTime = OutputObject.get("averageTurnaroundTime").getAsDouble();
            }

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found! Check the path: " + filename);
        } catch (Exception e) {
            System.out.println("Error reading JSON Output: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public ArrayList<outputprocess> getValidProcesses() { return valid_processes; }
    public ArrayList<String> getProcessesOrder() { return executionOrder; }
    public double getAverageWaitingTime() { return averageWaitingTime; }
    public double getAverageTurnaroundTime() { return averageTurnaroundTime; }
}



