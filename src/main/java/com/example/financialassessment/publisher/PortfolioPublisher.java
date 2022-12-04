package com.example.financialassessment.publisher;

import com.example.financialassessment.config.MessagingConfig;
//import com.example.financialassessment.dto.Portfolio;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
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
//        return "index_register";
    }

//    @PostMapping(value = "/register", consumes = {"application/json"})
//    public String submitForm(@RequestBody Map<String, Object> payload) {
//
////        System.out.println(user);
////        System.out.println(stockList);
//        System.out.println("heyaaaa");
//        System.out.println(payload);
////        MessageProperties properties = new MessageProperties();
////        properties.setContentType("application/json");
////        MessageBuilder.withBody().andProperties(properties).build();
////        template.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, payload);
//        try {
////            template.setEncoding("UTF-8");
//            template.convertAndSend(EXCHANGE, ROUTING_KEY, payload);
//            return "register_success";
//        }catch (Exception e){
//            return "register_success";
//        }
//    }

    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String submitForm(@RequestParam MultiValueMap<String, String > payload) {

//        System.out.println(user);
//        System.out.println(stockList);

        System.out.println(payload.get("name[first]"));

        System.out.println(payload.get("typea13"));

        List<String> str = payload.get("typea13");
        JSONArray array = new JSONArray(str);

        System.out.println(array);
        System.out.println(array.get(0));
        System.out.println(array.get(0).toString());

        JSONArray array1 = new JSONArray(array.get(0).toString());
        System.out.println(array1);
        System.out.println(array1.get(0));

//        System.out.println(array1.get(0).toString());


//        JSONObject object = new JSONObject(array.get(0));
//        System.out.println(object.toString());

//        System.out.println(array.getJSONArray(0));
//        System.out.println(array.getJSONObject(0));
//        System.out.println(array.getJSONArray(0).getJSONObject(0));


        System.out.println("heyaaaa");
        System.out.println(payload);
//        MessageProperties properties = new MessageProperties();
//        properties.setContentType("application/json");
//        MessageBuilder.withBody().andProperties(properties).build();
//        template.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, payload);
        try {
//            template.setEncoding("UTF-8");
            template.convertAndSend(EXCHANGE, ROUTING_KEY, payload);
            return "register_success";
        }catch (Exception e){
            return "register_success";
        }
    }

    @GetMapping("/registerSucess")
    public String getRegisterSuccess(){
        return "register_success";
    }


    @GetMapping("/error")
    public String getError(){
        return "portfolioForm";
    }


//    @GetMapping("/error")
//    public String getError(){
//        return "portfolioForm";
//    }


    @PostMapping("/error")
    public String postError(@RequestBody Map<String, Object> payload){
        return "portfolioForm";
    }


//    @PostMapping("/error")
//    public String postError(){
//        return "portfolioForm";
//    }
}
