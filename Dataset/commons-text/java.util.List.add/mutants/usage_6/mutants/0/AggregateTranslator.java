package org.apache.commons.text.translate;
public class AggregateTranslator extends org.apache.commons.text.translate.CharSequenceTranslator {
    private final java.util.List<org.apache.commons.text.translate.CharSequenceTranslator> translators = new java.util.ArrayList<>();

    public AggregateTranslator(final org.apache.commons.text.translate.CharSequenceTranslator... translators) {
        if (translators != null) {
            for (final org.apache.commons.text.translate.CharSequenceTranslator translator : translators) {
                if (translator != null) {
                    this.translators.add(translator);
                }
            }
        }
    }

    @java.lang.Override
    public int translate(final java.lang.CharSequence input, final int index, final java.io.Writer out) throws java.io.IOException {
        for (final org.apache.commons.text.translate.CharSequenceTranslator translator : translators) {
            final int consumed = translator.translate(input, index, out);
            if (consumed != 0) {
                return consumed;
            }
        }
        return 0;
    }
}