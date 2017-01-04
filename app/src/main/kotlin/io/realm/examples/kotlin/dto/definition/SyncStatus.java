package io.realm.examples.kotlin.dto.definition;


public enum SyncStatus {
    SYNC_SUCCESS(0),
    SYNC_ERROR(1),
    NEEDS_SYNC_CREATE(2),
    NEEDS_SYNC_UPDATE(3),
    NEEDS_SYNC_DELETE(4),
    SYNC_RETRY_CREATE(5),
    SYNC_RETRY_UPDATE(6),
    SYNC_RETRY_DELETE(7);

    private int statusCode;

    SyncStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Whether or not it needs sync, which could be any of these status:
     * {@link #NEEDS_SYNC_CREATE}, {@link #NEEDS_SYNC_UPDATE}, {@link #NEEDS_SYNC_DELETE}.
     *
     * @return
     */
    public boolean needsSync() {
        return this == NEEDS_SYNC_CREATE || this == NEEDS_SYNC_UPDATE || this == NEEDS_SYNC_DELETE;
    }

    public static SyncStatus getDefault() {
        return SYNC_SUCCESS;
    }

    public static SyncStatus getDefaultLocal() {
        return NEEDS_SYNC_CREATE;
    }

    public boolean hasError() {
        return this == SYNC_ERROR || this == SYNC_RETRY_CREATE || this == SYNC_RETRY_UPDATE || this == SYNC_RETRY_DELETE;
    }

    public boolean needsCreate() {
        return this == NEEDS_SYNC_CREATE || this == SYNC_RETRY_CREATE;
    }

    public boolean needsUpdate() {
        return this == NEEDS_SYNC_UPDATE || this == SYNC_RETRY_UPDATE;
    }

    public boolean needsDelete() {
        return this == NEEDS_SYNC_DELETE || this == SYNC_RETRY_DELETE;
    }

}
