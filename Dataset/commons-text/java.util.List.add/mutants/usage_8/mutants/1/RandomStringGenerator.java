package org.apache.commons.text;
public final class RandomStringGenerator {
    private final int minimumCodePoint;

    private final int maximumCodePoint;

    private final java.util.Set<org.apache.commons.text.CharacterPredicate> inclusivePredicates;

    private final org.apache.commons.text.TextRandomProvider random;

    private final java.util.List<java.lang.Character> characterList;

    private RandomStringGenerator(final int minimumCodePoint, final int maximumCodePoint, final java.util.Set<org.apache.commons.text.CharacterPredicate> inclusivePredicates, final org.apache.commons.text.TextRandomProvider random, final java.util.List<java.lang.Character> characterList) {
        this.minimumCodePoint = minimumCodePoint;
        this.maximumCodePoint = maximumCodePoint;
        this.inclusivePredicates = inclusivePredicates;
        this.random = random;
        this.characterList = characterList;
    }

    private int generateRandomNumber(final int minInclusive, final int maxInclusive) {
        if ((random) != null) {
            return (random.nextInt(((maxInclusive - minInclusive) + 1))) + minInclusive;
        }
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(minInclusive, (maxInclusive + 1));
    }

    private int generateRandomNumber(final java.util.List<java.lang.Character> characterList) {
        final int listSize = characterList.size();
        if ((random) != null) {
            return java.lang.String.valueOf(characterList.get(random.nextInt(listSize))).codePointAt(0);
        }
        return java.lang.String.valueOf(characterList.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(0, listSize))).codePointAt(0);
    }

    public java.lang.String generate(final int length) {
        if (length == 0) {
            return "";
        }
        org.apache.commons.lang3.Validate.isTrue((length > 0), "Length %d is smaller than zero.", length);
        final java.lang.StringBuilder builder = new java.lang.StringBuilder(length);
        long remaining = length;
        do {
            int codePoint;
            if (((characterList) != null) && (!(characterList.isEmpty()))) {
                codePoint = generateRandomNumber(characterList);
            }else {
                codePoint = generateRandomNumber(minimumCodePoint, maximumCodePoint);
            }
            switch (java.lang.Character.getType(codePoint)) {
                case java.lang.Character.UNASSIGNED :
                case java.lang.Character.PRIVATE_USE :
                case java.lang.Character.SURROGATE :
                    continue;
                default :
            }
            if ((inclusivePredicates) != null) {
                boolean matchedFilter = false;
                for (final org.apache.commons.text.CharacterPredicate predicate : inclusivePredicates) {
                    if (predicate.test(codePoint)) {
                        matchedFilter = true;
                        break;
                    }
                }
                if (!matchedFilter) {
                    continue;
                }
            }
            builder.appendCodePoint(codePoint);
            remaining--;
        } while (remaining != 0 );
        return builder.toString();
    }

    public java.lang.String generate(final int minLengthInclusive, final int maxLengthInclusive) {
        org.apache.commons.lang3.Validate.isTrue((minLengthInclusive >= 0), "Minimum length %d is smaller than zero.", minLengthInclusive);
        org.apache.commons.lang3.Validate.isTrue((minLengthInclusive <= maxLengthInclusive), "Maximum length %d is smaller than minimum length %d.", maxLengthInclusive, minLengthInclusive);
        return generate(generateRandomNumber(minLengthInclusive, maxLengthInclusive));
    }

    public static class Builder implements org.apache.commons.text.Builder<org.apache.commons.text.RandomStringGenerator> {
        public static final int DEFAULT_MAXIMUM_CODE_POINT = java.lang.Character.MAX_CODE_POINT;

        public static final int DEFAULT_LENGTH = 0;

        public static final int DEFAULT_MINIMUM_CODE_POINT = 0;

        private int minimumCodePoint = org.apache.commons.text.RandomStringGenerator.Builder.DEFAULT_MINIMUM_CODE_POINT;

        private int maximumCodePoint = org.apache.commons.text.RandomStringGenerator.Builder.DEFAULT_MAXIMUM_CODE_POINT;

        private java.util.Set<org.apache.commons.text.CharacterPredicate> inclusivePredicates;

        private org.apache.commons.text.TextRandomProvider random;

        private java.util.List<java.lang.Character> characterList;

        public org.apache.commons.text.RandomStringGenerator.Builder withinRange(final int minimumCodePoint, final int maximumCodePoint) {
            org.apache.commons.lang3.Validate.isTrue((minimumCodePoint <= maximumCodePoint), "Minimum code point %d is larger than maximum code point %d", minimumCodePoint, maximumCodePoint);
            org.apache.commons.lang3.Validate.isTrue((minimumCodePoint >= 0), "Minimum code point %d is negative", minimumCodePoint);
            org.apache.commons.lang3.Validate.isTrue((maximumCodePoint <= (java.lang.Character.MAX_CODE_POINT)), "Value %d is larger than Character.MAX_CODE_POINT.", maximumCodePoint);
            this.minimumCodePoint = minimumCodePoint;
            this.maximumCodePoint = maximumCodePoint;
            return this;
        }

        public org.apache.commons.text.RandomStringGenerator.Builder withinRange(final char[]... pairs) {
            characterList = new java.util.ArrayList<>();
            for (final char[] pair : pairs) {
                org.apache.commons.lang3.Validate.isTrue(((pair.length) == 2), "Each pair must contain minimum and maximum code point");
                final int minimumCodePoint = pair[0];
                final int maximumCodePoint = pair[1];
                org.apache.commons.lang3.Validate.isTrue((minimumCodePoint <= maximumCodePoint), "Minimum code point %d is larger than maximum code point %d", minimumCodePoint, maximumCodePoint);
                for (int index = minimumCodePoint; index <= maximumCodePoint; index++) {
                    characterList.add(((char) (index)));
                }
            }
            return this;
        }

        public org.apache.commons.text.RandomStringGenerator.Builder filteredBy(final org.apache.commons.text.CharacterPredicate... predicates) {
            if ((predicates == null) || ((predicates.length) == 0)) {
                inclusivePredicates = null;
                return this;
            }
            if ((inclusivePredicates) == null) {
                inclusivePredicates = new java.util.HashSet<>();
            }else {
                inclusivePredicates.clear();
            }
            java.util.Collections.addAll(inclusivePredicates, predicates);
            return this;
        }

        public org.apache.commons.text.RandomStringGenerator.Builder usingRandom(final org.apache.commons.text.TextRandomProvider random) {
            this.random = random;
            return this;
        }

        public org.apache.commons.text.RandomStringGenerator.Builder selectFrom(final char... chars);

        @java.lang.Override
        public org.apache.commons.text.RandomStringGenerator build() {
            return new org.apache.commons.text.RandomStringGenerator(minimumCodePoint, maximumCodePoint, inclusivePredicates, random, characterList);
        }
    }
}