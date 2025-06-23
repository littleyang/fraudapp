package configuration.mvc;

import com.sct.rest.SctAPIResult;
import com.sct.rest.utils.SctAPIResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

/**
 * 捕获 参数校验异常
 */
@ControllerAdvice
public class NotValidExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public SctAPIResult notValidExceptionHandler(MethodArgumentNotValidException e) {
        logger.error("请求参数校验异常！原因是：{}", e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        Optional<String> optional = bindingResult.getFieldErrors().stream().
                map(FieldError::getDefaultMessage)
                .reduce((s, s2) -> s + ";" + s2);
        return optional.map(SctAPIResultUtils::error)
                .orElseGet(() -> SctAPIResultUtils.error("参数校验异常"));
    }
}
