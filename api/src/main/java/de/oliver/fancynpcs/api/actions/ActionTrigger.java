package de.oliver.fancynpcs.api.actions;

public enum ActionTrigger {
    /**
     * represents any click interaction by a player.
     */
    ANY_CLICK,
    /**
     * represents a left click interaction by a player.
     */
    LEFT_CLICK,
    /**
     * represents a right click interaction by a player.
     */
    RIGHT_CLICK,
    /**
     * represents interactions invoked by the API.
     */
    CUSTOM,
    ;

    /**
     * Gets the ActionTrigger by its name.
     *
     * @param name the name of the ActionTrigger
     * @return the ActionTrigger or null if not found
     */
    public static ActionTrigger getByName(final String name) {
        for (ActionTrigger trigger : values()) {
            if (trigger.name().equalsIgnoreCase(name)) {
                return trigger;
            }
        }
        return null;
    }
}
