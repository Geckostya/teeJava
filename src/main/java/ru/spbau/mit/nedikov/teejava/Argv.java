package ru.spbau.mit.nedikov.teejava;

import com.beust.jcommander.Parameter;

public class Argv {
    @Parameter(names = { "-a", "--append" }, description = "append to the given FILEs, do not overwrite")
    public boolean appendMode;

    @Parameter(names = {"-i", "--ignore-interrupts"}, description = "ignore interrupt signals")
    public boolean ignoreInterrupts;

    @Parameter(names = "--output-error", description = "set behavior on write error. Valid values: warn, exit")
    public String outputError = "warn";

    @Parameter(names = "--help", description = "display this help and exit")
    public boolean help;

}
