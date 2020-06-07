package nl.backbase.game.kalah.aop;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.game.kalah.utils.GlobalConstants;
import nl.backbase.game.kalah.utils.IdGeneratorUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Aspect
@Component
public class AspectLogging {

    private static final String EXCEPTION_LOG_PATTERN = "Exception while processing a %s request on the URI %s from class %s:%s with message %s";
    private static final String ENTER_CONTROLLER_LOG_PATTERN = "Received a {} request on the endpoint {}";
    private static final String EXIT_CONTROLLER_LOG_PATTERN = "Success response sent to the {} {} request";
    private static final String ENTER_SERVICE_LOG_PATTERN = "ENTER :: {}.{}() :: Proccessing request";
    private static final String EXIT_SERVICE_LOG_PATTERN = "EXIT :: {}.{}() :: Processing completed";


    /**
     * This method is invokes by aspect based on execution expression. Method is
     * used to (INFO) log a Enter and exit/Exception log of a presentation Add
     * flowId, sessionId and requestId in slf4j MDC to include in logs
     *
     * @param proceedingJoinPoint {@link ProceedingJoinPoint}
     * @return output of the method
     * @throws Throwable when the method raises an Exception
     */
    @Around("execution( * nl.backbase.game.kalah.controller.GameController.* (..))")
    public Object logControllers(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest();
            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String gameId = pathVariables.get("gameId");

            MDC.put(GlobalConstants.GAME_ID_LOGGER_MDC_KEY, gameId == null || gameId == "" ? GlobalConstants.NOT_APPLICABLE :
                    gameId);
            MDC.put(GlobalConstants.REQUEST_ID_LOGGER_MDC_KEY, IdGeneratorUtil.generateRequestId());
            Optional<HttpServletRequest> contextOptional = getRequestFromContext();
            log.info(ENTER_CONTROLLER_LOG_PATTERN, proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                    proceedingJoinPoint.getSignature().getName(),
                    contextOptional.isPresent() ? contextOptional.get().getMethod() : GlobalConstants.NOT_APPLICABLE,
                    contextOptional.isPresent() ? contextOptional.get().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) : GlobalConstants.NOT_APPLICABLE);

            Object result = proceedingJoinPoint.proceed();

            log.info(EXIT_CONTROLLER_LOG_PATTERN, proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                    proceedingJoinPoint.getSignature().getName(),
                    contextOptional.isPresent() ? contextOptional.get().getMethod() : GlobalConstants.NOT_APPLICABLE,
                    contextOptional.isPresent() ? contextOptional.get().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) : GlobalConstants.NOT_APPLICABLE);
            return result;
        } catch (Throwable th) {
            Optional<HttpServletRequest> contextOptional = getRequestFromContext();
            log.error(String.format(EXCEPTION_LOG_PATTERN,
                    contextOptional.isPresent() ? contextOptional.get().getMethod() : GlobalConstants.NOT_APPLICABLE,
                    contextOptional.isPresent() ? contextOptional.get().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) : GlobalConstants.NOT_APPLICABLE,
                    proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                    proceedingJoinPoint.getSignature().getName(), th));
            throw th;
        } finally {
            MDC.clear();
        }
    }


    /**
     * This method is invokes by aspect based on execution expression. Method is
     * used to (Debug) log a Enter and exit log of a services Add requestId and
     * SessionId in slf4j MDC to include in logs
     *
     * @param proceedingJoinPoint : {@link ProceedingJoinPoint}
     * @return : output of the method
     * @throws Throwable any exception from the actual execution of the method
     */
    @Around("execution( * nl.backbase.game.kalah.service.GameServiceImpl.* (..))")
    public static Object logService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.debug(ENTER_SERVICE_LOG_PATTERN, proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                proceedingJoinPoint.getSignature().getName());

        Object result = proceedingJoinPoint.proceed();

        log.debug(EXIT_SERVICE_LOG_PATTERN, proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                proceedingJoinPoint.getSignature().getName());
        return result;

    }

    private Optional<HttpServletRequest> getRequestFromContext() {
        Optional<ServletRequestAttributes> attributesOptional = Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        return attributesOptional.map(ServletRequestAttributes::getRequest);
    }


}
