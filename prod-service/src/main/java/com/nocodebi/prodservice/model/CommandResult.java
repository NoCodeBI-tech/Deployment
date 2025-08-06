package com.nocodebi.prodservice.model;

public class CommandResult {
    private String stdout;
    private String stderr;
    private int exitCode;
    private boolean success;

    public CommandResult() {
    }

    public CommandResult(String stdout, String stderr, int exitCode) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitCode = exitCode;
        this.success = (exitCode == 0);
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "stdout='" + stdout + '\'' +
                ", stderr='" + stderr + '\'' +
                ", exitCode=" + exitCode +
                ", success=" + success +
                '}';
    }
}
