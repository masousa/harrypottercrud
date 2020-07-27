package br.com.teste.harryPotter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class Boot
{

   public static void main(String[] args)
   {
      SpringApplication.run(Boot.class, args);
   }

   @GetMapping("/")
   @ResponseBody
   public String home()
   {
      return "vai para  <a href='http://localhost:3000/swagger-ui.html'> inicial</a> para mais informações";
   }
}
