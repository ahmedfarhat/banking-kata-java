package com.optivem.kata.banking.core.usecases;

import com.optivem.kata.banking.core.domain.exceptions.ValidationException;
import com.optivem.kata.banking.core.domain.exceptions.ValidationMessages;
import com.optivem.kata.banking.core.usecases.openaccount.OpenAccountRequest;
import com.optivem.kata.banking.core.usecases.openaccount.OpenAccountResponse;
import com.optivem.kata.banking.core.usecases.openaccount.OpenAccountUseCase;
import com.optivem.kata.banking.infra.fake.generators.FakeAccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OpenAccountUseCaseTest {

    private FakeAccountNumberGenerator accountNumberGenerator;
    private OpenAccountUseCase useCase;

    @BeforeEach
    void init() {
        this.accountNumberGenerator = new FakeAccountNumberGenerator();
        this.useCase = new OpenAccountUseCase(accountNumberGenerator);
    }

    @ParameterizedTest
    @MethodSource
    void should_open_account_when_request_is_valid(String firstName, String lastName, int initialBalance, String accountNumber) {
        accountNumberGenerator.add(accountNumber);

        var request = new OpenAccountRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setInitialBalance(initialBalance);

        var expectedResponse = new OpenAccountResponse();
        expectedResponse.setAccountNumber(accountNumber);

        var response = useCase.handle(request);

        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> should_open_account_when_request_is_valid() {
        return Stream.of(Arguments.of("John", "Smith", 0, "GB41OMQP68570038161775"),
                Arguments.of("Mary", "McDonald", 50, "GB36BMFK75394735916876"));
    }

    @ParameterizedTest
    @MethodSource
    void should_throw_exception_when_first_name_is_empty(String firstName) {
        var request = new OpenAccountRequest();
        request.setFirstName(firstName);

        var exception = assertThrows(ValidationException.class, () -> useCase.handle(request));

        assertThat(exception.getMessage()).isEqualTo(ValidationMessages.FIRST_NAME_EMPTY);
    }

    private static Stream<String> should_throw_exception_when_first_name_is_empty() {
        return Stream.of(null, "", " ", "   ");
    }

    @ParameterizedTest
    @MethodSource
    void should_throw_exception_when_last_name_is_empty(String lastName) {
        var request = new OpenAccountRequest();
        request.setFirstName("John");
        request.setLastName(lastName);

        var exception = assertThrows(ValidationException.class, () -> useCase.handle(request));

        assertThat(exception.getMessage()).isEqualTo(ValidationMessages.LAST_NAME_EMPTY);
    }

    private static Stream<String> should_throw_exception_when_last_name_is_empty() {
        return Stream.of(null, "", " ", "   ");
    }

    @ParameterizedTest
    @MethodSource
    void should_throw_exception_when_initial_balance_is_negative(int balance) {
        var request = new OpenAccountRequest();
        request.setFirstName("John");
        request.setLastName("Smith");
        request.setInitialBalance(balance);

        var exception = assertThrows(ValidationException.class, () -> useCase.handle(request));

        assertThat(exception.getMessage()).isEqualTo(ValidationMessages.INITIAL_BALANCE_NEGATIVE);
    }

    private static Stream<Integer> should_throw_exception_when_initial_balance_is_negative() {
        return Stream.of(-1, -2, -10);
    }
}