package task;

import utils.Utils;

import java.util.*;

public class Schedule {
    private final Map<Integer, List<Task>> schedule = new HashMap<>();
    private List<Task> waitingList;

    public Schedule(List<Task> tasks) {
        for (Task task : tasks) {
            if (this.schedule.containsKey(task.getSchedule())) {
                this.schedule.get(task.getSchedule()).add(task);
            } else {
                this.schedule.put(task.getSchedule(), new ArrayList<>());
                this.schedule.get(task.getSchedule()).add(task);
            }
        }
        // showSchedule();
        waitingList = new ArrayList<>();
    }

    public void committed(int schedule, int idx, Lock lock, List<Task> _task){
        for (int i = idx-1; i>=0; i--){
            try {
                    this.schedule.get(schedule).get(i).setStatus("COMMITTED");
                    lock.unlock(this.schedule.get(schedule).get(i).getResource(), schedule);
            }catch (Exception e){
                System.out.print("");
            }
        }
        System.out.println(_task.get(idx));
        if (!waitingList.isEmpty()) {
            Iterator<Task> iterator = waitingList.iterator();
            while (iterator.hasNext()) {
                Task currentTask = iterator.next();
                String keyType = Utils.isKeyExclusive(_task, currentTask);

                if (lock.checkPrivileged(currentTask)) {
                    System.out.println(currentTask);
                    iterator.remove();
                    continue;
                }

                if (lock.checkPermission(currentTask, keyType)) {
                    lock.addLock(currentTask.getResource(), keyType, currentTask.getSchedule());
                    System.out.println(currentTask);
                    iterator.remove();
                }
            }
        }
    }

    public Task getTaskByQueueAtSchedule(int queue) {
        Set<Integer> keys = this.schedule.keySet();
        for (int key : keys) {
            List<Task> tasks = this.schedule.get(key);
            for (Task task : tasks) {
                if (task.getQueue() == queue)
                    return task;
            }
        }
        return null;
    }

    public boolean isWaiting(Task task) {
        return waitingList.contains(task);
    }

    public boolean isWaitingEmpty() {
        return waitingList.isEmpty();
    }

    public void addWaitingList(Task task) {
        waitingList.add(task);
    }

    public List<Task> getTransactionSchedule(int schedule) {
        return this.schedule.get(schedule);
    }

    public Map<Integer, List<Task>> getScheduleMap() {
        return this.schedule;
    }
    
    public Set<Integer> getTransactionNames() {
        return this.schedule.keySet();
    }
    
    public int getTransactionLatestTime(int schedule) {
        // get latest element in list
        return (this.schedule.get(schedule).get(this.schedule.get(schedule).size() - 1)).getQueue();
    }
    
    public void showSchedule() {
        for (Integer keyInteger : schedule.keySet()) {
            System.out.println(keyInteger + ": " + this.schedule.get(keyInteger));
        }
    }
    public List<Task> getWaitingList(){
        return this.waitingList;
    }
    public void setWaitingList(List<Task> waitingList){
        this.waitingList=waitingList;
    }
}
