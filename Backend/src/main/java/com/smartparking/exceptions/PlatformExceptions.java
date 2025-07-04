package com.smartparking.exceptions;

// This class contains custom exceptions for the Smart Parking application.
public class PlatformExceptions{
    public static class ExistentEmailException extends RuntimeException {
        public ExistentEmailException(String message) {
            super(message);
        }
    }//existent email exception 

    public static class ExistentSpotException extends RuntimeException {
        public ExistentSpotException(String message) {
            super(message);
        }
    }//existent spot exception

    public static class ReservationConflictException extends RuntimeException {
        public ReservationConflictException(String message) {
            super(message);
        }
    }//reservation conflict exception

}//platform exceptions class

