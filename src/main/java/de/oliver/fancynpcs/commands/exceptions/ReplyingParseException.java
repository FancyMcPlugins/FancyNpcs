package de.oliver.fancynpcs.commands.exceptions;

import org.jetbrains.annotations.NotNull;

public final class ReplyingParseException extends RuntimeException {

    private final @NotNull Runnable runnable;

    private ReplyingParseException(final @NotNull Runnable runnable) {
        this.runnable = runnable;
    }

    public static ReplyingParseException replying(final Runnable runnable) {
        return new ReplyingParseException(runnable);
    }

    public @NotNull Runnable runnable() {
        return runnable;
    }

}
