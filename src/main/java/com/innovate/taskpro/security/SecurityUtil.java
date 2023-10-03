package com.innovate.taskpro.security;

import com.innovate.taskpro.UserDto;
import com.innovate.taskpro.entity.UserBO;
import com.innovate.taskpro.mapper.UserDetailsMapper;
import com.innovate.taskpro.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.Random;
import java.util.function.Supplier;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
@Component
public class SecurityUtil {

    @Value("${auction.app.token.secret}")
    private String tokenSecret;

    @Value("${auction.app.token.expiration}")
    private long tokenExpiry;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsMapper userDetailsMapper;

    private static final String TOKEN_HEADER_NAME = "token";

    private Random random = new Random();

    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public String parseToken(HttpServletRequest request)  {
        String token = request.getHeader(TOKEN_HEADER_NAME);

        if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7, token.length());
        }

        return null;
    }

    public boolean validateToken(String token ) {
        try {
            Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}",  e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}",  e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}",  e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}",  e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}",  e.getMessage());
        }
        return false;
    }

    public UserDto getUserFromToken(String token) throws UsernameNotFoundException {
        logger.debug("Get user details from security token");
        String username = Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token).getBody().getSubject();
        Supplier<UsernameNotFoundException> sup = () -> new UsernameNotFoundException("The requested user is not found");
        UserBO user = userRepository.findByUserName(username).orElseThrow(sup);
        return userDetailsMapper.mapUserBoToDto(user);
    }

    public String generateToken(UserDto userprincipal) {
        return Jwts.builder()
                .setSubject(userprincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + tokenExpiry))
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    public String generateUserId() {
        String username = generateRandomAlphanumeric(3,5);

        while(userRepository.existsByUserName(username)) {
            username = generateRandomAlphanumeric(3,5);
        }

        return username;
    }

    private String generateRandomAlphanumeric(int lengthOfAlphabet, int lengthOfNumeric) {

        int alphabetLowerLimit = 97; // numeral 'a'
        int alphabetUpperLimit = 122; // numeral 'z'

        String alphabbetString = random.ints(alphabetLowerLimit, alphabetUpperLimit + 1)
                .limit(lengthOfAlphabet)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        int integerLowerLimit = 48; // numeral '0'
        int integerUpperLimit = 57; // numeral '9'

        String integerString = random.ints(integerLowerLimit, integerUpperLimit + 1)
                .limit(lengthOfNumeric)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return alphabbetString.concat(integerString);
    }

    public UserDto getUserFromContext() {
        TokenBasedAuthenticationToken authenticatedUser =  (TokenBasedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authenticatedUser != null ? (UserDto) authenticatedUser.getPrincipal() : null;
    }
}
