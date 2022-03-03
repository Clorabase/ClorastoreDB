package com.clorabase.clorastore;

/**
 * This enum includes the reason for which {@link ClorastoreException} is thrown.
 */
public enum Reasons {
    ERROR_CREATING_DATABASE,
    ERROR_UNKNOWN,
    NO_COLLECTION_EXIST,
    NO_DOC_EXIST,
    IO_ERROR,
    INVALID_DATATYPE,
    DOC_SIZE_EXCEED
}
