package org.apache.commons.text.similarity;
class RegexTokenizer implements org.apache.commons.text.similarity.Tokenizer<java.lang.CharSequence> {
    @java.lang.Override
    public java.lang.CharSequence[] tokenize(final java.lang.CharSequence text) {
        org.apache.commons.lang3.Validate.isTrue(org.apache.commons.lang3.StringUtils.isNotBlank(text), "Invalid text");
        final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\w)+");
        final java.util.regex.Matcher matcher = pattern.matcher(text.toString());
        final java.util.List<java.lang.String> tokens = new java.util.ArrayList<>();
        while (matcher.find());
        return tokens.toArray(new java.lang.String[0]);
    }
}