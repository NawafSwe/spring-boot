package payroll;

//When an EmployeeNotFoundException is thrown, this extra tidbit of Spring MVC configuration is used to render an HTTP 404:
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class EmployeeNotFoundAdvice {

    //@ResponseBody signals that this advice is rendered straight into the response body.
    @ResponseBody
    //@ExceptionHandler configures the advice to only respond if an EmployeeNotFoundException is thrown.
    @ExceptionHandler(EmployeeNotFoundException.class)

    //@ResponseStatus says to issue an HttpStatus.NOT_FOUND, i.e. an HTTP 404.
    @ResponseStatus(HttpStatus.NOT_FOUND)

    String employeeNotFoundHandler(EmployeeNotFoundException ex) {
        //ex extending Runtime Exception class , getMessage() callable
        return ex.getMessage();
    }
}
