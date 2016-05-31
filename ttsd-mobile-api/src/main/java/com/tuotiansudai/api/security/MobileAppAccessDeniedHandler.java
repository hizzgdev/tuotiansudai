package com.tuotiansudai.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuotiansudai.api.dto.BaseResponseDto;
import com.tuotiansudai.api.dto.ReturnMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileAppAccessDeniedHandler extends AccessDeniedHandlerImpl {
    static Logger log = Logger.getLogger(MobileAppAccessDeniedHandler.class);
    @Autowired
    private MobileAppTokenProvider mobileAppTokenProvider;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(request);

        if (!StringUtils.isEmpty(bufferedRequest.getInputStreamString())) {
            Pattern pattern = Pattern.compile("\"token\":\"(app-token:[\\w]+:[-\\w]+)\"");
            Matcher matcher = pattern.matcher(bufferedRequest.getInputStreamString());
            if (matcher.find()) {
                String requestToken = matcher.group(1);
                String redisLoginName = mobileAppTokenProvider.getUserNameByToken(requestToken);
                log.debug(MessageFormat.format("[Authentication Entry Point] uri: {0} body: {1} searchToken: {2} redisTokenLoginName:{3}", request.getRequestURI(), bufferedRequest.getInputStreamString(), requestToken, redisLoginName));
            } else {
                log.debug(MessageFormat.format("[Authentication Entry Point] uri: {0} body: {1}", request.getRequestURI(), bufferedRequest.getInputStreamString()));
            }
        }

        BaseResponseDto dto = mobileAppTokenProvider.generateResponseDto(ReturnMessage.UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter out = response.getWriter();
        String jsonString = objectMapper.writeValueAsString(dto);
        out.print(jsonString);
    }
}