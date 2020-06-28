package org.apache.commons.text;
public class StrSubstitutor {
    public static final char DEFAULT_ESCAPE = '$';

    public static final org.apache.commons.text.StrMatcher DEFAULT_PREFIX = org.apache.commons.text.StrMatcher.stringMatcher("${");

    public static final org.apache.commons.text.StrMatcher DEFAULT_SUFFIX = org.apache.commons.text.StrMatcher.stringMatcher("}");

    public static final org.apache.commons.text.StrMatcher DEFAULT_VALUE_DELIMITER = org.apache.commons.text.StrMatcher.stringMatcher(":-");

    private char escapeChar;

    private org.apache.commons.text.StrMatcher prefixMatcher;

    private org.apache.commons.text.StrMatcher suffixMatcher;

    private org.apache.commons.text.StrMatcher valueDelimiterMatcher;

    private org.apache.commons.text.lookup.StringLookup variableResolver;

    private boolean enableSubstitutionInVariables;

    private boolean preserveEscapes = false;

    private boolean disableSubstitutionInValues;

    public static <V> java.lang.String replace(final java.lang.Object source, final java.util.Map<java.lang.String, V> valueMap) {
        return new org.apache.commons.text.StrSubstitutor(valueMap).replace(source);
    }

    public static <V> java.lang.String replace(final java.lang.Object source, final java.util.Map<java.lang.String, V> valueMap, final java.lang.String prefix, final java.lang.String suffix) {
        return new org.apache.commons.text.StrSubstitutor(valueMap, prefix, suffix).replace(source);
    }

    public static java.lang.String replace(final java.lang.Object source, final java.util.Properties valueProperties) {
        if (valueProperties == null) {
            return source.toString();
        }
        final java.util.Map<java.lang.String, java.lang.String> valueMap = new java.util.HashMap<>();
        final java.util.Enumeration<?> propNames = valueProperties.propertyNames();
        while (propNames.hasMoreElements()) {
            final java.lang.String propName = ((java.lang.String) (propNames.nextElement()));
            final java.lang.String propValue = valueProperties.getProperty(propName);
            valueMap.put(propName, propValue);
        } 
        return org.apache.commons.text.StrSubstitutor.replace(source, valueMap);
    }

    public static java.lang.String replaceSystemProperties(final java.lang.Object source) {
        return new org.apache.commons.text.StrSubstitutor(org.apache.commons.text.lookup.StringLookupFactory.INSTANCE.systemPropertyStringLookup()).replace(source);
    }

    public StrSubstitutor() {
        this(((org.apache.commons.text.StrLookup<?>) (null)), org.apache.commons.text.StrSubstitutor.DEFAULT_PREFIX, org.apache.commons.text.StrSubstitutor.DEFAULT_SUFFIX, org.apache.commons.text.StrSubstitutor.DEFAULT_ESCAPE);
    }

    public <V> StrSubstitutor(final java.util.Map<java.lang.String, V> valueMap) {
        this(org.apache.commons.text.StrLookup.mapLookup(valueMap), org.apache.commons.text.StrSubstitutor.DEFAULT_PREFIX, org.apache.commons.text.StrSubstitutor.DEFAULT_SUFFIX, org.apache.commons.text.StrSubstitutor.DEFAULT_ESCAPE);
    }

    public <V> StrSubstitutor(final java.util.Map<java.lang.String, V> valueMap, final java.lang.String prefix, final java.lang.String suffix) {
        this(org.apache.commons.text.StrLookup.mapLookup(valueMap), prefix, suffix, org.apache.commons.text.StrSubstitutor.DEFAULT_ESCAPE);
    }

    public <V> StrSubstitutor(final java.util.Map<java.lang.String, V> valueMap, final java.lang.String prefix, final java.lang.String suffix, final char escape) {
        this(org.apache.commons.text.StrLookup.mapLookup(valueMap), prefix, suffix, escape);
    }

    public <V> StrSubstitutor(final java.util.Map<java.lang.String, V> valueMap, final java.lang.String prefix, final java.lang.String suffix, final char escape, final java.lang.String valueDelimiter) {
        this(org.apache.commons.text.StrLookup.mapLookup(valueMap), prefix, suffix, escape, valueDelimiter);
    }

    @java.lang.Deprecated
    public StrSubstitutor(final org.apache.commons.text.StrLookup<?> variableResolver) {
        this(variableResolver, org.apache.commons.text.StrSubstitutor.DEFAULT_PREFIX, org.apache.commons.text.StrSubstitutor.DEFAULT_SUFFIX, org.apache.commons.text.StrSubstitutor.DEFAULT_ESCAPE);
    }

    @java.lang.Deprecated
    public StrSubstitutor(final org.apache.commons.text.StrLookup<?> variableResolver, final java.lang.String prefix, final java.lang.String suffix, final char escape) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefix(prefix);
        this.setVariableSuffix(suffix);
        this.setEscapeChar(escape);
        this.setValueDelimiterMatcher(org.apache.commons.text.StrSubstitutor.DEFAULT_VALUE_DELIMITER);
    }

    @java.lang.Deprecated
    public StrSubstitutor(final org.apache.commons.text.StrLookup<?> variableResolver, final java.lang.String prefix, final java.lang.String suffix, final char escape, final java.lang.String valueDelimiter) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefix(prefix);
        this.setVariableSuffix(suffix);
        this.setEscapeChar(escape);
        this.setValueDelimiter(valueDelimiter);
    }

    @java.lang.Deprecated
    public StrSubstitutor(final org.apache.commons.text.StrLookup<?> variableResolver, final org.apache.commons.text.StrMatcher prefixMatcher, final org.apache.commons.text.StrMatcher suffixMatcher, final char escape) {
        this(variableResolver, prefixMatcher, suffixMatcher, escape, org.apache.commons.text.StrSubstitutor.DEFAULT_VALUE_DELIMITER);
    }

    @java.lang.Deprecated
    public StrSubstitutor(final org.apache.commons.text.StrLookup<?> variableResolver, final org.apache.commons.text.StrMatcher prefixMatcher, final org.apache.commons.text.StrMatcher suffixMatcher, final char escape, final org.apache.commons.text.StrMatcher valueDelimiterMatcher) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefixMatcher(prefixMatcher);
        this.setVariableSuffixMatcher(suffixMatcher);
        this.setEscapeChar(escape);
        this.setValueDelimiterMatcher(valueDelimiterMatcher);
    }

    public StrSubstitutor(final org.apache.commons.text.lookup.StringLookup variableResolver) {
        this(variableResolver, org.apache.commons.text.StrSubstitutor.DEFAULT_PREFIX, org.apache.commons.text.StrSubstitutor.DEFAULT_SUFFIX, org.apache.commons.text.StrSubstitutor.DEFAULT_ESCAPE);
    }

    public StrSubstitutor(final org.apache.commons.text.lookup.StringLookup variableResolver, final java.lang.String prefix, final java.lang.String suffix, final char escape) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefix(prefix);
        this.setVariableSuffix(suffix);
        this.setEscapeChar(escape);
        this.setValueDelimiterMatcher(org.apache.commons.text.StrSubstitutor.DEFAULT_VALUE_DELIMITER);
    }

    public StrSubstitutor(final org.apache.commons.text.lookup.StringLookup variableResolver, final java.lang.String prefix, final java.lang.String suffix, final char escape, final java.lang.String valueDelimiter) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefix(prefix);
        this.setVariableSuffix(suffix);
        this.setEscapeChar(escape);
        this.setValueDelimiter(valueDelimiter);
    }

    public StrSubstitutor(final org.apache.commons.text.lookup.StringLookup variableResolver, final org.apache.commons.text.StrMatcher prefixMatcher, final org.apache.commons.text.StrMatcher suffixMatcher, final char escape) {
        this(variableResolver, prefixMatcher, suffixMatcher, escape, org.apache.commons.text.StrSubstitutor.DEFAULT_VALUE_DELIMITER);
    }

    public StrSubstitutor(final org.apache.commons.text.lookup.StringLookup variableResolver, final org.apache.commons.text.StrMatcher prefixMatcher, final org.apache.commons.text.StrMatcher suffixMatcher, final char escape, final org.apache.commons.text.StrMatcher valueDelimiterMatcher) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefixMatcher(prefixMatcher);
        this.setVariableSuffixMatcher(suffixMatcher);
        this.setEscapeChar(escape);
        this.setValueDelimiterMatcher(valueDelimiterMatcher);
    }

    public java.lang.String replace(final java.lang.String source) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(source);
        if (!(substitute(buf, 0, source.length()))) {
            return source;
        }
        return buf.toString();
    }

    public java.lang.String replace(final java.lang.String source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(length).append(source, offset, length);
        if (!(substitute(buf, 0, length))) {
            return source.substring(offset, (offset + length));
        }
        return buf.toString();
    }

    public java.lang.String replace(final char[] source) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(source.length).append(source);
        substitute(buf, 0, source.length);
        return buf.toString();
    }

    public java.lang.String replace(final char[] source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public java.lang.String replace(final java.lang.StringBuffer source) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public java.lang.String replace(final java.lang.StringBuffer source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public java.lang.String replace(final java.lang.CharSequence source) {
        if (source == null) {
            return null;
        }
        return replace(source, 0, source.length());
    }

    public java.lang.String replace(final java.lang.CharSequence source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public java.lang.String replace(final org.apache.commons.text.StrBuilder source) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public java.lang.String replace(final org.apache.commons.text.StrBuilder source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public java.lang.String replace(final java.lang.Object source) {
        if (source == null) {
            return null;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder().append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public boolean replaceIn(final java.lang.StringBuffer source) {
        if (source == null) {
            return false;
        }
        return replaceIn(source, 0, source.length());
    }

    public boolean replaceIn(final java.lang.StringBuffer source, final int offset, final int length) {
        if (source == null) {
            return false;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(length).append(source, offset, length);
        if (!(substitute(buf, 0, length))) {
            return false;
        }
        source.replace(offset, (offset + length), buf.toString());
        return true;
    }

    public boolean replaceIn(final java.lang.StringBuilder source) {
        if (source == null) {
            return false;
        }
        return replaceIn(source, 0, source.length());
    }

    public boolean replaceIn(final java.lang.StringBuilder source, final int offset, final int length) {
        if (source == null) {
            return false;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(length).append(source, offset, length);
        if (!(substitute(buf, 0, length))) {
            return false;
        }
        source.replace(offset, (offset + length), buf.toString());
        return true;
    }

    public boolean replaceIn(final org.apache.commons.text.StrBuilder source) {
        if (source == null) {
            return false;
        }
        return substitute(source, 0, source.length());
    }

    public boolean replaceIn(final org.apache.commons.text.StrBuilder source, final int offset, final int length) {
        if (source == null) {
            return false;
        }
        return substitute(source, offset, length);
    }

    protected boolean substitute(final org.apache.commons.text.StrBuilder buf, final int offset, final int length) {
        return (substitute(buf, offset, length, null)) > 0;
    }

    private int substitute(final org.apache.commons.text.StrBuilder buf, final int offset, final int length, java.util.List<java.lang.String> priorVariables) {
        final org.apache.commons.text.StrMatcher pfxMatcher = getVariablePrefixMatcher();
        final org.apache.commons.text.StrMatcher suffMatcher = getVariableSuffixMatcher();
        final char escape = getEscapeChar();
        final org.apache.commons.text.StrMatcher valueDelimMatcher = getValueDelimiterMatcher();
        final boolean substitutionInVariablesEnabled = isEnableSubstitutionInVariables();
        final boolean substitutionInValuesDisabled = isDisableSubstitutionInValues();
        final boolean top = priorVariables == null;
        boolean altered = false;
        int lengthChange = 0;
        char[] chars = buf.buffer;
        int bufEnd = offset + length;
        int pos = offset;
        final int startMatchLen = pfxMatcher.isMatch(chars, pos, offset, bufEnd);
        if (altered) {
            pos++;
        }else {
            if (this.preserveEscapes) {
                pos++;
                continue;
            }
            chars = buf.buffer;
            altered = true;
            final int startPos = pos;
            pos += startMatchLen;
            int endMatchLen = 0;
            int nestedVarCount = 0;
            while (pos < bufEnd) {
                if (substitutionInVariablesEnabled && ((pfxMatcher.isMatch(chars, pos, offset, bufEnd)) != 0)) {
                    endMatchLen = pfxMatcher.isMatch(chars, pos, offset, bufEnd);
                    nestedVarCount++;
                    pos += endMatchLen;
                    continue;
                }
                endMatchLen = suffMatcher.isMatch(chars, pos, offset, bufEnd);
                if (endMatchLen == 0) {
                    pos++;
                }else {
                    if (nestedVarCount == 0) {
                        java.lang.String varNameExpr = new java.lang.String(chars, (startPos + startMatchLen), ((pos - startPos) - startMatchLen));
                        if (substitutionInVariablesEnabled) {
                            final org.apache.commons.text.StrBuilder bufName = new org.apache.commons.text.StrBuilder(varNameExpr);
                            substitute(bufName, 0, bufName.length());
                            varNameExpr = bufName.toString();
                        }
                        pos += endMatchLen;
                        final int endPos = pos;
                        java.lang.String varName = varNameExpr;
                        java.lang.String varDefaultValue = null;
                        if (valueDelimMatcher != null) {
                            final char[] varNameExprChars = varNameExpr.toCharArray();
                            int valueDelimiterMatchLen = 0;
                            for (int i = 0; i < (varNameExprChars.length); i++) {
                                if ((!substitutionInVariablesEnabled) && ((pfxMatcher.isMatch(varNameExprChars, i, i, varNameExprChars.length)) != 0)) {
                                    break;
                                }
                                if ((valueDelimMatcher.isMatch(varNameExprChars, i)) != 0) {
                                    valueDelimiterMatchLen = valueDelimMatcher.isMatch(varNameExprChars, i);
                                    varName = varNameExpr.substring(0, i);
                                    varDefaultValue = varNameExpr.substring((i + valueDelimiterMatchLen));
                                    break;
                                }
                            }
                        }
                        if (priorVariables == null) {
                            priorVariables = new java.util.ArrayList<>();
                            priorVariables.add(new java.lang.String(chars, offset, length));
                        }
                        checkCyclicSubstitution(varName, priorVariables);
                        priorVariables.add(varName);
                        java.lang.String varValue = resolveVariable(varName, buf, startPos, endPos);
                        if (varValue == null) {
                            varValue = varDefaultValue;
                        }
                        if (varValue != null) {
                            final int varLen = varValue.length();
                            buf.replace(startPos, endPos, varValue);
                            altered = true;
                            int change = 0;
                            if (!substitutionInValuesDisabled) {
                                change = substitute(buf, startPos, varLen, priorVariables);
                            }
                            change = (change + varLen) - (endPos - startPos);
                            pos += change;
                            bufEnd += change;
                            lengthChange += change;
                            chars = buf.buffer;
                        }
                        priorVariables.remove(((priorVariables.size()) - 1));
                        break;
                    }
                    nestedVarCount--;
                    pos += endMatchLen;
                }
            } 
        }
        if (top) {
            return altered ? 1 : 0;
        }
        return lengthChange;
    }

    private void checkCyclicSubstitution(final java.lang.String varName, final java.util.List<java.lang.String> priorVariables) {
        if (!(priorVariables.contains(varName))) {
            return;
        }
        final org.apache.commons.text.StrBuilder buf = new org.apache.commons.text.StrBuilder(256);
        buf.append("Infinite loop in property interpolation of ");
        buf.append(priorVariables.remove(0));
        buf.append(": ");
        buf.appendWithSeparators(priorVariables, "->");
        throw new java.lang.IllegalStateException(buf.toString());
    }

    protected java.lang.String resolveVariable(final java.lang.String variableName, final org.apache.commons.text.StrBuilder buf, final int startPos, final int endPos) {
        final org.apache.commons.text.StrLookup<?> resolver = getVariableResolver();
        if (resolver == null) {
            return null;
        }
        return resolver.lookup(variableName);
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    public void setEscapeChar(final char escapeCharacter) {
        this.escapeChar = escapeCharacter;
    }

    public org.apache.commons.text.StrMatcher getVariablePrefixMatcher() {
        return prefixMatcher;
    }

    public org.apache.commons.text.StrSubstitutor setVariablePrefixMatcher(final org.apache.commons.text.StrMatcher prefixMatcher) {
        org.apache.commons.lang3.Validate.isTrue((prefixMatcher != null), "Variable prefix matcher must not be null!");
        this.prefixMatcher = prefixMatcher;
        return this;
    }

    public org.apache.commons.text.StrSubstitutor setVariablePrefix(final char prefix) {
        return setVariablePrefixMatcher(org.apache.commons.text.StrMatcher.charMatcher(prefix));
    }

    public org.apache.commons.text.StrSubstitutor setVariablePrefix(final java.lang.String prefix) {
        org.apache.commons.lang3.Validate.isTrue((prefix != null), "Variable prefix must not be null!");
        return setVariablePrefixMatcher(org.apache.commons.text.StrMatcher.stringMatcher(prefix));
    }

    public org.apache.commons.text.StrMatcher getVariableSuffixMatcher() {
        return suffixMatcher;
    }

    public org.apache.commons.text.StrSubstitutor setVariableSuffixMatcher(final org.apache.commons.text.StrMatcher suffixMatcher) {
        org.apache.commons.lang3.Validate.isTrue((suffixMatcher != null), "Variable suffix matcher must not be null!");
        this.suffixMatcher = suffixMatcher;
        return this;
    }

    public org.apache.commons.text.StrSubstitutor setVariableSuffix(final char suffix) {
        return setVariableSuffixMatcher(org.apache.commons.text.StrMatcher.charMatcher(suffix));
    }

    public org.apache.commons.text.StrSubstitutor setVariableSuffix(final java.lang.String suffix) {
        org.apache.commons.lang3.Validate.isTrue((suffix != null), "Variable suffix must not be null!");
        return setVariableSuffixMatcher(org.apache.commons.text.StrMatcher.stringMatcher(suffix));
    }

    public org.apache.commons.text.StrMatcher getValueDelimiterMatcher() {
        return valueDelimiterMatcher;
    }

    public org.apache.commons.text.StrSubstitutor setValueDelimiterMatcher(final org.apache.commons.text.StrMatcher valueDelimiterMatcher) {
        this.valueDelimiterMatcher = valueDelimiterMatcher;
        return this;
    }

    public org.apache.commons.text.StrSubstitutor setValueDelimiter(final char valueDelimiter) {
        return setValueDelimiterMatcher(org.apache.commons.text.StrMatcher.charMatcher(valueDelimiter));
    }

    public org.apache.commons.text.StrSubstitutor setValueDelimiter(final java.lang.String valueDelimiter) {
        if ((valueDelimiter == null) || ((valueDelimiter.length()) == 0)) {
            setValueDelimiterMatcher(null);
            return this;
        }
        return setValueDelimiterMatcher(org.apache.commons.text.StrMatcher.stringMatcher(valueDelimiter));
    }

    public org.apache.commons.text.StrLookup<?> getVariableResolver() {
        return ((org.apache.commons.text.StrLookup<?>) (this.variableResolver));
    }

    public void setVariableResolver(final org.apache.commons.text.StrLookup<?> variableResolver) {
        this.variableResolver = variableResolver;
    }

    public void setVariableResolver(final org.apache.commons.text.lookup.StringLookup variableResolver) {
        this.variableResolver = variableResolver;
    }

    public boolean isEnableSubstitutionInVariables() {
        return enableSubstitutionInVariables;
    }

    public void setEnableSubstitutionInVariables(final boolean enableSubstitutionInVariables) {
        this.enableSubstitutionInVariables = enableSubstitutionInVariables;
    }

    public boolean isDisableSubstitutionInValues() {
        return disableSubstitutionInValues;
    }

    public void setDisableSubstitutionInValues(boolean disableSubstitutionInValues) {
        this.disableSubstitutionInValues = disableSubstitutionInValues;
    }

    public boolean isPreserveEscapes() {
        return preserveEscapes;
    }

    public void setPreserveEscapes(final boolean preserveEscapes) {
        this.preserveEscapes = preserveEscapes;
    }
}