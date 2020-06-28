package org.apache.commons.text.diff;
public class ReplacementsFinder<T> implements org.apache.commons.text.diff.CommandVisitor<T> {
    private final java.util.List<T> pendingInsertions;

    private final java.util.List<T> pendingDeletions;

    private int skipped;

    private final org.apache.commons.text.diff.ReplacementsHandler<T> handler;

    public ReplacementsFinder(final org.apache.commons.text.diff.ReplacementsHandler<T> handler) {
        pendingInsertions = new java.util.ArrayList<>();
        pendingDeletions = new java.util.ArrayList<>();
        skipped = 0;
        this.handler = handler;
    }

    @java.lang.Override
    public void visitInsertCommand(final T object) {
    }

    @java.lang.Override
    public void visitKeepCommand(final T object) {
        if ((pendingDeletions.isEmpty()) && (pendingInsertions.isEmpty())) {
            ++(skipped);
        }else {
            handler.handleReplacement(skipped, pendingDeletions, pendingInsertions);
            pendingDeletions.clear();
            pendingInsertions.clear();
            skipped = 1;
        }
    }

    @java.lang.Override
    public void visitDeleteCommand(final T object) {
        pendingDeletions.add(object);
    }
}