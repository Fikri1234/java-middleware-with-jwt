package com.ogya.Security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ogya.Common.TimeProvider;
import com.ogya.Controller.SigninController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * @author FIKRI-PC
 *
 */
@Component
public class JwtHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtHelper.class);
	
	@Value("${spring.application.name}")
    private String APP_NAME;
	
	//static final String APP_NAME = "pusiknas";
	static final String SECRET = "tessecret";
	static final int EXPIRES_IN = 3000000;
	static final String AUTH_HEADER = "Authorization";
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	TimeProvider timeProvider;

	private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
	
	
	
	public String getUsernameFromToken(String token) {
		logger.debug("[getUsernameFromToken] token: {}",token);
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        logger.debug("[getUsernameFromToken] user: {}",username);
        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (Exception e) {
            issueAt = null;
        }
        logger.debug("[getIssuedAtDateFromToken] issueAt: {}", issueAt);
        return issueAt;
    }

    public String refreshToken(String token) {
        String refreshedToken;
        Date a = timeProvider.now();
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            claims.setIssuedAt(a);
            refreshedToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith( SIGNATURE_ALGORITHM, SECRET )
                .compact();
        } catch (Exception e) {
            refreshedToken = null;
        }
        logger.debug("[refreshToken] refresh token: {}",refreshedToken);
        return refreshedToken;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setIssuer( APP_NAME )
                .setSubject(username)
                .setIssuedAt(timeProvider.now())
                .setExpiration(generateExpirationDate())
                .signWith( SIGNATURE_ALGORITHM, SECRET )
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Date generateExpirationDate() {
        //date data should be invoke from get API
        return new Date(timeProvider.now().getTime() + EXPIRES_IN + 10000);
    }

    // expired data should be invoke from get API
    public int getExpiredIn() {
        return  EXPIRES_IN;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
    	try {
	        User user = (User) userDetails;
	        final String username = getUsernameFromToken(token);
	        final Date created = getIssuedAtDateFromToken(token);
	        logger.debug("[validateToken] username: {}",username);
	        return (
	                username != null &&
	                username.equals(userDetails.getUsername())
	                /*&&
	                        !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())*/
	        );
    	} catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    public String getToken( HttpServletRequest request ) {
        /**
         *  Getting the token from Authentication header
         *  e.g Bearer your_token
         */
        String authHeader = getAuthHeaderFromHeader( request );
        logger.debug("[getToken] authH: {}",authHeader);
        if ( authHeader != null && authHeader.startsWith("Bearer ")) {
        	logger.debug("[getToken] if ");
            return authHeader.substring(7);
        }

        return null;
    }

    public String getAuthHeaderFromHeader( HttpServletRequest request ) {
        return request.getHeader(AUTH_HEADER);
}
}
