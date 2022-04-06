package com.example.financialassessment.consumer;

import com.example.financialassessment.config.MessagingConfig;
//import com.example.financialassessment.dto.*;
import com.example.financialassessment.dto.Portfolio;
import com.example.financialassessment.service.PdfService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PortfolioConsumer {

    @Autowired PdfService pdfService;
//
//    @RabbitListener(queues = MessagingConfig.QUEUE)
//    public void consumeQueue(String msg){
//        System.out.println("CONSUMER: " + msg);
//    }

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeQueue(Map<String, Object> payload){

        try {
            if (!payload.isEmpty()) {
                System.out.println("sending data for pdf........");
                pdfService.generatePdf(payload);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

//        Portfolio portfolio = new Portfolio();
//        portfolio.setFirstName(payload.get("first_name").toString());
//        portfolio.setLastName(payload.get("last_name").toString());
//        portfolio.setEmail(payload.get("email").toString());
//        portfolio.setGender(payload.get("gender").toString());
//        portfolio.setDob((Date) payload.get("dob"));
////        portfolio.setProfession(payload.get("profession").toString());
////        portfolio.setAddress(payload.get("address").toString());
////        portfolio.setExpense(payload.get("expense").toString());
////        portfolio.setIncome(payload.get("income").toString());
////        portfolio.setMobileNo(payload.get("mobile_no").toString());
//        portfolio.setRiskProfile(payload.get("risk_profile").toString());
//        portfolio.setLifeGoals(payload.get("risk_profile").toString());
////        portfolio.setMarried(payload.get("Married").toString().equals("Married"));
//
//        PdfService service = new PdfService();
//        service.displayPortfolio(portfolio);

//        List<Stock> stocks = new ArrayList<>();
        System.out.println(payload.get("stocks_name").toString());
        ArrayList<String> snames = (ArrayList<String>) payload.get("stocks_name");
        System.out.println(snames);
//        ArrayList<String> stock_names = Stream.of(payload.get("stocks_name")).map(Object::toString).collect(Collectors.toList());


//        List<MF> mfs = new ArrayList<>();
//        List<LifeInsurance> lifeInsurances = new ArrayList<>();
//        List<HealthInsurance> healthInsurances = new ArrayList<>();
//        List<Bonds> bonds = new ArrayList<>();
//        List<FD> fds = new ArrayList<>();
//        List<PPF> ppfs = new ArrayList<>();
//        List<loanEmi> loanEmis = new ArrayList<>();


        System.out.println("CONSUMER: " + payload);
    }
}
