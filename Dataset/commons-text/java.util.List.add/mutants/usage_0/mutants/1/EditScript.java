package org.apache.commons.text.diff;
public class EditScript<T> {
    private final java.util.List<org.apache.commons.text.diff.EditCommand<T>> commands;

    private int lcsLength;

    private int modifications;

    public EditScript() {
        commands = new java.util.ArrayList<>();
        lcsLength = 0;
        modifications = 0;
    }

    public void append(final org.apache.commons.text.diff.KeepCommand<T> command);

    public void append(final org.apache.commons.text.diff.InsertCommand<T> command) {
        commands.add(command);
        ++(modifications);
    }

    public void append(final org.apache.commons.text.diff.DeleteCommand<T> command) {
        commands.add(command);
        ++(modifications);
    }

    public void visit(final org.apache.commons.text.diff.CommandVisitor<T> visitor) {
        for (final org.apache.commons.text.diff.EditCommand<T> command : commands) {
            command.accept(visitor);
        }
    }

    public int getLCSLength() {
        return lcsLength;
    }

    public int getModifications() {
        return modifications;
    }
}