package com.ciscospark.androidsdk.message;

/**
 * A data type represents mention.
 * @since 1.4.0
 */
public abstract class Mention {

    /**
     * Mention one particular person by person Id.
     * @since 1.4.0
     */
    public static class MentionPerson extends Mention {
        public String personId;

        public MentionPerson(String personId) {
            this.personId = personId;
        }
    }

    /**
     * Mention all people in a room.
     * @since 1.4.0
     */
    public static class MentionAll extends Mention {

    }
}
