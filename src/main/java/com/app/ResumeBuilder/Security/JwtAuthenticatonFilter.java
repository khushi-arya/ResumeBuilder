package com.app.ResumeBuilder.Security;

import com.app.ResumeBuilder.Model.User;
import com.app.ResumeBuilder.Repo.UserRepo;
import com.app.ResumeBuilder.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticatonFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       String authheader = request.getHeader("Authorization");
       String token = null;
       String userid = null;

       if(authheader != null && authheader.startsWith("Bearer ")){
           token = authheader.substring(7);
           try {
               userid = jwtUtil.getUserIdFromToken(token);
           }catch (Exception e){
               logger.error("token is not valid");
           }
       }
       if(userid!=null && SecurityContextHolder.getContext().getAuthentication()==null){
         try{
             if(jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)){
                 User user = userRepo.findById(userid).orElseThrow(()-> new UsernameNotFoundException("User not found"));
                 UsernamePasswordAuthenticationToken authToken =
                         new UsernamePasswordAuthenticationToken(
                                 user,
                                 null,
                                 List.of(new SimpleGrantedAuthority("ROLE_USER"))
                         );

                 authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(authToken);
             }
         }catch (Exception e ){
             logger.error("token is not valid");
         }
       }
       filterChain.doFilter(request,response);


    }
}
