package com.example.financialassessment.publisher;

import com.example.financialassessment.config.MessagingConfig;
//import com.example.financialassessment.dto.Portfolio;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class PortfolioPublisher {

    @Autowired
    private RabbitTemplate template;

    @Value("${EXCHANGE}")
    private String EXCHANGE;

    @Value("${ROUTING_KEY}")
    private String ROUTING_KEY;

//    @PostMapping("/")
//    public String createPortfolio(@RequestBody Portfolio portfolio){
//        template.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, portfolio);
//        return "redirect to a page displaying koshantra details";
//    }

    @GetMapping("/register")
    public String getSomething(Model model){
//        Portfolio user = new Portfolio();
//        model.addAttribute("user", user);

//        Stock stock = new Stock();
//        model.addAttribute("stock",stock);
//
//        StockList stockList = new StockList();
//        model.addAttribute("stockList", stockList);
//
//        List<String> listProfession = Arrays.asList("Developer", "Tester", "Architect");
//        model.addAttribute("listProfession", listProfession);

        return "portfolioForm";
    }

    @PostMapping("/register")
    public String submitForm(@RequestBody Map<String, Object> payload) {

//        System.out.println(user);
//        System.out.println(stockList);
        System.out.println("heyaaaa");
        System.out.println(payload);
//        MessageProperties properties = new MessageProperties();
//        properties.setContentType("application/json");
//        MessageBuilder.withBody(body.getBytes()).andProperties(properties).build()
//        template.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, payload);
        template.convertAndSend(EXCHANGE, ROUTING_KEY, payload);
        return "register_success";
    }

    @GetMapping("/registerSucess")
    public String getRegisterSuccess(){
        return "register_success";
    }
}
