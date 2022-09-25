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
import org.jfree.chart.renderer.category.BarRenderer;
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
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PdfService {

    @Autowired private PdfGeneratorUtil pdfGeneratorUtil;

    public void generatePdf(Map<String, Object> payload){

//        ArrayList<String> snames = (ArrayList<String>) payload.get("stocks_name");

        System.out.println("service payload: " + payload);

//        List<String> headers = Arrays.asList("ID", "Name", "Salary", "Status");
//        List<Map<String, Object>> rows = new ArrayList<>();
//        rows.add(Map.of("ID", "1", "Name", "Jim", "Salary", "50000", "Status", "active"));
//        rows.add(Map.of("ID", "2", "Name", "Sally", "Salary", "50000", "Status", "inactive"));
//
        Map<String,Object> data = new HashMap<>();
//        data.put("headers", headers);
//        data.put("rows", rows);

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
        List<String> stocksColumnsAllData = Arrays.asList("Name", "Amount", "Original Amount", "Market Value", "Date of Purchase", "Frequency");
        List<Map<String,Object>> stocksData = new ArrayList<>();
//        ArrayList<String> stocks_name = (ArrayList<String>) payload.get("stocks_name");
        ArrayList<String> stock_amount = (ArrayList<String>) payload.get("stocks_amount");
        ArrayList<String> stocks_market_value = (ArrayList<String>) payload.get("stocks_market_value");
        ArrayList<String> stocks_date_of_purchase = (ArrayList<String>) payload.get("stocks_date_of_purchase");
        ArrayList<String> stocks_frequency = (ArrayList<String>) payload.get("stocks_frequency");
        Long stock_full_amount = 0L, mfs_full_amount = 0L, ppfs_full_amount = 0L;
        for(int i=0;i<stock_amount.size(); i++){
            long amount = getAmountFrom(stocks_frequency.get(i), stock_amount.get(i), stocks_date_of_purchase.get(i));
            stock_full_amount += amount;
            stocksData.add(Map.of(
                    "Amount", amount,
                    "Original Amount", stock_amount.get(i),
                    "Market Value", stocks_market_value.get(i),
                    "Date of Purchase", stocks_date_of_purchase.get(i),
                    "Frequency", stocks_frequency.get(i)));
        }
        koshantra_data.put("stocksColumns", stocksColumns);
        koshantra_data.put("stocksData", stocksData);
        koshantra_data.put("stocksColumnsAllData", stocksColumnsAllData);

        List<String> mfsColumns = Arrays.asList("Market Value", "Amount", "Type", "Date of Purchase", "Frequency");
        List<String> mfsColumnsAllData = Arrays.asList("Market Value", "Amount", "Original Amount", "Type", "Date of Purchase", "Frequency");
        List<Map<String,Object>> mfsData = new ArrayList<>();
        ArrayList<String> mfs_mkt_value = (ArrayList<String>) payload.get("mf_mkt_value");
        ArrayList<String> mf_amount = (ArrayList<String>) payload.get("mf_amount");
        ArrayList<String> mfs_type = (ArrayList<String>) payload.get("mf_type");
        ArrayList<String> mfs_date_of_purchase = (ArrayList<String>) payload.get("mf_date_of_purchase");
        ArrayList<String> mfs_frequency = (ArrayList<String>) payload.get("mf_frequency");
        for(int i=0;i<mfs_frequency.size(); i++){
            long amount = getAmountFrom(mfs_frequency.get(i), mf_amount.get(i), mfs_date_of_purchase.get(i));
            mfs_full_amount += amount;
            mfsData.add(Map.of("Type", mfs_type.get(i),
                    "Amount", amount,
                    "Original Amount", mf_amount.get(i),
                    "Market Value", mfs_mkt_value.get(i),
                    "Date of Purchase", mfs_date_of_purchase.get(i),
                    "Frequency", mfs_frequency.get(i)));
        }
        koshantra_data.put("mfsColumns", mfsColumns);
        koshantra_data.put("mfsData", mfsData);
        koshantra_data.put("mfsColumnsAllData", mfsColumnsAllData);

        List<String> lisColumns = Arrays.asList("Start Date", "Premium Paying Term", "Type", "Sum Assured", "Name", "Policy Term");
        List<Map<String,Object>> lisData = new ArrayList<>();
        ArrayList<String> lis_start_date = (ArrayList<String>) payload.get("li_start_date");
        ArrayList<String> li_premium_paying_term = (ArrayList<String>) payload.get("li_premium_paying_term");
        ArrayList<String> li_type = (ArrayList<String>) payload.get("li_type");
        ArrayList<String> li_sum_assured = (ArrayList<String>) payload.get("li_sum_assured");
        ArrayList<String> li_name = (ArrayList<String>) payload.get("li_name");
        ArrayList<String> li_policy_term = (ArrayList<String>) payload.get("li_policy_term");
        for(int i=0;i<li_name.size(); i++){
            lisData.add(Map.of("Name", li_name.get(i),
                    "Start Date", lis_start_date.get(i),
                    "Premium Paying Term", li_premium_paying_term.get(i),
                    "Sum Assured", li_sum_assured.get(i),
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
        List<String> ppfsColumnsAllData = Arrays.asList("Invested", "Original Amount", "Date of Investment", "Frequency");
        List<Map<String,Object>> ppfsData = new ArrayList<>();
        ArrayList<String> ppf_invested = (ArrayList<String>) payload.get("ppf_invested");
        ArrayList<String> ppf_date_of_investment = (ArrayList<String>) payload.get("ppf_date_of_investment");
        ArrayList<String> ppf_frequency = (ArrayList<String>) payload.get("ppf_frequency");
        for(int i=0;i<ppf_invested.size(); i++){
            long amount = getAmountFrom(ppf_frequency.get(i), ppf_invested.get(i), ppf_date_of_investment.get(i));
            ppfs_full_amount += amount;
            ppfsData.add(Map.of("Invested", amount,
                    "Original Amount", ppf_invested.get(i),
                    "Date of Investment", ppf_date_of_investment.get(i),
                    "Frequency", ppf_frequency.get(i)));
        }
        koshantra_data.put("ppfsColumns", ppfsColumns);
        koshantra_data.put("ppfsData", ppfsData);
        koshantra_data.put("ppfsColumnsAllData", ppfsColumnsAllData);

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
        data.put("logo", "src/main/resources/templates/images/Wealth_Creation_Logo.png");
        data.put("client_name", payload.get("first_name")+" "+payload.get("last_name"));
//        String fileName = payload.get("first_name")+" "+payload.get("last_name") + "_" + UUID.randomUUID().toString();
        String fileName = payload.get("first_name")+"_"+payload.get("last_name");
        String cash_flow_chart = "src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg";
        String pie_chart_1 = "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg";
        String pie_chart_2 = "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg";
        String life_insurance_chart = "src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg";
        String health_insurance_chart = "src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg";
        File cashFlowChartFile, pieChart1File, pieChart2File, lifeInsuranceFile, healthInsuranceFile;
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

        DecimalFormat df = new DecimalFormat("0.00"); /*df.format(futureCost)*/

        //Page4
        List<String> incomeColumns = Arrays.asList("Income", "Monthly(Rs.)", "Annually(Rs.)");
        List<Map<String,Object>> incomeData = new ArrayList<>();
        String annual_income = (String) payload.get("income");
        String annual_expense = (String) payload.get("expense");
        if(payload.get("profession").equals("Self Employed")){
            incomeData.add(Map.of("Income", "Business Income", "Monthly(Rs.)", df.format(Long.parseLong(annual_income)/12.0), "Annually(Rs.)", annual_income));
            incomeData.add(Map.of("Income", "Income Salary", "Monthly(Rs.)", "0", "Annually(Rs.)", "0"));
        }else{
            incomeData.add(Map.of("Income", "Business Income", "Monthly(Rs.)", "0", "Annually(Rs.)", "0"));
            incomeData.add(Map.of("Income", "Income Salary", "Monthly(Rs.)", df.format(Long.parseLong(annual_income)/12.0), "Annually(Rs.)", annual_income));
        }
//        incomeData.add(Map.of("Income", "Business Income", "Monthly ₹", "0", "Annually(₹)", "0"));
//        incomeData.add(Map.of("Income", "Income Salary", "Monthly ₹", "100", "Annually(₹)", "1200"));
//        incomeData.add(Map.of("Income", "Rental Income", "Monthly ₹", "300", "Annually(₹)", "3600"));
        data.put("incomeColumns", incomeColumns);
        data.put("incomeData", incomeData);
        List<Map<String, Object>> incomeTotal = new ArrayList<>();
        incomeTotal.add(Map.of("Income", "Total", "Monthly(Rs.)", df.format(Long.parseLong(annual_income)/12.0), "Annually(Rs.)", annual_income));
        data.put("incomeTotal", incomeTotal);

        //Page5
        try {
            List<String> xAxis = Arrays.asList("Income", "Expenses");
            List<String> values = Arrays.asList(annual_income, annual_expense);
            cashFlowChartFile = createBarChart(xAxis, values, "", "INR (₹)", "","src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg");
            data.put("cash_flow_chart", "src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg");
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
//        DecimalFormat df = new DecimalFormat("0.00"); /*df.format(futureCost)*/
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
                                double futureCost = (present_value) * Math.pow(1 + rate, (18-age));

                                financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Graduation",
                                        "Years to Goal", (18 - age),
                                        "Present Value", present_value,
                                        "Growth Rate(%)", (rate*100),
                                        "Future Cost(Rs.)", Math.round(futureCost)));
                            }
                            if (age < 21) {
                                double rate = 0.08;
                                int present_value = 2000000;
                                double futureCost = (present_value) * Math.pow(1 + rate, (21 - age));
                                financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Higher Studies",
                                        "Years to Goal", (21 - age),
                                        "Present Value", present_value,
                                        "Growth Rate(%)", (rate*100),
                                        "Future Cost(Rs.)", Math.round(futureCost)));
                            }
                        }
                    }
                }
                if (life_goal.equals("Self Marriage")) {
                    LocalDate curDate = LocalDate.now();
                    int age = Period.between(LocalDate.parse((String) payload.get("dob")), curDate).getYears(), present_value = 2000000;
                    double rate = 0.06;
                    double futureCost = (present_value) * Math.pow(1 + rate, (25 - age));
                    financialGoalsData.add(Map.of("Goal Name", payload.get("first_name") + " " + payload.get("last_name") + "-Marriage",
                            "Years to Goal", (25 - age),
                            "Present Value", present_value,
                            "Growth Rate(%)", (rate*100),
                            "Future Cost(Rs.)", Math.round(futureCost)));
                }
                if (life_goal.equals("Retirement")) {
                    LocalDate curDate = LocalDate.now();
                    int age = Period.between(LocalDate.parse((String) payload.get("dob")), curDate).getYears();
                    double rate = 0.06;
                    double futureCost = (Long.parseLong(annual_expense)) * Math.pow(1 + rate, (58 - age));
                    financialGoalsData.add(Map.of("Goal Name", payload.get("first_name") + " " + payload.get("last_name") + "-Retirement",
                            "Years to Goal", (58 - age),
                            "Present Value", annual_expense,
                            "Growth Rate(%)", (rate*100),
                            "Future Cost(Rs.)", Math.round(futureCost)));
                }
                if (life_goal.equals("Child Marriage")) {
                    for (int i = 0; i < dependents_relationship.size(); i++) {
                        if (dependents_relationship.get(i).equals("Child")) {
                            LocalDate curDate = LocalDate.now();
                            int age = Period.between(LocalDate.parse(dependents_dob.get(i)), curDate).getYears();
                            double rate = 0.06;
                            int present_value = 2000000;
                            double futureCost = (present_value) * Math.pow(1 + rate, (25 - age));
                            financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Marriage",
                                    "Years to Goal", (25 - age),
                                    "Present Value", present_value,
                                    "Growth Rate(%)", (rate*100),
                                    "Future Cost(Rs.)", Math.round(futureCost)));
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

//        Long total_stocks_amount = stocks_amount == null || stocks_amount.isEmpty()? 0L : stocks_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);
//        Long total_mfs_amount = mfs_amount == null || mfs_amount.isEmpty()? 0L : mfs_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long total_bonds_amount = bonds_invested == null || bonds_invested.isEmpty()? 0L : bonds_invested.stream().map(Long::parseLong).reduce(0L, Long::sum);
//        Long total_ppfs_amount = ppfs_invested == null || ppfs_invested.isEmpty()? 0L : ppfs_invested.stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long total_fds_amount = fds_amount == null || fds_amount.isEmpty()? 0L : fds_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);

        Long existing_equity_amount = stock_full_amount + mfs_full_amount;
        Long existing_debt_amount = total_bonds_amount + ppfs_full_amount + total_fds_amount;

        Long total = existing_equity_amount + existing_debt_amount;

        float equity_percentage = 0F, debt_percentage = 0F;
        try {
            equity_percentage = total.equals(0L) ? 0F : Float.valueOf(df.format((existing_equity_amount / (total*1.00)) * (100.00)));//add point upto 2 decimal
//            debt_percentage = total.equals(0L) ? 0 : (int) ((existing_debt_amount / (total*1.0)) * 100);
            debt_percentage = 100 - equity_percentage;
        }catch (Exception e){
            e.printStackTrace();
        }

        int age = Period.between(LocalDate.parse((String) payload.get("dob")), LocalDate.now()).getYears();
        float ideal_equity_percentage = Float.valueOf(df.format(100-age));
        float ideal_debt_percentage = age;

        //Page10
        try {
            List<String> items = Arrays.asList("Equity ("+equity_percentage+"%)", "Debt ("+debt_percentage+"%)");
            List<Float> itemValues = Arrays.asList(equity_percentage, debt_percentage);
            pieChart1File = createPieChart(items, itemValues, "Current Asset Allocation", "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg");
            data.put("pie_chart_1", "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg");

            items = Arrays.asList("Equity ("+ideal_equity_percentage+"%)", "Debt ("+ideal_debt_percentage+"%)");
            itemValues = Arrays.asList(ideal_equity_percentage, ideal_debt_percentage);
            pieChart2File = createPieChart(items, itemValues, "Recommended Asset Allocation", "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg");
            data.put("pie_chart_2", "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg");
        }catch (Exception e){
            e.printStackTrace();
        }

        //Page11
//        Long emergency_fund = Long.parseLong(annual_income)/4; //3 MONTH SALARY
        Long emergency_fund = Long.parseLong(annual_expense)/2; //Formual 6 * monthly expense
        long ideal_equity_amount = ((long)ideal_equity_percentage * (Long.parseLong(annual_income)/12))* 100;
        long ideal_debt_amount = ((long)ideal_debt_percentage * (Long.parseLong(annual_income)/12))*100;
        Long lis_amount = ((ArrayList<String>) payload.get("li_sum_assured")).stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long his_amount = ((ArrayList<String>) payload.get("hi_sum_insured")).stream().map(Long::parseLong).reduce(0L, Long::sum);

        Long total_li_required = 20 * (Long.parseLong(annual_income));
        Long additional_li_required = (total_li_required) - lis_amount;
        Long ideal_hi_amount = 0L;
        if(age>=21 && age<30 && (!payload.get("number_of_dependents").toString().isEmpty() && Integer.parseInt((String) payload.get("number_of_dependents")) > 0)) ideal_hi_amount = 7_50_000L;
        else if(age>=21 && age<30 ) ideal_hi_amount = 5_00_000L;
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
            long switch_out_equity = (long) (((equity_percentage-ideal_equity_percentage) / (total==0?1:total)) * 100);
            long switch_in_debt = total - switch_out_equity;
            data.put("switch_out_in_equity", "Switch Out");
            data.put("switch_out_in_debt", "Switch In");
            data.put("switch_out_in_equity_amount", switch_out_equity);
            data.put("switch_out_in_debt_amount", switch_in_debt);
        }else{
            long switch_in_equity = (long) (((ideal_equity_percentage-equity_percentage) / (total==0?1:total)) * 100);
            long switch_out_debt = switch_in_equity - total;
            data.put("switch_out_in_equity", "Switch In");
            data.put("switch_out_in_debt", "Switch Out");
            data.put("switch_out_in_equity_amount", switch_in_equity);
            data.put("switch_out_in_debt_amount", switch_out_debt);
        }
        List<String> LifeInsuranceColumns = Arrays.asList("Person To Be Assured", "Addl. Coverage Required");
        List<String> HealthInsuranceColumns = Arrays.asList("Person To Be Insured", "Addl. Coverage Required");

        List<Map<String,Object>> LifeInsuranceData = new ArrayList<>();
        List<Map<String,Object>> HealthInsuranceData = new ArrayList<>();

//        List<String> LifeInsuranceData = Arrays.asList("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
//                "Addl. Coverage Required", additional_li_required+"");
//        List<String> HealthInsuranceData = Arrays.asList("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
//                "Addl. Coverage Required", additional_hi_required+"");
        LifeInsuranceData.add(Map.of("Person To Be Assured", payload.get("first_name")+" "+payload.get("last_name"),
                "Addl. Coverage Required", additional_li_required+""));
        HealthInsuranceData.add(Map.of("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
                "Addl. Coverage Required", additional_hi_required+""));

        data.put("LifeInsuranceColumns", LifeInsuranceColumns);
        data.put("HealthInsuranceColumns", HealthInsuranceColumns);
        data.put("LifeInsuranceData", LifeInsuranceData);
        data.put("HealthInsuranceData", HealthInsuranceData);

        //Page12
        try {
            List<String> xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
            List<String> values = Arrays.asList(total_li_required+"", lis_amount+"", additional_li_required+"");
            lifeInsuranceFile = createBarChart(xAxis, values, "Life Insurance Client", "INR (₹)", "","src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg");
            data.put("life_insurance_chart", "src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg");
            xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
            values = Arrays.asList(ideal_hi_amount+"", his_amount+"", additional_hi_required+"");
            healthInsuranceFile = createBarChart(xAxis, values, "Health Insurance Client", "INR (₹)", "","src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg");
            data.put("health_insurance_chart", "src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg");
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

        data.put("first_name", payload.get("first_name"));
        data.put("last_name", payload.get("last_name"));
        //Page13

        //Page14

        try {
//            PdfGeneratorUtil pdfGeneratorUtil1 = new PdfGeneratorUtil();
            File outputFile = pdfGeneratorUtil.createPdf(payload.get("first_name")+ "_" + payload.get("last_name") + "_report",data, "Page1_Cover","Page2","Page2.1_Index.html","Page3_PI","Page4_CashFlow", "Page5_CashFlowGraph",
                    /*"Page7_Investment"*//*, "Page8_Assumptions",*/"Page9_FinancialGoals","Page10_AssetAllocationChart","your_networth", "Page11_ActionPlan",
                    "Page12_ActionPlanChart", "Page14_Disclaimer");

            File file = pdfGeneratorUtil.createPdf(payload.get("first_name")+ "_" + payload.get("last_name") + "_customer_all_data", koshantra_data,
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
            EmailSendingUtil emailSendingUtil = new EmailSendingUtil();
            emailSendingUtil.sendEmail(outputFile, file, payload.get("first_name") + "_" + payload.get("last_name"), cash_flow_chart, pie_chart_1, pie_chart_2, life_insurance_chart, health_insurance_chart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File createBarChart(List<String> xAxis, List<String> values, String title, String xLabel,
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

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(.10);

        int width = 640;    /* Width of the image */
        int height = 480;   /* Height of the image */
//        File BarChart = new File( "src/main/resources/templates/images/"+fileName+".jpeg" );
        File BarChart = new File( fileName);
        ChartUtils.saveChartAsJPEG(BarChart , barChart , width , height);
        return BarChart;
    }

    public File createPieChart(List<String> items, List<Float> itemValues, String title, String fileName) throws IOException {
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

        int width = 500;   /* Width of the image */
        int height = 300;  /* Height of the image */
//        File pieChart = new File( "src/main/resources/templates/images/"+fileName+".jpeg" );
        File pieChart = new File( fileName );
        ChartUtils.saveChartAsJPEG( pieChart , chart , width , height );
        return pieChart;
    }

    private long getAmountFrom(String freq, String comp_amount, String date_of_purchase){
        long amount = Long.parseLong(comp_amount);
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        LocalDate now = LocalDate.now(zoneId);
        now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate started = LocalDate.parse(date_of_purchase);

        if(freq.equals("Daily")){
            amount = Duration.between(started.atStartOfDay(), now.atStartOfDay()).toDays() * amount;
        }else if(freq.equals("Monthly")){
            long monthsBetween = ChronoUnit.MONTHS.between(
                    started.withDayOfMonth(1),
                    now.withDayOfMonth(1)) + 1;
            amount = monthsBetween * amount;
            System.out.println(monthsBetween);

//            int diffYear = now.getYear() - started.getYear();
//            amount = (diffYear * 12L) + (now.getMonthValue()-started.getMonthValue() + 1) * amount;
        }
        return amount;
    }

}
