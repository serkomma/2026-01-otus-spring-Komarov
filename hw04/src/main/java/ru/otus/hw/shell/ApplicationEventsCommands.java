package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.LocalizedMessagesService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.TestService;

@ShellComponent
@RequiredArgsConstructor
public class ApplicationEventsCommands {

    private Student student;

    private final TestService testService;

    private final ResultService resultService;

    private final LocalizedMessagesService localizedMessagesService;

    @ShellMethod(value = "Log in", key = {"l", "login"})
    public String login(
            @ShellOption({"-firstname", "-f"})String firstname,
            @ShellOption({"-lastname", "-l"})String lastname) {
        student =  new Student(firstname, lastname);
        return localizedMessagesService.getMessage("Shell.login.success");
    }

    @ShellMethod(value = "Test", key = {"t", "test"})
    @ShellMethodAvailability(value = "isLogged")
    public void test() {
        resultService.showResult(testService.executeTestFor(student));
    }

    private Availability isLogged() {
        if (student != null) {
            return Availability.available();
        } else  {
            return Availability.unavailable(localizedMessagesService.getMessage("Shell.test.error.login"));
        }
    }
}
