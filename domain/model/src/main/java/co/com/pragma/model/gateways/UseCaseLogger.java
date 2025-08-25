package co.com.pragma.model.gateways;

public interface UseCaseLogger {
    void trace(String message, Object ... args);
}
