package com.example.financialassessment;

import com.example.financialassessment.service.PdfService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class FinancialAssessmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialAssessmentApplication.class, args);

//		ConfigurableApplicationContext ctx = SpringApplication.run(FinancialAssessmentApplication.class, args);
//
//		PdfService pdfService = ctx.getBean(PdfService.class);
//		Map<String, Object> payload = new HashMap<>();
//		pdfService.generatePdf(payload);
	}

}

//UI: FOOTER[HAVE], CORRECT HEADER, FRONTEND+DB [ONE USER], RABBIT MQ
