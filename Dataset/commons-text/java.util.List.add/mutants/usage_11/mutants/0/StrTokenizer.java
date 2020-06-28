package org.apache.commons.text;
public class StrTokenizer implements java.lang.Cloneable , java.util.ListIterator<java.lang.String> {
    private static final org.apache.commons.text.StrTokenizer CSV_TOKENIZER_PROTOTYPE;

    private static final org.apache.commons.text.StrTokenizer TSV_TOKENIZER_PROTOTYPE;

    static {
        CSV_TOKENIZER_PROTOTYPE = new org.apache.commons.text.StrTokenizer();
        org.apache.commons.text.StrTokenizer.CSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(org.apache.commons.text.StrMatcher.commaMatcher());
        org.apache.commons.text.StrTokenizer.CSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(org.apache.commons.text.StrMatcher.doubleQuoteMatcher());
        org.apache.commons.text.StrTokenizer.CSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(org.apache.commons.text.StrMatcher.noneMatcher());
        org.apache.commons.text.StrTokenizer.CSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(org.apache.commons.text.StrMatcher.trimMatcher());
        org.apache.commons.text.StrTokenizer.CSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        org.apache.commons.text.StrTokenizer.CSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);
        TSV_TOKENIZER_PROTOTYPE = new org.apache.commons.text.StrTokenizer();
        org.apache.commons.text.StrTokenizer.TSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(org.apache.commons.text.StrMatcher.tabMatcher());
        org.apache.commons.text.StrTokenizer.TSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(org.apache.commons.text.StrMatcher.doubleQuoteMatcher());
        org.apache.commons.text.StrTokenizer.TSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(org.apache.commons.text.StrMatcher.noneMatcher());
        org.apache.commons.text.StrTokenizer.TSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(org.apache.commons.text.StrMatcher.trimMatcher());
        org.apache.commons.text.StrTokenizer.TSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        org.apache.commons.text.StrTokenizer.TSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);
    }

    private char[] chars;

    private java.lang.String[] tokens;

    private int tokenPos;

    private org.apache.commons.text.StrMatcher delimMatcher = org.apache.commons.text.StrMatcher.splitMatcher();

    private org.apache.commons.text.StrMatcher quoteMatcher = org.apache.commons.text.StrMatcher.noneMatcher();

    private org.apache.commons.text.StrMatcher ignoredMatcher = org.apache.commons.text.StrMatcher.noneMatcher();

    private org.apache.commons.text.StrMatcher trimmerMatcher = org.apache.commons.text.StrMatcher.noneMatcher();

    private boolean emptyAsNull = false;

    private boolean ignoreEmptyTokens = true;

    private static org.apache.commons.text.StrTokenizer getCSVClone() {
        return ((org.apache.commons.text.StrTokenizer) (org.apache.commons.text.StrTokenizer.CSV_TOKENIZER_PROTOTYPE.clone()));
    }

    public static org.apache.commons.text.StrTokenizer getCSVInstance() {
        return org.apache.commons.text.StrTokenizer.getCSVClone();
    }

    public static org.apache.commons.text.StrTokenizer getCSVInstance(final java.lang.String input) {
        final org.apache.commons.text.StrTokenizer tok = org.apache.commons.text.StrTokenizer.getCSVClone();
        tok.reset(input);
        return tok;
    }

    public static org.apache.commons.text.StrTokenizer getCSVInstance(final char[] input) {
        final org.apache.commons.text.StrTokenizer tok = org.apache.commons.text.StrTokenizer.getCSVClone();
        tok.reset(input);
        return tok;
    }

    private static org.apache.commons.text.StrTokenizer getTSVClone() {
        return ((org.apache.commons.text.StrTokenizer) (org.apache.commons.text.StrTokenizer.TSV_TOKENIZER_PROTOTYPE.clone()));
    }

    public static org.apache.commons.text.StrTokenizer getTSVInstance() {
        return org.apache.commons.text.StrTokenizer.getTSVClone();
    }

    public static org.apache.commons.text.StrTokenizer getTSVInstance(final java.lang.String input) {
        final org.apache.commons.text.StrTokenizer tok = org.apache.commons.text.StrTokenizer.getTSVClone();
        tok.reset(input);
        return tok;
    }

    public static org.apache.commons.text.StrTokenizer getTSVInstance(final char[] input) {
        final org.apache.commons.text.StrTokenizer tok = org.apache.commons.text.StrTokenizer.getTSVClone();
        tok.reset(input);
        return tok;
    }

    public StrTokenizer() {
        super();
        this.chars = null;
    }

    public StrTokenizer(final java.lang.String input) {
        super();
        if (input != null) {
            chars = input.toCharArray();
        }else {
            chars = null;
        }
    }

    public StrTokenizer(final java.lang.String input, final char delim) {
        this(input);
        setDelimiterChar(delim);
    }

    public StrTokenizer(final java.lang.String input, final java.lang.String delim) {
        this(input);
        setDelimiterString(delim);
    }

    public StrTokenizer(final java.lang.String input, final org.apache.commons.text.StrMatcher delim) {
        this(input);
        setDelimiterMatcher(delim);
    }

    public StrTokenizer(final java.lang.String input, final char delim, final char quote) {
        this(input, delim);
        setQuoteChar(quote);
    }

    public StrTokenizer(final java.lang.String input, final org.apache.commons.text.StrMatcher delim, final org.apache.commons.text.StrMatcher quote) {
        this(input, delim);
        setQuoteMatcher(quote);
    }

    public StrTokenizer(final char[] input) {
        super();
        if (input == null) {
            this.chars = null;
        }else {
            this.chars = input.clone();
        }
    }

    public StrTokenizer(final char[] input, final char delim) {
        this(input);
        setDelimiterChar(delim);
    }

    public StrTokenizer(final char[] input, final java.lang.String delim) {
        this(input);
        setDelimiterString(delim);
    }

    public StrTokenizer(final char[] input, final org.apache.commons.text.StrMatcher delim) {
        this(input);
        setDelimiterMatcher(delim);
    }

    public StrTokenizer(final char[] input, final char delim, final char quote) {
        this(input, delim);
        setQuoteChar(quote);
    }

    public StrTokenizer(final char[] input, final org.apache.commons.text.StrMatcher delim, final org.apache.commons.text.StrMatcher quote) {
        this(input, delim);
        setQuoteMatcher(quote);
    }

    public int size() {
        checkTokenized();
        return tokens.length;
    }

    public java.lang.String nextToken() {
        if (hasNext()) {
            return tokens[((tokenPos)++)];
        }
        return null;
    }

    public java.lang.String previousToken() {
        if (hasPrevious()) {
            return tokens[(--(tokenPos))];
        }
        return null;
    }

    public java.lang.String[] getTokenArray() {
        checkTokenized();
        return tokens.clone();
    }

    public java.util.List<java.lang.String> getTokenList() {
        checkTokenized();
        final java.util.List<java.lang.String> list = new java.util.ArrayList<>(tokens.length);
        java.util.Collections.addAll(list, tokens);
        return list;
    }

    public org.apache.commons.text.StrTokenizer reset() {
        tokenPos = 0;
        tokens = null;
        return this;
    }

    public org.apache.commons.text.StrTokenizer reset(final java.lang.String input) {
        reset();
        if (input != null) {
            this.chars = input.toCharArray();
        }else {
            this.chars = null;
        }
        return this;
    }

    public org.apache.commons.text.StrTokenizer reset(final char[] input) {
        reset();
        if (input != null) {
            this.chars = input.clone();
        }else {
            this.chars = null;
        }
        return this;
    }

    @java.lang.Override
    public boolean hasNext() {
        checkTokenized();
        return (tokenPos) < (tokens.length);
    }

    @java.lang.Override
    public java.lang.String next() {
        if (hasNext()) {
            return tokens[((tokenPos)++)];
        }
        throw new java.util.NoSuchElementException();
    }

    @java.lang.Override
    public int nextIndex() {
        return tokenPos;
    }

    @java.lang.Override
    public boolean hasPrevious() {
        checkTokenized();
        return (tokenPos) > 0;
    }

    @java.lang.Override
    public java.lang.String previous() {
        if (hasPrevious()) {
            return tokens[(--(tokenPos))];
        }
        throw new java.util.NoSuchElementException();
    }

    @java.lang.Override
    public int previousIndex() {
        return (tokenPos) - 1;
    }

    @java.lang.Override
    public void remove() {
        throw new java.lang.UnsupportedOperationException("remove() is unsupported");
    }

    @java.lang.Override
    public void set(final java.lang.String obj) {
        throw new java.lang.UnsupportedOperationException("set() is unsupported");
    }

    @java.lang.Override
    public void add(final java.lang.String obj) {
        throw new java.lang.UnsupportedOperationException("add() is unsupported");
    }

    private void checkTokenized() {
        if ((tokens) == null) {
            if ((chars) == null) {
                final java.util.List<java.lang.String> split = tokenize(null, 0, 0);
                tokens = split.toArray(new java.lang.String[split.size()]);
            }else {
                final java.util.List<java.lang.String> split = tokenize(chars, 0, chars.length);
                tokens = split.toArray(new java.lang.String[split.size()]);
            }
        }
    }

    protected java.util.List<java.lang.String> tokenize(final char[] srcChars, final int offset, final int count) {
        if ((srcChars == null) || (count == 0)) {
            return java.util.Collections.emptyList();
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder();
        final java.util.List<java.lang.String> tokenList = new java.util.ArrayList<>();
        int pos = offset;
        while ((pos >= 0) && (pos < count)) {
            pos = readNextToken(srcChars, pos, count, buf, tokenList);
            if (pos >= count) {
                addToken(tokenList, "");
            }
        } 
        return tokenList;
    }

    private void addToken(final java.util.List<java.lang.String> list, java.lang.String tok) {
        if ((tok == null) || ((tok.length()) == 0)) {
            if (isIgnoreEmptyTokens()) {
                return;
            }
            if (isEmptyTokenAsNull()) {
                tok = null;
            }
        }
        list.add(tok);
    }

    private int readNextToken(final char[] srcChars, int start, final int len, final org.apache.commons.text.StrBuilder workArea, final java.util.List<java.lang.String> tokenList) {
        while (start < len) {
            final int removeLen = java.lang.Math.max(getIgnoredMatcher().isMatch(srcChars, start, start, len), getTrimmerMatcher().isMatch(srcChars, start, start, len));
            if (((removeLen == 0) || ((getDelimiterMatcher().isMatch(srcChars, start, start, len)) > 0)) || ((getQuoteMatcher().isMatch(srcChars, start, start, len)) > 0)) {
                break;
            }
            start += removeLen;
        } 
        if (start >= len) {
            addToken(tokenList, "");
            return -1;
        }
        final int delimLen = getDelimiterMatcher().isMatch(srcChars, start, start, len);
        if (delimLen > 0) {
            addToken(tokenList, "");
            return start + delimLen;
        }
        final int quoteLen = getQuoteMatcher().isMatch(srcChars, start, start, len);
        if (quoteLen > 0) {
            return readWithQuotes(srcChars, (start + quoteLen), len, workArea, tokenList, start, quoteLen);
        }
        return readWithQuotes(srcChars, start, len, workArea, tokenList, 0, 0);
    }

    private int readWithQuotes(final char[] srcChars, final int start, final int len, final org.apache.commons.text.StrBuilder workArea, final java.util.List<java.lang.String> tokenList, final int quoteStart, final int quoteLen) {
        workArea.clear();
        int pos = start;
        boolean quoting = quoteLen > 0;
        int trimStart = 0;
        while (pos < len) {
            if (quoting) {
                if (isQuote(srcChars, pos, len, quoteStart, quoteLen)) {
                    if (isQuote(srcChars, (pos + quoteLen), len, quoteStart, quoteLen)) {
                        workArea.append(srcChars, pos, quoteLen);
                        pos += quoteLen * 2;
                        trimStart = workArea.size();
                        continue;
                    }
                    quoting = false;
                    pos += quoteLen;
                    continue;
                }
                workArea.append(srcChars[(pos++)]);
                trimStart = workArea.size();
            }else {
                final int delimLen = getDelimiterMatcher().isMatch(srcChars, pos, start, len);
                if (delimLen > 0) {
                    addToken(tokenList, workArea.substring(0, trimStart));
                    return pos + delimLen;
                }
                if ((quoteLen > 0) && (isQuote(srcChars, pos, len, quoteStart, quoteLen))) {
                    quoting = true;
                    pos += quoteLen;
                    continue;
                }
                final int ignoredLen = getIgnoredMatcher().isMatch(srcChars, pos, start, len);
                if (ignoredLen > 0) {
                    pos += ignoredLen;
                    continue;
                }
                final int trimmedLen = getTrimmerMatcher().isMatch(srcChars, pos, start, len);
                if (trimmedLen > 0) {
                    workArea.append(srcChars, pos, trimmedLen);
                    pos += trimmedLen;
                    continue;
                }
                workArea.append(srcChars[(pos++)]);
                trimStart = workArea.size();
            }
        } 
        addToken(tokenList, workArea.substring(0, trimStart));
        return -1;
    }

    private boolean isQuote(final char[] srcChars, final int pos, final int len, final int quoteStart, final int quoteLen) {
        for (int i = 0; i < quoteLen; i++) {
            if (((pos + i) >= len) || ((srcChars[(pos + i)]) != (srcChars[(quoteStart + i)]))) {
                return false;
            }
        }
        return true;
    }

    public org.apache.commons.text.StrMatcher getDelimiterMatcher() {
        return this.delimMatcher;
    }

    public org.apache.commons.text.StrTokenizer setDelimiterMatcher(final org.apache.commons.text.StrMatcher delim) {
        if (delim == null) {
            this.delimMatcher = org.apache.commons.text.StrMatcher.noneMatcher();
        }else {
            this.delimMatcher = delim;
        }
        return this;
    }

    public org.apache.commons.text.StrTokenizer setDelimiterChar(final char delim) {
        return setDelimiterMatcher(org.apache.commons.text.StrMatcher.charMatcher(delim));
    }

    public org.apache.commons.text.StrTokenizer setDelimiterString(final java.lang.String delim) {
        return setDelimiterMatcher(org.apache.commons.text.StrMatcher.stringMatcher(delim));
    }

    public org.apache.commons.text.StrMatcher getQuoteMatcher() {
        return quoteMatcher;
    }

    public org.apache.commons.text.StrTokenizer setQuoteMatcher(final org.apache.commons.text.StrMatcher quote) {
        if (quote != null) {
            this.quoteMatcher = quote;
        }
        return this;
    }

    public org.apache.commons.text.StrTokenizer setQuoteChar(final char quote) {
        return setQuoteMatcher(org.apache.commons.text.StrMatcher.charMatcher(quote));
    }

    public org.apache.commons.text.StrMatcher getIgnoredMatcher() {
        return ignoredMatcher;
    }

    public org.apache.commons.text.StrTokenizer setIgnoredMatcher(final org.apache.commons.text.StrMatcher ignored) {
        if (ignored != null) {
            this.ignoredMatcher = ignored;
        }
        return this;
    }

    public org.apache.commons.text.StrTokenizer setIgnoredChar(final char ignored) {
        return setIgnoredMatcher(org.apache.commons.text.StrMatcher.charMatcher(ignored));
    }

    public org.apache.commons.text.StrMatcher getTrimmerMatcher() {
        return trimmerMatcher;
    }

    public org.apache.commons.text.StrTokenizer setTrimmerMatcher(final org.apache.commons.text.StrMatcher trimmer) {
        if (trimmer != null) {
            this.trimmerMatcher = trimmer;
        }
        return this;
    }

    public boolean isEmptyTokenAsNull() {
        return this.emptyAsNull;
    }

    public org.apache.commons.text.StrTokenizer setEmptyTokenAsNull(final boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
        return this;
    }

    public boolean isIgnoreEmptyTokens() {
        return ignoreEmptyTokens;
    }

    public org.apache.commons.text.StrTokenizer setIgnoreEmptyTokens(final boolean ignoreEmptyTokens) {
        this.ignoreEmptyTokens = ignoreEmptyTokens;
        return this;
    }

    public java.lang.String getContent() {
        if ((chars) == null) {
            return null;
        }
        return new java.lang.String(chars);
    }

    @java.lang.Override
    public java.lang.Object clone() {
        try {
            return cloneReset();
        } catch (final java.lang.CloneNotSupportedException ex) {
            return null;
        }
    }

    java.lang.Object cloneReset() throws java.lang.CloneNotSupportedException {
        final org.apache.commons.text.StrTokenizer cloned = ((org.apache.commons.text.StrTokenizer) (super.clone()));
        if ((cloned.chars) != null) {
            cloned.chars = cloned.chars.clone();
        }
        cloned.reset();
        return cloned;
    }

    @java.lang.Override
    public java.lang.String toString() {
        if ((tokens) == null) {
            return "StrTokenizer[not tokenized yet]";
        }
        return "StrTokenizer" + (getTokenList());
    }
}