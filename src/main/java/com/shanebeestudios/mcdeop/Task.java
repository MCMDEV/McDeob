package com.shanebeestudios.mcdeop;

public class Task {

    private final Version version;
    private final TaskType taskType;

    public Task(Version version, TaskType taskType) {
        this.version = version;
        this.taskType = taskType;
    }

    public Version getVersion() {
        return version;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getJar()  {
        if(taskType == TaskType.CLIENT) {
            return version.getClientJar();
        }
        return version.getServerJar();
    }

    public String getMappings()  {
        if(taskType == TaskType.CLIENT) {
            return version.getClientMappings();
        }
        return version.getServerMappings();
    }

    public enum TaskType   {
        CLIENT,
        SERVER
    }

}
