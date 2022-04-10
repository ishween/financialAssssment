package com.example.financialassessment.service;

import com.example.financialassessment.dto.Portfolio;
import com.example.financialassessment.util.EmailSendingUtil;
import com.example.financialassessment.util.PdfGeneratorUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.expression.Lists;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PdfService {

    @Autowired private PdfGeneratorUtil pdfGeneratorUtil;

    public void generatePdf(Map<String, Object> payload){

        ArrayList<String> snames = (ArrayList<String>) payload.get("stocks_name");

        System.out.println("service payload: " + payload);

        List<String> headers = Arrays.asList("ID", "Name", "Salary", "Status");
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(Map.of("ID", "1", "Name", "Jim", "Salary", "50000", "Status", "active"));
        rows.add(Map.of("ID", "2", "Name", "Sally", "Salary", "50000", "Status", "inactive"));

        Map<String,Object> data = new HashMap<>();
        data.put("headers", headers);
        data.put("rows", rows);

        Map<String,Object> koshantra_data = new HashMap<>();
        koshantra_data.put("first_name", payload.get("first_name"));
        koshantra_data.put("last_name", payload.get("last_name"));
        koshantra_data.put("dob", payload.get("dob"));
        koshantra_data.put("profession", payload.get("profession"));
        koshantra_data.put("income", payload.get("income"));
        koshantra_data.put("expense", payload.get("expense"));
        koshantra_data.put("mobile_no", payload.get("mobile_no"));
        koshantra_data.put("gender", payload.get("gender"));
        koshantra_data.put("email", payload.get("email"));
        koshantra_data.put("address", payload.get("address"));
        koshantra_data.put("risk_profile", payload.get("risk_profile"));
        koshantra_data.put("life_goals", payload.get("life_goals"));

        koshantra_data.put("number_of_dependents", payload.get("number_of_dependents"));
        List<String> dependentsColumns = Arrays.asList("Name", "DOB", "Relation");
        List<Map<String,Object>> dependentsData = new ArrayList<>();
        koshantra_data.put("dependentsColumns", dependentsColumns);
        koshantra_data.put("dependentsData", dependentsData);

        List<String> stocksColumns = Arrays.asList("Name", "Amount", "Market Value", "Date of Purchase", "Frequency");
        List<Map<String,Object>> stocksData = new ArrayList<>();
        ArrayList<String> stocks_name = (ArrayList<String>) payload.get("stocks_name");
        ArrayList<String> stock_amount = (ArrayList<String>) payload.get("stocks_amount");
        ArrayList<String> stocks_market_value = (ArrayList<String>) payload.get("stocks_market_value");
        ArrayList<String> stocks_date_of_purchase = (ArrayList<String>) payload.get("stocks_date_of_purchase");
        ArrayList<String> stocks_frequency = (ArrayList<String>) payload.get("stocks_frequency");
        for(int i=0;i<stocks_name.size(); i++){
            stocksData.add(Map.of("Name", stocks_name.get(i),
                    "Amount", stock_amount.get(i),
                    "Market Value", stocks_market_value.get(i),
                    "Date of Purchase", stocks_date_of_purchase.get(i),
                    "Frequency", stocks_frequency.get(i)));
        }
        koshantra_data.put("stocksColumns", stocksColumns);
        koshantra_data.put("stocksData", stocksData);

        List<String> mfsColumns = Arrays.asList("Market Value", "Amount", "Type", "Date of Purchase", "Frequency");
        List<Map<String,Object>> mfsData = new ArrayList<>();
        ArrayList<String> mfs_mkt_value = (ArrayList<String>) payload.get("mf_mkt_value");
        ArrayList<String> mf_amount = (ArrayList<String>) payload.get("mf_amount");
        ArrayList<String> mfs_type = (ArrayList<String>) payload.get("mf_type");
        ArrayList<String> mfs_date_of_purchase = (ArrayList<String>) payload.get("mf_date_of_purchase");
        ArrayList<String> mfs_frequency = (ArrayList<String>) payload.get("mf_frequency");
        for(int i=0;i<mfs_frequency.size(); i++){
            mfsData.add(Map.of("Type", mfs_type.get(i),
                    "Amount", mf_amount.get(i),
                    "Market Value", mfs_mkt_value.get(i),
                    "Date of Purchase", mfs_date_of_purchase.get(i),
                    "Frequency", mfs_frequency.get(i)));
        }
        koshantra_data.put("mfsColumns", mfsColumns);
        koshantra_data.put("mfsData", mfsData);

        List<String> lisColumns = Arrays.asList("Start Date", "Premium Paying Term", "Type", "Sum Insured", "Name", "Policy Term");
        List<Map<String,Object>> lisData = new ArrayList<>();
        ArrayList<String> lis_start_date = (ArrayList<String>) payload.get("li_start_date");
        ArrayList<String> li_premium_paying_term = (ArrayList<String>) payload.get("li_premium_paying_term");
        ArrayList<String> li_type = (ArrayList<String>) payload.get("li_type");
        ArrayList<String> li_sum_insured = (ArrayList<String>) payload.get("li_sum_insured");
        ArrayList<String> li_name = (ArrayList<String>) payload.get("li_name");
        ArrayList<String> li_policy_term = (ArrayList<String>) payload.get("li_policy_term");
        for(int i=0;i<li_name.size(); i++){
            lisData.add(Map.of("Name", li_name.get(i),
                    "Start Date", lis_start_date.get(i),
                    "Premium Paying Term", li_premium_paying_term.get(i),
                    "Sum Insured", li_sum_insured.get(i),
                    "Type", li_type.get(i),
                    "Policy Term", li_policy_term.get(i)));
        }
        koshantra_data.put("lisColumns", lisColumns);
        koshantra_data.put("lisData", lisData);

        List<String> hisColumns = Arrays.asList("Name", "Premium Amount", "Type", "Sum Insured", "Start Date");
        List<Map<String,Object>> hisData = new ArrayList<>();
        ArrayList<String> hi_premium_amount = (ArrayList<String>) payload.get("hi_premium_amount");
        ArrayList<String> hi_name = (ArrayList<String>) payload.get("hi_name");
        ArrayList<String> hi_type = (ArrayList<String>) payload.get("hi_type");
        ArrayList<String> hi_start_date = (ArrayList<String>) payload.get("hi_start_date");
        ArrayList<String> hi_sum_insured = (ArrayList<String>) payload.get("hi_sum_insured");
        for(int i=0;i<hi_start_date.size(); i++){
            hisData.add(Map.of("Name", hi_name.get(i),
                    "Premium Amount", hi_premium_amount.get(i),
                    "Type", hi_type.get(i),
                    "Sum Insured", hi_sum_insured.get(i),
                    "Start Date", hi_start_date.get(i)));
        }
        koshantra_data.put("hisColumns", hisColumns);
        koshantra_data.put("hisData", hisData);

        List<String> bondsColumns = Arrays.asList("Name", "Invested", "Maturity Date", "Date of Investment");
        List<Map<String,Object>> bondsData = new ArrayList<>();
        ArrayList<String> bonds_name = (ArrayList<String>) payload.get("bonds_name");
        ArrayList<String> bond_invested = (ArrayList<String>) payload.get("bonds_invested");
        ArrayList<String> bonds_date_of_investment = (ArrayList<String>) payload.get("bonds_date_of_investment");
        ArrayList<String> bonds_maturity_date = (ArrayList<String>) payload.get("bonds_maturity_date");
        for(int i=0;i<bonds_name.size(); i++){
            bondsData.add(Map.of("Name", bonds_name.get(i),
                    "Invested", bond_invested.get(i),
                    "Maturity Date", bonds_maturity_date.get(i),
                    "Date of Investment", bonds_date_of_investment.get(i)));
        }
        koshantra_data.put("bondsColumns", bondsColumns);
        koshantra_data.put("bondsData", bondsData);

        List<String> ppfsColumns = Arrays.asList("Invested", "Date of Investment", "Frequency");
        List<Map<String,Object>> ppfsData = new ArrayList<>();
        ArrayList<String> ppf_invested = (ArrayList<String>) payload.get("ppf_invested");
        ArrayList<String> ppf_date_of_investment = (ArrayList<String>) payload.get("ppf_date_of_investment");
        ArrayList<String> ppf_frequency = (ArrayList<String>) payload.get("ppf_frequency");
        for(int i=0;i<ppf_invested.size(); i++){
            ppfsData.add(Map.of("Invested", ppf_invested.get(i),
                    "Date of Investment", ppf_date_of_investment.get(i),
                    "Frequency", ppf_frequency.get(i)));
        }
        koshantra_data.put("ppfsColumns", ppfsColumns);
        koshantra_data.put("ppfsData", ppfsData);

        List<String> fdsColumns = Arrays.asList("Name", "Amount", "Start Date", "Maturity Date");
        List<Map<String,Object>> fdsData = new ArrayList<>();
        ArrayList<String> fd_start_date = (ArrayList<String>) payload.get("fd_start_date");
        ArrayList<String> fd_name = (ArrayList<String>) payload.get("fd_name");
        ArrayList<String> fd_maturity_date = (ArrayList<String>) payload.get("fd_maturity_date");
        ArrayList<String> fd_amount = (ArrayList<String>) payload.get("fd_amount");
        for(int i=0;i<fd_name.size(); i++){
            fdsData.add(Map.of("Name", fd_name.get(i),
                    "Amount", fd_amount.get(i),
                    "Start Date", fd_start_date.get(i),
                    "Maturity Date", fd_maturity_date.get(i)));
        }
        koshantra_data.put("fdsColumns", fdsColumns);
        koshantra_data.put("fdsData", fdsData);

        List<String> loanoremisColumns = Arrays.asList("Start Date", "Installment Amount", "Installment Left", "Type");
        List<Map<String,Object>> loanoremisData = new ArrayList<>();
        ArrayList<String> loan_emi_start_date = (ArrayList<String>) payload.get("loan_emi_start_date");
        ArrayList<String> loan_emi_installment_amount = (ArrayList<String>) payload.get("loan_emi_installment_amount");
        ArrayList<String> loan_emi_installement_left = (ArrayList<String>) payload.get("loan_emi_installement_left");
        ArrayList<String> loan_emi_type = (ArrayList<String>) payload.get("loan_emi_type");
        for(int i=0;i<loan_emi_installment_amount.size(); i++){
            loanoremisData.add(Map.of("Start Date", loan_emi_start_date.get(i),
                    "Installment Amount", loan_emi_installment_amount.get(i),
                    "Installment Left", loan_emi_installement_left.get(i),
                    "Type", loan_emi_type.get(i)));
        }
        koshantra_data.put("loanoremisColumns", loanoremisColumns);
        koshantra_data.put("loanoremisData", loanoremisData);


        //Page 1
//        data.put("logo", "src/main/resources/templates/images/koshantra_logo.PNG");
        data.put("logo", "src/main/resources/templates/images/koshantra_logo_with_text.png");
        data.put("client_name", payload.get("first_name")+" "+payload.get("last_name"));
        String fileName = payload.get("first_name")+" "+payload.get("last_name") + "_" + UUID.randomUUID().toString();
        data.put("ID",fileName);

        //Page3
        List<String> personalInfoColumns = Arrays.asList("Relationship", "Name", "DOB", "Occupation", "Mobile No.");
        List<Map<String,Object>> personalInfoData = new ArrayList<>();
//        personalInfoData.add(Map.of("Relationship", "Client"));
        personalInfoData.add(Map.of("Relationship", "Self", "Name", payload.get("first_name")+" "+payload.get("last_name"), "DOB", payload.get("dob"), "Occupation", payload.get("profession"), "Mobile No.", payload.get("mobile_no")));
//        personalInfoData.add(Map.of("Relationship", "Partner"));
        ArrayList<String> dependents_relationship = (ArrayList<String>) payload.get("dependents_relation");
        ArrayList<String> dependents_dob = (ArrayList<String>) payload.get("dependents_dob");
        ArrayList<String> dependents_name = (ArrayList<String>) payload.get("dependents_name");

        for(int i = 0; i<dependents_name.size(); i++){
            personalInfoData.add(Map.of("Relationship", dependents_relationship.get(i),
                    "Name", dependents_name.get(i),
                    "DOB", dependents_dob.get(i)));

            dependentsData.add(Map.of("Name", dependents_name.get(i),
                    "DOB", dependents_dob.get(i),
                    "Relation", dependents_relationship.get(i)));
        }

//        personalInfoData.add(Map.of("Relationship", "Spouse", "Name", "Ishween", "DOB", "01/06/1999", "Occupation", "Salaried", "Mobile No.", "9540305678"));
////        personalInfoData.add(Map.of("Relationship", "Dependents"));
//        personalInfoData.add(Map.of("Relationship", "Son", "Name", "Ishween", "DOB", "01/06/1999", "Occupation", "Salaried", "Mobile No.", "9540305678"));
//        personalInfoData.add(Map.of("Relationship", "Daughter", "Name", "Ishween", "DOB", "01/06/1999", "Occupation", "Salaried", "Mobile No.", "9540305678"));
        data.put("personalInfoColumns", personalInfoColumns);
        data.put("personalInfoData", personalInfoData);

        //Page4
        List<String> incomeColumns = Arrays.asList("Income", "Monthly ₹", "Annually(₹)");
        List<Map<String,Object>> incomeData = new ArrayList<>();
        String annual_income = (String) payload.get("income");
        String annual_expense = (String) payload.get("expense");
        if(payload.get("profession").equals("Self Employed")){
            incomeData.add(Map.of("Income", "Business Income", "Monthly ₹", Long.parseLong(annual_income)/12.0, "Annually(₹)", annual_income));
            incomeData.add(Map.of("Income", "Income Salary", "Monthly ₹", "0", "Annually(₹)", "0"));
        }else{
            incomeData.add(Map.of("Income", "Business Income", "Monthly ₹", "0", "Annually(₹)", "0"));
            incomeData.add(Map.of("Income", "Income Salary", "Monthly ₹", Long.parseLong(annual_income)/12.0, "Annually(₹)", annual_income));
        }
//        incomeData.add(Map.of("Income", "Business Income", "Monthly ₹", "0", "Annually(₹)", "0"));
//        incomeData.add(Map.of("Income", "Income Salary", "Monthly ₹", "100", "Annually(₹)", "1200"));
//        incomeData.add(Map.of("Income", "Rental Income", "Monthly ₹", "300", "Annually(₹)", "3600"));
        data.put("incomeColumns", incomeColumns);
        data.put("incomeData", incomeData);
        List<Map<String, Object>> incomeTotal = new ArrayList<>();
        incomeTotal.add(Map.of("Income", "Total", "Monthly ₹", Long.parseLong(annual_income)/12.0, "Annually(₹)", annual_income));
        data.put("incomeTotal", incomeTotal);

        //Page5
        try {
            List<String> xAxis = Arrays.asList("Income", "Expenses");
            List<String> values = Arrays.asList(annual_income, annual_expense);
            createBarChart(xAxis, values, "", "in (₹)", "","cash_flow_chart");
            data.put("cash_flow_chart", "src/main/resources/templates/images/cash_flow_chart.jpeg");
        }catch (Exception e){
            e.printStackTrace();
        }

        //Page 8
//        List<String> assumptionColumns = Arrays.asList("Description", "Percentage");
//        List<Map<String,Object>> assumptionData = new ArrayList<>();
//        assumptionData.add(Map.of("Description", "Liquid Mutual Fund", "Percentage", "6"));
//        assumptionData.add(Map.of("Description", "Debt Mutual Fund", "Percentage", "7"));
//        assumptionData.add(Map.of("Description", "Equity Mutual Fund", "Percentage", "12"));
//        data.put("assumptionColumns", assumptionColumns);
//        data.put("assumptionData", assumptionData);

        //Page9
        List<String> financialGoalsColumns = Arrays.asList("Goal Name", "Years to Goal", "Present Value", "Growth Rate(%)", "Future Cost(Rs.)");
        List<Map<String,Object>> financialGoalsData = new ArrayList<>();
        String life_goals = (String) payload.get("life_goals");
        DecimalFormat df = new DecimalFormat("0.00");
        if(life_goals != null) {
            String[] life_goals_list = life_goals.split(",");
//        ArrayList<String> life_goals = (ArrayList<String>) payload.get("life_goals");
            for (String life_goal : life_goals_list) {
                if (life_goal.equals("Child Education")) {
                    //child dependents
                    for (int i = 0; i < dependents_relationship.size(); i++) {
                        if (dependents_relationship.get(i).equals("Child")) {
                            LocalDate curDate = LocalDate.now();
                            int age = Period.between(LocalDate.parse(dependents_dob.get(i)), curDate).getYears();
                            if (age < 18) {
                                double rate = 0.08;
                                int present_value = 700000;
                                double futureCost = (present_value) * Math.pow(1 + rate, age);

                                financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Graduation",
                                        "Years to Goal", (18 - age),
                                        "Present Value", present_value,
                                        "Growth Rate(%)", (rate*100),
                                        "Future Cost(Rs.)", df.format(futureCost)));
                            }
                            if (age < 21) {
                                double rate = 0.08;
                                int present_value = 2000000;
                                double futureCost = (present_value) * Math.pow(1 + rate, age);
                                financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Higher Studies",
                                        "Years to Goal", (21 - age),
                                        "Present Value", present_value,
                                        "Growth Rate(%)", (rate*100),
                                        "Future Cost(Rs.)", df.format(futureCost)));
                            }
                        }
                    }
                }
                if (life_goal.equals("Self Marriage")) {
                    LocalDate curDate = LocalDate.now();
                    int age = Period.between(LocalDate.parse((String) payload.get("dob")), curDate).getYears(), present_value = 2000000;
                    double rate = 0.06;
                    double futureCost = (present_value) * Math.pow(1 + rate, age);
                    financialGoalsData.add(Map.of("Goal Name", payload.get("first_name") + " " + payload.get("last_name") + "-Marriage",
                            "Years to Goal", (25 - age),
                            "Present Value", present_value,
                            "Growth Rate(%)", (rate*100),
                            "Future Cost(Rs.)", df.format(futureCost)));
                }
                if (life_goal.equals("Retirement")) {
                    LocalDate curDate = LocalDate.now();
                    int age = Period.between(LocalDate.parse((String) payload.get("dob")), curDate).getYears();
                    double rate = 0.06;
                    double futureCost = (Long.parseLong(annual_expense)) * Math.pow(1 + rate, age);
                    financialGoalsData.add(Map.of("Goal Name", payload.get("first_name") + " " + payload.get("last_name") + "-Retirement",
                            "Years to Goal", (58 - age),
                            "Present Value", annual_expense,
                            "Growth Rate(%)", (rate*100),
                            "Future Cost(Rs.)", df.format(futureCost)));
                }
                if (life_goal.equals("Child Marriage")) {
                    for (int i = 0; i < dependents_relationship.size(); i++) {
                        if (dependents_relationship.get(i).equals("Child")) {
                            LocalDate curDate = LocalDate.now();
                            int age = Period.between(LocalDate.parse(dependents_dob.get(i)), curDate).getYears();
                            double rate = 0.06;
                            int present_value = 2000000;
                            double futureCost = (present_value) * Math.pow(1 + rate, age);
                            financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Marriage",
                                    "Years to Goal", (25 - age),
                                    "Present Value", present_value,
                                    "Growth Rate(%)", (rate*100),
                                    "Future Cost(Rs.)", df.format(futureCost)));
                        }
                    }
                }
            }
        }else{
            System.out.println("LIFE GOALS IS COMING AS NULL");
        }
//        financialGoalsData.add(Map.of("Goal Name", "X-Graduation", "Years to Goal", "12", "Present Cost of Goal", "300000", "Growth Rate(%)", "8", "Future Cost", "583000"));
        data.put("financialGoalsColumns", financialGoalsColumns);
        data.put("financialGoalsData", financialGoalsData);

        ArrayList<String> stocks_amount = (ArrayList<String>) payload.get("stocks_amount");
        ArrayList<String> mfs_amount = (ArrayList<String>) payload.get("mf_amount");
        ArrayList<String> bonds_invested = (ArrayList<String>) payload.get("bonds_invested");
        ArrayList<String> ppfs_invested = (ArrayList<String>) payload.get("ppf_invested");
        ArrayList<String> fds_amount = (ArrayList<String>) payload.get("fd_amount");

//        List<Long> longList = Lists.transform(mfs_amount, new Function<String, Long>() {
//            public Long apply(String s) {
//                return Long.valueOf(s);
//            }
//        });

        Long total_stocks_amount = stocks_amount == null || stocks_amount.isEmpty()? 0L : stocks_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long total_mfs_amount = mfs_amount == null || mfs_amount.isEmpty()? 0L : mfs_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long total_bonds_amount = bonds_invested == null || bonds_invested.isEmpty()? 0L : bonds_invested.stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long total_ppfs_amount = ppfs_invested == null || ppfs_invested.isEmpty()? 0L : ppfs_invested.stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long total_fds_amount = fds_amount == null || fds_amount.isEmpty()? 0L : fds_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);

        Long existing_equity_amount = total_stocks_amount + total_mfs_amount;
        Long existing_debt_amount = total_bonds_amount + total_ppfs_amount + total_fds_amount;

        Long total = existing_equity_amount + existing_debt_amount;

        int equity_percentage = 0, debt_percentage = 0;
        try {
            equity_percentage = total.equals(0L) ? 0 : (int) ((existing_equity_amount / (total*1.0)) * 100);
            debt_percentage = total.equals(0L) ? 0 : (int) ((existing_debt_amount / (total*1.0)) * 100);
        }catch (Exception e){
            e.printStackTrace();
        }

        int age = Period.between(LocalDate.parse((String) payload.get("dob")), LocalDate.now()).getYears();
        int ideal_equity_percentage = (100-age);
        int ideal_debt_percentage = age;

        //Page10
        try {
            List<String> items = Arrays.asList("Equity ("+equity_percentage+"%)", "Debt ("+debt_percentage+"%)");
            List<Integer> itemValues = Arrays.asList(equity_percentage, debt_percentage);
            createPieChart(items, itemValues, "Current Asset Allocation", "pie_chart_1");
            data.put("pie_chart_1", "src/main/resources/templates/images/pie_chart_1.jpeg");

            items = Arrays.asList("Equity ("+ideal_equity_percentage+"%)", "Debt ("+ideal_debt_percentage+"%)");
            itemValues = Arrays.asList(ideal_equity_percentage, ideal_debt_percentage);
            createPieChart(items, itemValues, "Recommended Asset Allocation", "pie_chart_2");
            data.put("pie_chart_2", "src/main/resources/templates/images/pie_chart_2.jpeg");
        }catch (Exception e){
            e.printStackTrace();
        }

        //Page11
        Long emergency_fund = Long.parseLong(annual_income)/4; //3 MONTH SALARY
        Long ideal_equity_amount = (ideal_equity_percentage * Long.parseLong(annual_income)/12)/ 100;
        Long ideal_debt_amount = (ideal_debt_percentage * Long.parseLong(annual_income)/12)/100;
        Long lis_amount = ((ArrayList<String>) payload.get("li_sum_insured")).stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long his_amount = ((ArrayList<String>) payload.get("hi_sum_insured")).stream().map(Long::parseLong).reduce(0L, Long::sum);

        Long total_li_required = 20 * (Long.parseLong(annual_income));
        Long additional_li_required = (total_li_required) - lis_amount;
        Long ideal_hi_amount = 0L;
        if(age>=21 && age<30)ideal_hi_amount = 5_00_000L;
        else if(age>=30 && age <40) ideal_hi_amount = 10_00_000L;
        else if (age>=40 && age<50) ideal_hi_amount = 15_00_000L;
        else if(age>=50) ideal_hi_amount = 20_00_000L;
        Long additional_hi_required = ideal_hi_amount - his_amount;

        data.put("emergency_fund", emergency_fund);
        data.put("ideal_equity_amount", ideal_equity_amount);
        data.put("ideal_debt_amount", ideal_debt_amount);
        data.put("additional_li_required", additional_li_required);
        data.put("additional_hi_required", additional_hi_required);
        if(existing_equity_amount > ideal_equity_amount){
            data.put("switch_out_in", "Switch Out");
            data.put("switch_out_in_equity_amount", existing_equity_amount-ideal_equity_amount);
            data.put("switch_out_in_debt_amount", existing_debt_amount-ideal_debt_amount);
        }else{
            data.put("switch_out_in", "Switch In");
            data.put("switch_out_in_equity_amount", ideal_equity_amount-existing_equity_amount);
            data.put("switch_out_in_debt_amount", ideal_debt_amount-existing_debt_amount);
        }
        List<String> InsuranceColumns = Arrays.asList("Person To Be Insured", "Addl. Coverage Required");
        List<Map<String,Object>> LifeInsuranceData = new ArrayList<>();
        List<Map<String,Object>> HealthInsuranceData = new ArrayList<>();

//        List<String> LifeInsuranceData = Arrays.asList("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
//                "Addl. Coverage Required", additional_li_required+"");
//        List<String> HealthInsuranceData = Arrays.asList("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
//                "Addl. Coverage Required", additional_hi_required+"");
        LifeInsuranceData.add(Map.of("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
                "Addl. Coverage Required", additional_li_required+""));
        HealthInsuranceData.add(Map.of("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
                "Addl. Coverage Required", additional_hi_required+""));

        data.put("InsuranceColumns", InsuranceColumns);
        data.put("LifeInsuranceData", LifeInsuranceData);
        data.put("HealthInsuranceData", HealthInsuranceData);

        //Page12
        try {
            List<String> xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
            List<String> values = Arrays.asList(total_li_required+"", lis_amount+"", additional_li_required+"");
            createBarChart(xAxis, values, "Life Insurance Client", "in (₹)", "","life_insurance_chart");
            data.put("life_insurance_chart", "src/main/resources/templates/images/life_insurance_chart.jpeg");
            xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
            values = Arrays.asList(ideal_hi_amount+"", his_amount+"", additional_hi_required+"");
            createBarChart(xAxis, values, "Health Insurance Client", "in (₹)", "","health_insurance_chart");
            data.put("health_insurance_chart", "src/main/resources/templates/images/health_insurance_chart.jpeg");
            //TODO: add chart name as health-ID(from DB)
        }catch (Exception e){
            e.printStackTrace();
        }

        //YOUR NETWORTH
        Long home_loan = 0L, personal_loan = 0L;
        ArrayList<String> loans_type = (ArrayList<String>) payload.get("loan_emi_type");
        ArrayList<String> loans_amount = (ArrayList<String>) payload.get("loan_emi_installment_amount");
        for(int i=0; i<loans_type.size(); i++){
            String loan_type = loans_type.get(i);
            if(loan_type.equals("Home Loan")){
                home_loan += Long.parseLong(loans_amount.get(i));
            }else if(loan_type.equals("Personal Loan")){
                personal_loan += Long.parseLong(loans_amount.get(i));
            }
        }

//        List<String> netWorthColumns = Arrays.asList("Assets", "Liabilities");
//        List<String> assetsColumnsRepeat = Arrays.asList("Assets");
//        List<String> liabilitiesColumnsRepeat = Arrays.asList("Liabilities");
//
//        List<Map<String,Object>> assetsColumns = new ArrayList<>();
//        List<Map<String,Object>> liabilitiesColumns = new ArrayList<>();
//        assetsColumns.add(Map.of("Assets", "Financial Assets"));
//        assetsColumns.add(Map.of("Assets", "Amount"));
//        assetsColumns.add(Map.of("Assets", "Percentage"));
//
//        liabilitiesColumns.add(Map.of("Liabilities", "Liability Type"));
//        liabilitiesColumns.add(Map.of("Liabilities", "Amount"));


        List<String> assetsColumns = Arrays.asList("Financial Assets", "Amount", "Percentage");
        List<String> liabilitiesColumns = Arrays.asList("Liability Type", "Amount");


//        assetsColumns.add(Map.of("Financial Assets", "Equity", "Amount", existing_equity_amount+"", "Percentage", equity_percentage+""));
//        liabilitiesColumns.add(Map.of("Financial Assets", "Equity", "Amount", existing_equity_amount+"", "Percentage", equity_percentage+""));

//        List<String> assetsColumns = Arrays.asList("Financial Assets", "Amount", "Percentage");
//        List<String> liabilitiesColumns = Arrays.asList("Liability Type", "Amount");

//        List<String> assetsData = Arrays.asList("Financial Assets", "Equity", "Amount", existing_equity_amount+"", "Percentage", equity_percentage+"");
        List<Map<String,Object>> assetsData = new ArrayList<>();
        assetsData.add(Map.of("Financial Assets", "Equity", "Amount", existing_equity_amount+"", "Percentage", equity_percentage+"%"));
        assetsData.add(Map.of("Financial Assets", "Debt/Fixed Income Asset", "Amount", existing_debt_amount+"", "Percentage", debt_percentage+"%"));
        assetsData.add(Map.of("Financial Assets", "Total", "Amount", total+"", "Percentage", "100%"));

        List<Map<String,Object>> liabilitiesData = new ArrayList<>();
        liabilitiesData.add(Map.of("Liability Type", "Home Loan", "Amount", home_loan+"" ));
        liabilitiesData.add(Map.of("Liability Type", "Personal Loan", "Amount", personal_loan+""));
        liabilitiesData.add(Map.of("Liability Type", "Total", "Amount", home_loan+personal_loan+""));


//        data.put("netWorthColumns", netWorthColumns);
//        data.put("assetsColumnsRepeat", assetsColumnsRepeat);
//        data.put("liabilitiesColumnsRepeat", liabilitiesColumnsRepeat);
        data.put("assetsColumns", assetsColumns);
        data.put("liabilitiesColumns", liabilitiesColumns);
        data.put("assetsData", assetsData);
        data.put("liabilitiesData", liabilitiesData);

        //Page13

        //Page14

        try {
//            PdfGeneratorUtil pdfGeneratorUtil1 = new PdfGeneratorUtil();
            File outputFile = pdfGeneratorUtil.createPdf(fileName + "_customer_report",data, "Page1_Cover","Page2","Page3_PI","Page4_CashFlow", "Page5_CashFlowGraph",
                    /*"Page7_Investment"*//*, "Page8_Assumptions",*/"Page9_FinancialGoals","Page10_AssetAllocationChart","your_networth", "Page11_ActionPlan",
                    "Page12_ActionPlanChart", "Page14_Disclaimer");

            File file = pdfGeneratorUtil.createPdf(payload.get("first_name")+ " " + payload.get("last_name") + "_client_all_data", koshantra_data,
                    "clients_all_data");

//            File file = new File("payload_"+"id"+".txt");
//            BufferedWriter bf = new BufferedWriter(new FileWriter(file));
//            for (Map.Entry<String, Object> entry :
//                    payload.entrySet()) {
//
//                // put key and value separated by a colon
//                bf.write(entry.getKey() + ":"
//                        + entry.getValue().toString());
//
//                // new line
//                bf.newLine();
//            }
//
//            bf.flush();

//            FileWriter payload_file = new FileWriter("payload_"+"id"+".txt");
//            payload_file.write(payload.toString());
//            payload_file.close();
            EmailSendingUtil.sendEmail(outputFile, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createBarChart(List<String> xAxis, List<String> values, String title, String xLabel,
                               String yLabel, String fileName) throws IOException {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
        for(int i=0;i<values.size();i++){
            dataset.addValue(Double.parseDouble(values.get(i)), xAxis.get(i), "");
        }
        if(values.isEmpty()){
            dataset.addValue(0, "", "");
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                title,
                yLabel, xLabel,
                dataset,PlotOrientation.VERTICAL,
                true, false, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(66, 126, 245));
        plot.getRenderer().setSeriesPaint(1, new Color(212, 76, 112));
        plot.getRenderer().setSeriesPaint(2, new Color(96, 232, 235));

        int width = 640;    /* Width of the image */
        int height = 480;   /* Height of the image */
        File BarChart = new File( "src/main/resources/templates/images/"+fileName+".jpeg" );
        ChartUtils.saveChartAsJPEG(BarChart , barChart , width , height);
    }

    public void createPieChart(List<String> items, List<Integer> itemValues, String title, String fileName) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset( );

        for(int i=0;i<itemValues.size();i++){
            dataset.setValue(items.get(i), itemValues.get(i));
        }

        if(itemValues.isEmpty()){
            dataset.setValue("", 0);
        }

        // Create the chart object
        PiePlot plot = new PiePlot(dataset);
        plot.setSectionPaint(0, new Color(66, 126, 245));
        plot.setSectionPaint(1, new Color(212, 76, 112));

        // Define labels management for pie chart
        StandardPieSectionLabelGenerator spilg = new StandardPieSectionLabelGenerator("{2}" );

        plot.setLabelGenerator(spilg);

        JFreeChart chart = ChartFactory.createPieChart(
                title,   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);

        int width = 600;   /* Width of the image */
        int height = 400;  /* Height of the image */
        File pieChart = new File( "src/main/resources/templates/images/"+fileName+".jpeg" );
        ChartUtils.saveChartAsJPEG( pieChart , chart , width , height );

    }

}
