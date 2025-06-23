package ru.slisarenko.pxelsoftware.exception;

public class AccountException extends RuntimeException {

    public AccountException(String message)
    {
        super(message);
    }

    public AccountException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
