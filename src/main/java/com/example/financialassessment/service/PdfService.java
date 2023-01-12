package com.example.financialassessment.service;

import com.example.financialassessment.util.EmailSendingUtil;
import com.example.financialassessment.util.PdfGeneratorUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

import static com.example.financialassessment.service.ServiceConstants.*;

@Component
public class PdfService {

    @Autowired private PdfGeneratorUtil pdfGeneratorUtil;

    private Map<String, Object> data;

    public PdfService() {
        data = new HashMap<>();
    }

    private Map<String, ArrayList<String>> getStocksData(Map<String, String> payload) {
        HashMap<String, ArrayList<String>> stocksData = new HashMap<>();
        String stocksPayload = payload.get(STOCKS);
        System.out.println("stocksPayload: " + stocksPayload);

        ArrayList<String> stocksAmount = new ArrayList<>();
        ArrayList<String> stocksMktValue = new ArrayList<>();
        ArrayList<String> stocksDOI = new ArrayList<>();
        ArrayList<String> stocksFreq = new ArrayList<>();
        if(stocksPayload==null || stocksPayload.isEmpty()){
            stocksData.put(STOCKS_AMOUNT, stocksAmount);
            stocksData.put(STOCKS_MKT_VALUE, stocksMktValue);
            stocksData.put(STOCKS_DOI, stocksDOI);
            stocksData.put(STOCKS_FREQ, stocksFreq);
            return stocksData;
        }
        JSONArray stocks = new JSONArray(stocksPayload);

        System.out.println("STOCKS FETCH");
        for(int i=0; i<stocks.length(); i++){
            System.out.println(stocks.get(i));
            JSONObject object = new JSONObject(stocks.get(i).toString());
            System.out.println("Stock: i: " + i + " Amount: " + object.getString("Amount") + " Mkt value: " +
                    object.getString("Market Value") + " DOI: " + object.getString("Date of Investment") + " Freq: " + object.getString("Frequency"));
            stocksAmount.add(object.getString(STOCKS_AMOUNT));
            stocksMktValue.add(object.getString(STOCKS_MKT_VALUE));
            stocksDOI.add(object.getString(STOCKS_DOI));
            stocksFreq.add(object.getString(STOCKS_FREQ));
        }

        stocksData.put(STOCKS_AMOUNT, stocksAmount);
        stocksData.put(STOCKS_MKT_VALUE, stocksMktValue);
        stocksData.put(STOCKS_DOI, stocksDOI);
        stocksData.put(STOCKS_FREQ, stocksFreq);

        return stocksData;
    }
    private Map<String, ArrayList<String>> getMFsData(Map<String, String> payload) {
        HashMap<String, ArrayList<String>> mfData = new HashMap<>();
        String mfPayload = payload.get(MUTUAL_FUNDS);

        ArrayList<String> mfsAmount = new ArrayList<>();
        ArrayList<String> mfsMktValue = new ArrayList<>();
        ArrayList<String> mfsType = new ArrayList<>();
        ArrayList<String> mfsDOI = new ArrayList<>();
        ArrayList<String> mfsFreq = new ArrayList<>();

        if(mfPayload == null || mfPayload.isEmpty()){
            mfData.put(MUTUAL_FUNDS_AMOUNT, mfsAmount);
            mfData.put(MUTUAL_FUNDS_MKT_VALUE, mfsMktValue);
            mfData.put(MUTUAL_FUNDS_TYPE, mfsType);
            mfData.put(MUTUAL_FUNDS_DOI, mfsDOI);
            mfData.put(MUTUAL_FUNDS_FREQ, mfsFreq);
            return mfData;
        }
        JSONArray mfs = new JSONArray(mfPayload);

        System.out.println("MF FETCH");
        for(int i=0; i<mfs.length(); i++){
            System.out.println(mfs.get(i));
            JSONObject object = new JSONObject(mfs.get(i).toString());
            System.out.println("MF: i: " + i + " Amount: " + object.getString("Amount") + " DOI: " + object.getString("Date of Investment")
                    + " Freq: " + object.getString("Frequency") + " Mrkt Value: " + object.getString("Market Value") + " Type: " + object.getString("Type"));
            mfsAmount.add(object.getString(MUTUAL_FUNDS_AMOUNT));
            mfsDOI.add(object.getString(MUTUAL_FUNDS_DOI));
            mfsFreq.add(object.getString(MUTUAL_FUNDS_FREQ));
            mfsMktValue.add(object.getString(MUTUAL_FUNDS_MKT_VALUE));
            mfsType.add(object.getString(MUTUAL_FUNDS_TYPE));
        }
        mfData.put(MUTUAL_FUNDS_AMOUNT, mfsAmount);
        mfData.put(MUTUAL_FUNDS_MKT_VALUE, mfsMktValue);
        mfData.put(MUTUAL_FUNDS_TYPE, mfsType);
        mfData.put(MUTUAL_FUNDS_DOI, mfsDOI);
        mfData.put(MUTUAL_FUNDS_FREQ, mfsFreq);

        return mfData;
    }
    private Map<String, ArrayList<String>> getLIData(Map<String, String> payload) {
        HashMap<String, ArrayList<String>> liData = new HashMap<>();
        String liPayload = payload.get(LI);

        ArrayList<String> lisSumInsured = new ArrayList<>();
        ArrayList<String> liInsuranceName = new ArrayList<>();
        ArrayList<String> lisType = new ArrayList<>();
        ArrayList<String> liPolicyTerm = new ArrayList<>();
        ArrayList<String> liPremiumAmount = new ArrayList<>();
        ArrayList<String> liPremiumPayingTerm = new ArrayList<>();
        ArrayList<String> liStartDate = new ArrayList<>();
        if(liPayload == null || liPayload.isEmpty()){
            liData.put(LI_SUM_INSURED, lisSumInsured);
            liData.put(LI_INSURANCE_NAME, liInsuranceName);
            liData.put(LI_TYPE, lisType);
            liData.put(LI_POLICY_TERM, liPolicyTerm);
            liData.put(LI_PREMIUM_AMOUNT, liPremiumAmount);
            liData.put(LI_PAYING_TERM, liPremiumPayingTerm);
            liData.put(LI_START_DATE, liStartDate);
            return liData;
        }
        JSONArray lifeinsurance = new JSONArray(liPayload);
        System.out.println("LI FETCH");
        for(int i=0; i<lifeinsurance.length(); i++){
            System.out.println(lifeinsurance.get(i));
            JSONObject object = new JSONObject(lifeinsurance.get(i).toString());
            System.out.println("LI: i: " + i + " Sum Insured: " + object.getString("Sum Insured") + " Insurance Name: " + object.getString("Insurance Name")
                    + " Policy Term: " + object.getString("Policy Term") + " Premium Amount: " + object.getString("Premium Amount")
                    + " Type: " + object.getString("Type") + " Premium Paying Term: " + object.getString("Premium Paying Term")
                    + " Start Date: " + object.getString("Start Date"));
            lisSumInsured.add(object.getString(LI_SUM_INSURED));
            liInsuranceName.add(object.getString(LI_INSURANCE_NAME));
            liPolicyTerm.add(object.getString(LI_POLICY_TERM));
            liPremiumAmount.add(object.getString(LI_PREMIUM_AMOUNT));
            lisType.add(object.getString(LI_TYPE));
            liPremiumPayingTerm.add(object.getString(LI_PAYING_TERM));
            liStartDate.add(object.getString(LI_START_DATE));
        }
        liData.put(LI_SUM_INSURED, lisSumInsured);
        liData.put(LI_INSURANCE_NAME, liInsuranceName);
        liData.put(LI_TYPE, lisType);
        liData.put(LI_POLICY_TERM, liPolicyTerm);
        liData.put(LI_PREMIUM_AMOUNT, liPremiumAmount);
        liData.put(LI_PAYING_TERM, liPremiumPayingTerm);
        liData.put(LI_START_DATE, liStartDate);

        return liData;
    }
    private Map<String, ArrayList<String>> getHIData(Map<String, String> payload) {
        HashMap<String, ArrayList<String>> hiData = new HashMap<>();
        String hiPayload = payload.get(HI);
        ArrayList<String> hisSumInsured = new ArrayList<>();
        ArrayList<String> hisInsuranceName = new ArrayList<>();
        ArrayList<String> hisFrequencyType = new ArrayList<>();
        ArrayList<String> hisPremiumAmount = new ArrayList<>();
        ArrayList<String> hiStartDate = new ArrayList<>();
        if(hiPayload==null || hiPayload.isEmpty()){
            hiData.put(HI_SUM_INSURED, hisSumInsured);
            hiData.put(HI_COMPANY_NAME, hisInsuranceName);
            hiData.put(HI_FREQ_TYPE, hisFrequencyType);
            hiData.put(HI_PREMIUM_AMOUNT, hisPremiumAmount);
            hiData.put(HI_START_DATE, hiStartDate);
            return hiData;
        }
        JSONArray healthinsurance = new JSONArray(payload.get("healthinsurance"));

        System.out.println("HI FETCH");
        for(int i=0; i<healthinsurance.length(); i++){
            System.out.println(healthinsurance.get(i));
            JSONObject object = new JSONObject(healthinsurance.get(i).toString());
            System.out.println("HI: i: " + i + " Sum Insured: " + object.getString("Sum Insured") + " Company Name : " + object.getString("Company Name")
                    + " Premium Amount: " + object.getString("Premium Amount")
                    + " Frequency Type: " + object.getString("Frequency Type") + " Start Date: " + object.getString("Start Date"));
            hisSumInsured.add(object.getString(HI_SUM_INSURED));
            hisInsuranceName.add(object.getString(HI_COMPANY_NAME));
            hisPremiumAmount.add(object.getString(HI_PREMIUM_AMOUNT));
            hisFrequencyType.add(object.getString(HI_FREQ_TYPE));
            hiStartDate.add(object.getString(HI_START_DATE));
        }
        hiData.put(HI_SUM_INSURED, hisSumInsured);
        hiData.put(HI_COMPANY_NAME, hisInsuranceName);
        hiData.put(HI_FREQ_TYPE, hisFrequencyType);
        hiData.put(HI_PREMIUM_AMOUNT, hisPremiumAmount);
        hiData.put(HI_START_DATE, hiStartDate);

        return hiData;
    }

    private Map<String, ArrayList<String>> getBondsData(Map<String, String> payload) {
        HashMap<String, ArrayList<String>> bondsData = new HashMap<>();
        String bondsPayload = payload.get(BONDS);

        ArrayList<String> bond_invested = new ArrayList<>();
        ArrayList<String> bonds_name = new ArrayList<>();
        ArrayList<String> bonds_date_of_investment = new ArrayList<>();
        ArrayList<String> bonds_maturity_date = new ArrayList<>();
        if(bondsPayload == null || bondsPayload.isEmpty()){

            bondsData.put(BONDS_NAME, bonds_name);
            bondsData.put(BONDS_INVESTED, bond_invested);
            bondsData.put(BONDS_DOI, bonds_date_of_investment);
            bondsData.put(BONDS_MATURITY_DATE, bonds_maturity_date);
            return bondsData;
        }
        JSONArray bonds = new JSONArray(bondsPayload);
        System.out.println("bonds FETCH");
        for(int i=0; i<bonds.length(); i++){
            System.out.println(bonds.get(i));
            JSONObject object = new JSONObject(bonds.get(i).toString());
            System.out.println("bonds: i: " + i + " Name: " + object.getString("Name") + " Amount: " +
                    object.getString("Amount") + " DOI: " + object.getString("Date of Investment") + " Maturity Date: " + object.getString("Maturity Date"));
            bond_invested.add(object.getString(BONDS_INVESTED));
            bonds_name.add(object.getString(BONDS_NAME));
            bonds_date_of_investment.add(object.getString(BONDS_DOI));
            bonds_maturity_date.add(object.getString(BONDS_MATURITY_DATE));
        }

        bondsData.put(BONDS_NAME, bonds_name);
        bondsData.put(BONDS_INVESTED, bond_invested);
        bondsData.put(BONDS_DOI, bonds_date_of_investment);
        bondsData.put(BONDS_MATURITY_DATE, bonds_maturity_date);

        return bondsData;
    }
    private Map<String, ArrayList<String>> getFDsData(Map<String, String> payload) {
        HashMap<String, ArrayList<String>> fdData = new HashMap<>();
        String fdPayload = payload.get(FD);

        ArrayList<String> fdsName = new ArrayList<>();
        ArrayList<String> fdsAmountInvested = new ArrayList<>();
        ArrayList<String> fdsInterestRate = new ArrayList<>();
        ArrayList<String> fdsStartDate = new ArrayList<>();
        ArrayList<String> fdsMaturityDate = new ArrayList<>();

        if(fdPayload == null || fdPayload.isEmpty()){
            fdData.put(FD_NAME, fdsName);
            fdData.put(FD_AMT_INVESTED, fdsAmountInvested);
            fdData.put(FD_INTEREST_RATE, fdsInterestRate);
            fdData.put(FD_START_DATE, fdsStartDate);
            fdData.put(FD_MATURITY_DATE, fdsMaturityDate);
            return fdData;

        }
        JSONArray fds = new JSONArray(fdPayload);

        System.out.println("fds FETCH");
        for(int i=0; i<fds.length(); i++){
            System.out.println(fds.get(i));
            JSONObject object = new JSONObject(fds.get(i).toString());
            System.out.println("fds: i: " + i + " Bank/NBFC Name: " + object.getString("Bank/NBFC Name") + " Amount Invested: " +
                    object.getString("Amount Invested") + " Interest Rate: " + object.getString("Interest Rate")
                    + " Start Date: " + object.getString("Start Date") + " Maturity Date: " + object.getString("Maturity Date"));
            fdsName.add(object.getString(FD_NAME));
            fdsAmountInvested.add(object.getString(FD_AMT_INVESTED));
            fdsStartDate.add(object.getString(FD_START_DATE));
            fdsInterestRate.add(object.getString(FD_INTEREST_RATE));
            fdsMaturityDate.add(object.getString(FD_MATURITY_DATE));

        }
        fdData.put(FD_NAME, fdsName);
        fdData.put(FD_AMT_INVESTED, fdsAmountInvested);
        fdData.put(FD_INTEREST_RATE, fdsInterestRate);
        fdData.put(FD_START_DATE, fdsStartDate);
        fdData.put(FD_MATURITY_DATE, fdsMaturityDate);

        return fdData;
    }
    private Map<String, ArrayList<String>> getPPFData(Map<String, String> payload) {
        HashMap<String, ArrayList<String>> ppfData = new HashMap<>();
        String ppfPayload = payload.get(PPF);

        ArrayList<String> ppfsInvested = new ArrayList<>();
        ArrayList<String> ppfsDOI = new ArrayList<>();
        ArrayList<String> ppfsFreq = new ArrayList<>();
        if(ppfPayload == null || ppfPayload.isEmpty()){
            ppfData.put(PPF_AMOUNT, ppfsInvested);
            ppfData.put(PPF_DOI, ppfsDOI);
            ppfData.put(PPF_FREQ, ppfsFreq);
            return ppfData;
        }
        JSONArray ppfs = new JSONArray(ppfPayload);

        System.out.println("PPF FETCH");
        for(int i=0; i<ppfs.length(); i++){
            System.out.println(ppfs.get(i));
            JSONObject object = new JSONObject(ppfs.get(i).toString());
            System.out.println("PPF: i: " + i + " Amount: " + object.getString("Amount") + " DOI: " + object.getString("Date of Investment")
                    + " Freq: " + object.getString("Frequency"));
            ppfsInvested.add(object.getString(PPF_AMOUNT));
            ppfsDOI.add(object.getString(PPF_DOI));
            ppfsFreq.add(object.getString(PPF_FREQ));
        }
        ppfData.put(PPF_AMOUNT, ppfsInvested);
        ppfData.put(PPF_DOI, ppfsDOI);
        ppfData.put(PPF_FREQ, ppfsFreq);
        return ppfData;
    }
    private Map<String, ArrayList<String>> getLoanEmiData(Map<String, String> payload) {
        HashMap<String, ArrayList<String>> loanData = new HashMap<>();
        String loanPayload = payload.get(LOAN_EMI);

        ArrayList<String> loanAmount = new ArrayList<>();
        ArrayList<String> loanNoInstallments = new ArrayList<>();
        ArrayList<String> loanAmountInstallment = new ArrayList<>();
        ArrayList<String> loanType = new ArrayList<>();
        ArrayList<String> loanStartDate = new ArrayList<>();

        if(loanPayload == null || loanPayload.isEmpty()){
            loanData.put(LOAN_EMI_AMOUNT, loanAmount);
            loanData.put(LOAN_EMI_NO_INSTALLMENTS_LEFT, loanNoInstallments);
            loanData.put(LOAN_EMI_AMOUNT_OF_INSTALLMENT, loanAmountInstallment);
            loanData.put(LOAN_EMI_TYPE, loanType);
            loanData.put(LOAN_EMI_START_DATE, loanStartDate);
            return loanData;
        }
        JSONArray loanemi = new JSONArray(loanPayload);
        System.out.println("loanemi FETCH");
        for(int i=0; i<loanemi.length(); i++){
            System.out.println(loanemi.get(i));
            JSONObject object = new JSONObject(loanemi.get(i).toString());
            System.out.println("Loan: i: " + i + " Loan Amount: " + object.getString("Loan Amount") + " No of Installments Left : " + object.getString("No of Installments Left")
                    + " Amount of Installment:: " + object.getString("Amount of Installment")
                    + " Type: " + object.getString("Type") + " Start Date: " + object.getString("Start Date"));
            loanAmount.add(object.getString(LOAN_EMI_AMOUNT));
            loanNoInstallments.add(object.getString(LOAN_EMI_NO_INSTALLMENTS_LEFT));
            loanAmountInstallment.add(object.getString(LOAN_EMI_AMOUNT_OF_INSTALLMENT));
            loanType.add(object.getString(LOAN_EMI_TYPE));
            loanStartDate.add(object.getString(LOAN_EMI_START_DATE));
        }

        loanData.put(LOAN_EMI_AMOUNT, loanAmount);
        loanData.put(LOAN_EMI_NO_INSTALLMENTS_LEFT, loanNoInstallments);
        loanData.put(LOAN_EMI_AMOUNT_OF_INSTALLMENT, loanAmountInstallment);
        loanData.put(LOAN_EMI_TYPE, loanType);
        loanData.put(LOAN_EMI_START_DATE, loanStartDate);
        return loanData;
    }
    private Map<String, ArrayList<String>> getDependentsData(Map<String, String> payload) {
        HashMap<String, ArrayList<String>> dependentsData = new HashMap<>();
        String dependentsPayload = payload.get(DEPENDENTS);
        System.out.println("dependentsPayload: " + dependentsPayload);

        ArrayList<String> dependents_name = new ArrayList<>();
        ArrayList<String> dependents_dob = new ArrayList<>();
        ArrayList<String> dependents_relationship = new ArrayList<>();
        if(dependentsPayload ==null || dependentsPayload.isEmpty()){
            dependentsData.put(DEPENDENTS_NAME, dependents_name);
            dependentsData.put(DEPENDENTS_DOB, dependents_dob);
            dependentsData.put(DEPENDENTS_RELATION, dependents_relationship);
            return dependentsData;
        }
        JSONArray dependents = new JSONArray(dependentsPayload);
        System.out.println("Dependents FETCH");
        for(int i=0; i<dependents.length(); i++){
            System.out.println(dependents.get(i));
            JSONObject object = new JSONObject(dependents.get(i).toString());
            System.out.println("Dependents: i: " + i + " Name: " + object.getString("Name") + " Date of Birth : " + object.getString("Date of Birth")
                    + " Relationship: " + object.getString("Relationship"));

            dependents_name.add(object.getString(DEPENDENTS_NAME));
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println(sdf2.format(sdf.parse(object.getString(DEPENDENTS_DOB))));
                dependents_dob.add(sdf2.format(sdf.parse(object.getString(DEPENDENTS_DOB))));
            } catch (Exception ex){

            }
            dependents_relationship.add(object.getString(DEPENDENTS_RELATION));
        }

        dependentsData.put(DEPENDENTS_NAME, dependents_name);
        dependentsData.put(DEPENDENTS_DOB, dependents_dob);
        dependentsData.put(DEPENDENTS_RELATION, dependents_relationship);

        return dependentsData;
    }

    private Map<String, Object> getAllData(Map<String, String> payload) {
        Map<String, Object> koshantra_data = new HashMap<>();

        koshantra_data.put("first_name", payload.get("name[first]"));
        koshantra_data.put("last_name", payload.get("name[last]"));
        koshantra_data.put("dob", payload.get("dateof[year]")+"-"+payload.get("dateof[month]")+"-"+payload.get("dateof[day]"));
        koshantra_data.put("profession", payload.get("profession"));
        koshantra_data.put("income", payload.get("annualincome"));
        koshantra_data.put("expense", payload.get("annualexpense"));
        koshantra_data.put("mobile_no", payload.get("phonenumber[full]"));
        koshantra_data.put("gender", payload.get("gender"));
        koshantra_data.put("email", payload.get("email"));
        koshantra_data.put("address", payload.get("address[addr_line1]")+","+payload.get("address[addr_line2]")+","+payload.get("address[city]")+","
        +payload.get("address[state]")+","+payload.get("address[postal]"));
        koshantra_data.put("risk_profile", payload.get("riskprofile"));
        String[] lifeGoals = payload.get("lifegoals").split("\n");
        System.out.println("LIFE GOALS: " + Arrays.toString(lifeGoals));
        StringBuilder goals = new StringBuilder();
        for(String lifeGoal: lifeGoals){
            goals.append(lifeGoal).append(",");
        }
        System.out.println("GOALS: " + goals);
        koshantra_data.put("life_goals", goals.substring(0, goals.length()-1));

        List<String> dependentsColumns = Arrays.asList("Name", "DOB", "Relation");
        List<Map<String,Object>> dependentsData = new ArrayList<>();
        Map<String, ArrayList<String>> dependentsMap = getDependentsData(payload);
        for(int i = 0; i<dependentsMap.get(DEPENDENTS_NAME).size(); i++){
            dependentsData.add(Map.of("Name", dependentsMap.get(DEPENDENTS_NAME).get(i),
                    "DOB", dependentsMap.get(DEPENDENTS_DOB).get(i),
                    "Relation", dependentsMap.get(DEPENDENTS_RELATION).get(i)));
        }
        koshantra_data.put("dependentsColumns", dependentsColumns);
        koshantra_data.put("dependentsData", dependentsData);
        koshantra_data.put("number_of_dependents", dependentsMap.get(DEPENDENTS_NAME).size());


        List<String> stocksColumns = Arrays.asList("Name", "Amount", "Market Value", "Date of Purchase", "Frequency");
        List<String> stocksColumnsAllData = Arrays.asList("Name", "Amount", "Original Amount", "Market Value", "Date of Purchase", "Frequency");
        List<Map<String,Object>> stocksData = new ArrayList<>();

        Map<String, ArrayList<String>> stocks = getStocksData(payload);

        Long stock_full_amount = 0L, mfs_full_amount = 0L, ppfs_full_amount = 0L;
        for(int i=0;i<stocks.get(STOCKS_AMOUNT).size(); i++){
            long amount = getAmountFrom(stocks.get(STOCKS_FREQ).get(i), stocks.get(STOCKS_AMOUNT).get(i), stocks.get(STOCKS_DOI).get(i));
            stock_full_amount += amount;
            stocksData.add(Map.of(
                    "Amount", amount,
                    "Original Amount", stocks.get(STOCKS_AMOUNT).get(i),
                    "Market Value", stocks.get(STOCKS_MKT_VALUE).get(i),
                    "Date of Purchase", stocks.get(STOCKS_DOI).get(i),
                    "Frequency", stocks.get(STOCKS_FREQ).get(i)));
        }
        koshantra_data.put("stocksColumns", stocksColumns);
        koshantra_data.put("stocksData", stocksData);
        koshantra_data.put("stocksColumnsAllData", stocksColumnsAllData);

        List<String> mfsColumns = Arrays.asList("Market Value", "Amount", "Type", "Date of Purchase", "Frequency");
        List<String> mfsColumnsAllData = Arrays.asList("Market Value", "Amount", "Original Amount", "Type", "Date of Purchase", "Frequency");
        List<Map<String,Object>> mfsData = new ArrayList<>();
        Map<String, ArrayList<String>> mfPayload = getMFsData(payload);

        for(int i=0;i<mfPayload.get(MUTUAL_FUNDS_AMOUNT).size(); i++){
            long amount = getAmountFrom(mfPayload.get(MUTUAL_FUNDS_FREQ).get(i), mfPayload.get(MUTUAL_FUNDS_AMOUNT).get(i), mfPayload.get(MUTUAL_FUNDS_DOI).get(i));
            mfs_full_amount += amount;
            mfsData.add(Map.of("Type", mfPayload.get(MUTUAL_FUNDS_TYPE).get(i),
                    "Amount", amount,
                    "Original Amount", mfPayload.get(MUTUAL_FUNDS_AMOUNT).get(i),
                    "Market Value", mfPayload.get(MUTUAL_FUNDS_MKT_VALUE).get(i),
                    "Date of Purchase", mfPayload.get(MUTUAL_FUNDS_DOI).get(i),
                    "Frequency", mfPayload.get(MUTUAL_FUNDS_FREQ).get(i)));
        }
        koshantra_data.put("mfsColumns", mfsColumns);
        koshantra_data.put("mfsData", mfsData);
        koshantra_data.put("mfsColumnsAllData", mfsColumnsAllData);

        List<String> lisColumns = Arrays.asList("Start Date", "Premium Paying Term", "Type", "Sum Assured", "Name", "Policy Term", "Premium Amount");
        List<Map<String,Object>> lisData = new ArrayList<>();
        Map<String, ArrayList<String>> liPayload = getLIData(payload);
        Map<String, ArrayList<String>> hiPayload = getHIData(payload);


        for(int i=0;i<liPayload.get(LI_PREMIUM_AMOUNT).size(); i++){
            lisData.add(Map.of("Name", liPayload.get(LI_INSURANCE_NAME).get(i),
                    "Start Date", liPayload.get(LI_START_DATE).get(i),
                    "Premium Paying Term", liPayload.get(LI_PAYING_TERM).get(i),
                    "Sum Assured", liPayload.get(LI_SUM_INSURED).get(i),
                    "Type", liPayload.get(LI_TYPE).get(i),
                    "Policy Term", liPayload.get(LI_POLICY_TERM).get(i),
                    "Premium Amount", liPayload.get(LI_PREMIUM_AMOUNT).get(i)));
        }
        koshantra_data.put("lisColumns", lisColumns);
        koshantra_data.put("lisData", lisData);

        List<String> hisColumns = Arrays.asList("Name", "Premium Amount", "Type", "Sum Insured", "Start Date");
        List<Map<String,Object>> hisData = new ArrayList<>();
        for(int i=0;i<hiPayload.get(HI_PREMIUM_AMOUNT).size(); i++){
            hisData.add(Map.of("Name", hiPayload.get(HI_COMPANY_NAME).get(i),
                    "Premium Amount", hiPayload.get(HI_PREMIUM_AMOUNT).get(i),
                    "Frequency Type", hiPayload.get(HI_FREQ_TYPE).get(i),
                    "Sum Insured", hiPayload.get(HI_SUM_INSURED).get(i),
                    "Start Date", hiPayload.get(HI_START_DATE).get(i)));
        }
        koshantra_data.put("hisColumns", hisColumns);
        koshantra_data.put("hisData", hisData);

        List<String> bondsColumns = Arrays.asList("Name", "Invested", "Maturity Date", "Date of Investment");
        List<Map<String,Object>> bondsData = new ArrayList<>();
        Map<String, ArrayList<String>> bondsMap = getBondsData(payload);

        for(int i=0;i<bondsMap.get(BONDS_NAME).size(); i++){
            bondsData.add(Map.of("Name", bondsMap.get(BONDS_NAME).get(i),
                    "Invested", bondsMap.get(BONDS_INVESTED).get(i),
                    "Maturity Date", bondsMap.get(BONDS_MATURITY_DATE).get(i),
                    "Date of Investment", bondsMap.get(BONDS_DOI).get(i)));
        }
        koshantra_data.put("bondsColumns", bondsColumns);
        koshantra_data.put("bondsData", bondsData);

        List<String> ppfsColumns = Arrays.asList("Invested", "Date of Investment", "Frequency");
        List<String> ppfsColumnsAllData = Arrays.asList("Invested", "Original Amount", "Date of Investment", "Frequency");
        List<Map<String,Object>> ppfsData = new ArrayList<>();

        Map<String, ArrayList<String>> ppfPayload = getPPFData(payload);
        for(int i=0;i<ppfPayload.get(PPF_AMOUNT).size(); i++){
            long amount = getAmountFrom(ppfPayload.get(PPF_FREQ).get(i), ppfPayload.get(PPF_AMOUNT).get(i), ppfPayload.get(PPF_DOI).get(i));
            ppfs_full_amount += amount;
            ppfsData.add(Map.of("Invested", amount,
                    "Original Amount", ppfPayload.get(PPF_AMOUNT).get(i),
                    "Date of Investment", ppfPayload.get(PPF_DOI).get(i),
                    "Frequency", ppfPayload.get(PPF_FREQ).get(i)));
        }
        koshantra_data.put("ppfsColumns", ppfsColumns);
        koshantra_data.put("ppfsData", ppfsData);
        koshantra_data.put("ppfsColumnsAllData", ppfsColumnsAllData);

        List<String> fdsColumns = Arrays.asList("Name", "Amount", "Start Date", "Maturity Date", "Interest Rate");
        List<Map<String,Object>> fdsData = new ArrayList<>();

        Map<String, ArrayList<String>> fdPayload = getFDsData(payload);

        for(int i=0;i<fdPayload.get(FD_NAME).size(); i++){
            fdsData.add(Map.of("Name", fdPayload.get(FD_NAME).get(i),
                    "Amount", fdPayload.get(FD_AMT_INVESTED).get(i),
                    "Start Date", fdPayload.get(FD_START_DATE).get(i),
                    "Maturity Date", fdPayload.get(FD_MATURITY_DATE).get(i),
                    "Interest Rate", fdPayload.get(FD_INTEREST_RATE).get(i)));
        }
        koshantra_data.put("fdsColumns", fdsColumns);
        koshantra_data.put("fdsData", fdsData);

        List<String> loanoremisColumns = Arrays.asList("Start Date", "Installment Amount", "Installment Left", "Type", "Amount");
        List<Map<String,Object>> loanoremisData = new ArrayList<>();

        Map<String, ArrayList<String>> loanPayload = getLoanEmiData(payload);
        for(int i=0;i<loanPayload.get(LOAN_EMI_AMOUNT).size(); i++){
            loanoremisData.add(Map.of("Start Date", loanPayload.get(LOAN_EMI_START_DATE).get(i),
                    "Installment Amount", loanPayload.get(LOAN_EMI_AMOUNT_OF_INSTALLMENT).get(i),
                    "Installment Left", loanPayload.get(LOAN_EMI_NO_INSTALLMENTS_LEFT).get(i),
                    "Type", loanPayload.get(LOAN_EMI_TYPE).get(i),
                    "Amount", loanPayload.get(LOAN_EMI_AMOUNT).get(i)));
        }
        koshantra_data.put("loanoremisColumns", loanoremisColumns);
        koshantra_data.put("loanoremisData", loanoremisData);

        return koshantra_data;
    }


    private void getPersonalInformation(String name, Object dateOfBirth, String profession, String mobileNo, ArrayList<String> dependentsName, ArrayList<String> dependentsRelation, ArrayList<String> dependentsDOB) {
        List<String> personalInfoColumns = Arrays.asList("Relationship", "Name", "DOB", "Occupation", "Mobile No.");
        List<Map<String,Object>> personalInfoData = new ArrayList<>();
        personalInfoData.add(Map.of("Relationship", "Self", "Name", name, "DOB", dateOfBirth, "Occupation", profession, "Mobile No.", mobileNo));

        for(int i = 0; i<dependentsName.size(); i++){
            personalInfoData.add(Map.of("Relationship", dependentsRelation.get(i),
                    "Name", dependentsName.get(i),
                    "DOB", dependentsDOB.get(i)));
        }

        this.data.put("personalInfoColumns", personalInfoColumns);
        this.data.put("personalInfoData", personalInfoData);
    }

    private void getIncomeInfo(String income, String profession, DecimalFormat df) {
        List<String> incomeColumns = Arrays.asList("Income", "Monthly(Rs.)", "Annually(Rs.)");
        List<Map<String,Object>> incomeData = new ArrayList<>();
        if(profession.equals("Self Employed")){
            incomeData.add(Map.of("Income", "Business Income", "Monthly(Rs.)", df.format(Long.parseLong(income)/12.0), "Annually(Rs.)", income));
            incomeData.add(Map.of("Income", "Income Salary", "Monthly(Rs.)", "0", "Annually(Rs.)", "0"));
        }else{
            incomeData.add(Map.of("Income", "Business Income", "Monthly(Rs.)", "0", "Annually(Rs.)", "0"));
            incomeData.add(Map.of("Income", "Income Salary", "Monthly(Rs.)", df.format(Long.parseLong(income)/12.0), "Annually(Rs.)", income));
        }
        data.put("incomeColumns", incomeColumns);
        data.put("incomeData", incomeData);

        List<Map<String, Object>> incomeTotal = new ArrayList<>();
        incomeTotal.add(Map.of("Income", "Total", "Monthly(Rs.)", df.format(Long.parseLong(income)/12.0), "Annually(Rs.)", income));

        data.put("incomeTotal", incomeTotal);
    }

    private void getCashFlow(String income, String expense, String fileName) {
        try {
            List<String> xAxis = Arrays.asList("Income", "Expenses");
            List<String> values = Arrays.asList(income, expense);
            createBarChart(xAxis, values, "", "INR (₹)", "","src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg");
            data.put("cash_flow_chart", "src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Long getTotalBondsAmount(ArrayList<String> bondsInvested){
        return bondsInvested == null || bondsInvested.isEmpty()? 0L : bondsInvested.stream().map(Long::parseLong).reduce(0L, Long::sum);
    }

    private Long getTotalFDsAmount(ArrayList<String> fdAmount) {
        return fdAmount == null || fdAmount.isEmpty()? 0L : fdAmount.stream().map(Long::parseLong).reduce(0L, Long::sum);
    }

    private Long getStocksInfo(ArrayList<String> stocksAmount, ArrayList<String> stocksMarketVal, ArrayList<String> stocksDateOfPurchase, ArrayList<String> stocksFreq) {
        List<Map<String,Object>> stocksData = new ArrayList<>();

        Long stockFullAmount = 0L;
        for(int i=0;i<stocksAmount.size(); i++){
            long amount = getAmountFrom(stocksFreq.get(i), stocksAmount.get(i), stocksDateOfPurchase.get(i));
            stockFullAmount += amount;
            stocksData.add(Map.of(
                    "Amount", amount,
                    "Original Amount", stocksAmount.get(i),
                    "Market Value", stocksMarketVal.get(i),
                    "Date of Purchase", stocksDateOfPurchase.get(i),
                    "Frequency", stocksFreq.get(i)));
        }

        return stockFullAmount;
    }

    private Long getMFsInfo(ArrayList<String> mfAmount, ArrayList<String> mfMarketVal, ArrayList<String> mfType, ArrayList<String> mfDateOfPurchase, ArrayList<String> mfFreq) {
        Long mfsFullAmount = 0L;
        List<Map<String,Object>> mfsData = new ArrayList<>();
        for(int i=0;i<mfFreq.size(); i++){
            long amount = getAmountFrom(mfFreq.get(i), mfAmount.get(i), mfDateOfPurchase.get(i));
            mfsFullAmount += amount;
            mfsData.add(Map.of("Type", mfType.get(i),
                    "Amount", amount,
                    "Original Amount", mfAmount.get(i),
                    "Market Value", mfMarketVal.get(i),
                    "Date of Purchase", mfDateOfPurchase.get(i),
                    "Frequency", mfFreq.get(i)));
        }

        return mfsFullAmount;
    }

    private Long getPPFsInfo(ArrayList<String> ppfInvested, ArrayList<String> ppfDateOfInvestment, ArrayList<String> ppfFreq) {
        Long ppfFullAmount = 0L;
        List<Map<String,Object>> ppfsData = new ArrayList<>();
        for(int i=0;i<ppfInvested.size(); i++){
            long amount = getAmountFrom(ppfFreq.get(i), ppfInvested.get(i), ppfDateOfInvestment.get(i));
            ppfFullAmount += amount;
            ppfsData.add(Map.of("Invested", amount,
                    "Original Amount", ppfInvested.get(i),
                    "Date of Investment", ppfDateOfInvestment.get(i),
                    "Frequency", ppfFreq.get(i)));
        }

        return ppfFullAmount;
    }

    private void getAssetAllocationChart(String fileName, float equityPercentage, float debtPercentage, float idealEquityPercentage, float idealDebtPercentage) {
        try {
            List<String> items = Arrays.asList("Equity ("+equityPercentage+"%)", "Debt ("+debtPercentage+"%)");
            List<Float> itemValues = Arrays.asList(equityPercentage, debtPercentage);
            createPieChart(items, itemValues, "Current Asset Allocation", "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg");
            data.put("pie_chart_1", "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg");

            items = Arrays.asList("Equity ("+idealEquityPercentage+"%)", "Debt ("+idealDebtPercentage+"%)");
            itemValues = Arrays.asList(idealEquityPercentage, idealDebtPercentage);
            createPieChart(items, itemValues, "Recommended Asset Allocation", "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg");
            data.put("pie_chart_2", "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getFinancialGoals(String lifeGoals, String DOB, String name, String annualExpense, ArrayList<String> dependentsName, ArrayList<String> dependentsRelation, ArrayList<String> dependentsDOB) {
        List<String> financialGoalsColumns = Arrays.asList("Goal Name", "Years to Goal", "Present Value", "Growth Rate(%)", "Future Cost(Rs.)");
        List<Map<String,Object>> financialGoalsData = new ArrayList<>();
        if(lifeGoals != null) {
            String[] life_goals_list = lifeGoals.split("\n");
            System.out.println("life goals: " + Arrays.toString(life_goals_list));
            for (String life_goal : life_goals_list) {
                if (life_goal.trim().equals("Child's Education")) {
                    //child dependents
                    for (int i = 0; i < dependentsRelation.size(); i++) {
                        if (dependentsRelation.get(i).equals("Child")) {
                            LocalDate curDate = LocalDate.now();
                            int age = Period.between(LocalDate.parse(dependentsDOB.get(i)), curDate).getYears();
                            if (age < 18) {
                                double rate = 0.08;
                                int present_value = 700000;
                                double futureCost = (present_value) * Math.pow(1 + rate, (18-age));

                                financialGoalsData.add(Map.of("Goal Name", dependentsName.get(i) + "-Graduation",
                                        "Years to Goal", (18 - age),
                                        "Present Value", present_value,
                                        "Growth Rate(%)", (rate*100),
                                        "Future Cost(Rs.)", Math.round(futureCost)));
                            }
                            if (age < 21) {
                                double rate = 0.08;
                                int present_value = 2000000;
                                double futureCost = (present_value) * Math.pow(1 + rate, (21 - age));
                                financialGoalsData.add(Map.of("Goal Name", dependentsName.get(i) + "-Higher Studies",
                                        "Years to Goal", (21 - age),
                                        "Present Value", present_value,
                                        "Growth Rate(%)", (rate*100),
                                        "Future Cost(Rs.)", Math.round(futureCost)));
                            }
                        }
                    }
                }
                if (life_goal.trim().equals("Self Marriage")) {
                    LocalDate curDate = LocalDate.now();
                    int age = Period.between(LocalDate.parse(DOB), curDate).getYears(), present_value = 2000000;
                    double rate = 0.06;
                    double futureCost = (present_value) * Math.pow(1 + rate, (25 - age));
                    financialGoalsData.add(Map.of("Goal Name", name + "-Marriage",
                            "Years to Goal", (25 - age),
                            "Present Value", present_value,
                            "Growth Rate(%)", (rate*100),
                            "Future Cost(Rs.)", Math.round(futureCost)));
                }
                if (life_goal.trim().equals("Retirement")) {
                    LocalDate curDate = LocalDate.now();
                    int age = Period.between(LocalDate.parse(DOB), curDate).getYears();
                    double rate = 0.06;
                    double futureCost = (Long.parseLong(annualExpense)) * Math.pow(1 + rate, (58 - age));
                    financialGoalsData.add(Map.of("Goal Name", name + "-Retirement",
                            "Years to Goal", (58 - age),
                            "Present Value", annualExpense,
                            "Growth Rate(%)", (rate*100),
                            "Future Cost(Rs.)", Math.round(futureCost)));
                }
                if (life_goal.trim().equals("Child's Marriage")) {
                    for (int i = 0; i < dependentsRelation.size(); i++) {
                        if (dependentsRelation.get(i).equals("Child")) {
                            LocalDate curDate = LocalDate.now();
                            int age = Period.between(LocalDate.parse(dependentsDOB.get(i)), curDate).getYears();
                            double rate = 0.06;
                            int present_value = 2000000;
                            double futureCost = (present_value) * Math.pow(1 + rate, (25 - age));
                            financialGoalsData.add(Map.of("Goal Name", dependentsName.get(i) + "-Marriage",
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
        data.put("financialGoalsColumns", financialGoalsColumns);
        data.put("financialGoalsData", financialGoalsData);
    }

    private void getActionPlan(String expense, String income, float idealEquityPercentage, float idealDebtPercentage,
                               Long additionalLiRequired, Long additionalHiRequired, Long existingEquityAmount, float equityPercentage,
                               float debtPercentage, Long total, String name) {

        Long emergency_fund = Long.parseLong(expense)/2; //Formual 6 * monthly expense
        long idealEquityAmount = ((long)idealEquityPercentage * (Long.parseLong(income)/12))* 100;
        long idealDebtAmount = ((long)idealDebtPercentage * (Long.parseLong(income)/12))*100;

        data.put("emergency_fund", emergency_fund);
        data.put("ideal_equity_amount", idealEquityAmount);
        data.put("ideal_debt_amount", idealDebtAmount);
        data.put("additional_li_required", additionalLiRequired);
        data.put("additional_hi_required", additionalHiRequired);
        if(existingEquityAmount > idealEquityAmount){
            long switch_out_equity = (long) (((idealEquityPercentage-equityPercentage) * (total==0?1:total)) / 100);
            long switch_in_debt = (long) (((idealDebtPercentage-debtPercentage) * (total==0?1:total)) / 100);
            data.put("switch_out_in_equity", "Switch Out");
            data.put("switch_out_in_debt", "Switch In");
            data.put("switch_out_in_equity_amount", switch_out_equity);
            data.put("switch_out_in_debt_amount", switch_in_debt);
        }else{
            long switch_in_equity = (long) (((idealEquityPercentage-equityPercentage) * (total==0?1:total)) / 100);
            long switch_out_debt = (long) (((idealDebtPercentage-debtPercentage) * (total==0?1:total)) / 100);
            data.put("switch_out_in_equity", "Switch In");
            data.put("switch_out_in_debt", "Switch Out");
            data.put("switch_out_in_equity_amount", switch_in_equity);
            data.put("switch_out_in_debt_amount", switch_out_debt);
        }
        List<String> LifeInsuranceColumns = Arrays.asList("Person To Be Assured", "Addl. Coverage Required");
        List<String> HealthInsuranceColumns = Arrays.asList("Person To Be Insured", "Addl. Coverage Required");

        List<Map<String,Object>> LifeInsuranceData = new ArrayList<>();
        List<Map<String,Object>> HealthInsuranceData = new ArrayList<>();

        LifeInsuranceData.add(Map.of("Person To Be Assured", name,
                "Addl. Coverage Required", additionalLiRequired+""));
        HealthInsuranceData.add(Map.of("Person To Be Insured", name,
                "Addl. Coverage Required", additionalHiRequired+""));

        data.put("LifeInsuranceColumns", LifeInsuranceColumns);
        data.put("HealthInsuranceColumns", HealthInsuranceColumns);
        data.put("LifeInsuranceData", LifeInsuranceData);
        data.put("HealthInsuranceData", HealthInsuranceData);
    }

    private void getInsuranceChart(Long total_li_required, Long lis_amount, Long additional_li_required, Long ideal_hi_amount, Long his_amount,
                                   Long additional_hi_required, String fileName) {
        try {
            List<String> xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
            List<String> values = Arrays.asList(total_li_required+"", lis_amount+"", additional_li_required+"");
            createBarChart(xAxis, values, "Life Insurance Client", "INR (₹)", "","src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg");
            data.put("life_insurance_chart", "src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg");
            xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
            values = Arrays.asList(ideal_hi_amount+"", his_amount+"", additional_hi_required+"");
            createBarChart(xAxis, values, "Health Insurance Client", "INR (₹)", "","src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg");
            data.put("health_insurance_chart", "src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg");
            //TODO: add chart name as health-ID(from DB)
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getNetworth(ArrayList<String> loanEmiType, ArrayList<String> loanEmiAmount, Long existingEquityAmount, float equityPercentage,
                             Long existingDebtAmount, float debtPercentage, Long total) {
        Long homeLoan = 0L, personaLoan = 0L;
        for(int i=0; i<loanEmiType.size(); i++){
            String loan_type = loanEmiType.get(i);
            if(loan_type.equals("Home Loan")){
                homeLoan += Long.parseLong(loanEmiAmount.get(i));
            }else if(loan_type.equals("Personal Loan")){
                personaLoan += Long.parseLong(loanEmiAmount.get(i));
            }
        }


        List<String> assetsColumns = Arrays.asList("Financial Assets", "Amount", "Percentage");
        List<String> liabilitiesColumns = Arrays.asList("Liability Type", "Amount");

        List<Map<String,Object>> assetsData = new ArrayList<>();
        assetsData.add(Map.of("Financial Assets", "Equity", "Amount", existingEquityAmount+"", "Percentage", equityPercentage+"%"));
        assetsData.add(Map.of("Financial Assets", "Debt/Fixed Income Asset", "Amount", existingDebtAmount+"", "Percentage", debtPercentage+"%"));
        assetsData.add(Map.of("Financial Assets", "Total", "Amount", total+"", "Percentage", "100%"));

        List<Map<String,Object>> liabilitiesData = new ArrayList<>();
        liabilitiesData.add(Map.of("Liability Type", "Home Loan", "Amount", homeLoan+"" ));
        liabilitiesData.add(Map.of("Liability Type", "Personal Loan", "Amount", personaLoan+""));
        liabilitiesData.add(Map.of("Liability Type", "Total", "Amount", homeLoan+personaLoan+""));

        data.put("assetsColumns", assetsColumns);
        data.put("liabilitiesColumns", liabilitiesColumns);
        data.put("assetsData", assetsData);
        data.put("liabilitiesData", liabilitiesData);
    }

    private void getFormattedData(Map<String, String> payload) {
        data.put("first_name", payload.get("name[first]"));
        data.put("last_name", payload.get("name[last]"));

        Map<String, ArrayList<String>> dependentsMap = getDependentsData(payload);

        //PI
        data.put("logo", "src/main/resources/templates/images/Wealth_Creation_Logo.png");
        data.put("client_name", payload.get("name[first]")+" "+payload.get("name[last]"));

        String fileName = payload.get("name[first]")+"_"+payload.get("name[last]");
        data.put("ID",fileName);

        DecimalFormat df = new DecimalFormat("0.00"); /*df.format(futureCost)*/


        String dob = payload.get("dateof[year]") + "-" + payload.get("dateof[month]") + "-" + payload.get("dateof[day]");
        getPersonalInformation(payload.get("name[first]")+" "+payload.get("name[last]"), dob, payload.get("profession"),
                payload.get("phonenumber[full]"), dependentsMap.get(DEPENDENTS_NAME), dependentsMap.get(DEPENDENTS_RELATION), dependentsMap.get(DEPENDENTS_DOB));

        getIncomeInfo(payload.get("annualincome"), payload.get("profession"), df);

        getCashFlow(payload.get("annualincome"), payload.get("annualexpense"), fileName);

        getFinancialGoals(payload.get("lifegoals"), dob, payload.get("name[first]")+" "+payload.get("name[last]"),
                payload.get("annualexpense"), dependentsMap.get(DEPENDENTS_NAME), dependentsMap.get(DEPENDENTS_RELATION), dependentsMap.get(DEPENDENTS_DOB));

        Map<String, ArrayList<String>> bondsMap = getBondsData(payload);
        Long bondsFullAmount = getTotalBondsAmount(bondsMap.get(BONDS_INVESTED));

        Map<String, ArrayList<String>> fdPayload = getFDsData(payload);
        Long fdFullAmount = getTotalFDsAmount(fdPayload.get(FD_AMT_INVESTED));

        Map<String, ArrayList<String>> stocksData = getStocksData(payload);
        Long stockFullAmount = getStocksInfo(stocksData.get(STOCKS_AMOUNT), stocksData.get(STOCKS_MKT_VALUE),stocksData.get(STOCKS_DOI), stocksData.get(STOCKS_FREQ));

        Map<String, ArrayList<String>> ppfPayload = getPPFData(payload);
        Long ppfFullAmount = getPPFsInfo(ppfPayload.get(PPF_AMOUNT), ppfPayload.get(PPF_DOI), ppfPayload.get(PPF_FREQ));

        Map<String, ArrayList<String>> mfPayload = getMFsData(payload);
        Long mfFullAmount = getMFsInfo(mfPayload.get(MUTUAL_FUNDS_AMOUNT), mfPayload.get(MUTUAL_FUNDS_MKT_VALUE),mfPayload.get(MUTUAL_FUNDS_TYPE), mfPayload.get(MUTUAL_FUNDS_DOI), mfPayload.get(MUTUAL_FUNDS_FREQ));

        Long existing_equity_amount = stockFullAmount + mfFullAmount;
        Long existing_debt_amount =  bondsFullAmount + ppfFullAmount + fdFullAmount;
        Long total = existing_equity_amount + existing_debt_amount;
        float equity_percentage = 0F, debt_percentage = 0F;
        try {
            equity_percentage = total.equals(0L) ? 0F : Float.valueOf(df.format((existing_equity_amount / (total*1.00)) * (100.00)));//add point upto 2 decimal
            debt_percentage = 100 - equity_percentage;
        }catch (Exception e){
            e.printStackTrace();
        }
        int age = Period.between(LocalDate.parse(dob), LocalDate.now()).getYears();
        float ideal_equity_percentage = Float.valueOf(df.format(100-age));
        float ideal_debt_percentage = age;

        getAssetAllocationChart(fileName, equity_percentage, debt_percentage, ideal_equity_percentage, ideal_debt_percentage);

        Map<String, ArrayList<String>> liPayload = getLIData(payload);
        Map<String, ArrayList<String>> hiPayload = getHIData(payload);

        Long lis_amount = (liPayload.get(LI_SUM_INSURED)).stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long his_amount = (hiPayload.get(HI_SUM_INSURED)).stream().map(Long::parseLong).reduce(0L, Long::sum);

        Long total_li_required = 20 * (Long.parseLong(payload.get("annualincome")));
        Long additional_li_required = (total_li_required) - lis_amount;
        Long ideal_hi_amount = 0L;
        if(age>=21 && age<30 && (!dependentsMap.get(DEPENDENTS_DOB).isEmpty())) ideal_hi_amount = 7_50_000L;
        else if(age>=21 && age<30 ) ideal_hi_amount = 5_00_000L;
        else if(age>=30 && age <40) ideal_hi_amount = 10_00_000L;
        else if (age>=40 && age<50) ideal_hi_amount = 15_00_000L;
        else if(age>=50) ideal_hi_amount = 20_00_000L;
        Long additional_hi_required = ideal_hi_amount - his_amount;

        getActionPlan(payload.get("annualexpense"), payload.get("annualincome"), ideal_equity_percentage, ideal_debt_percentage,
                additional_li_required, additional_hi_required, existing_equity_amount, equity_percentage, debt_percentage, total, payload.get("name[first]")+" "+payload.get("name[last]"));

        getInsuranceChart(total_li_required, lis_amount, additional_li_required, ideal_hi_amount, his_amount, additional_hi_required, fileName);

        Map<String, ArrayList<String>> loanPayload = getLoanEmiData(payload);
        getNetworth(loanPayload.get(LOAN_EMI_TYPE), loanPayload.get(LOAN_EMI_AMOUNT),
                existing_equity_amount, equity_percentage, existing_debt_amount, debt_percentage, total);
    }

    public void generatePdf(Map<String, String> payload){

        System.out.println("service payload: " + payload);

        getFormattedData(payload);
        Map<String,Object> koshantra_data = getAllData(payload);

        String fileName = payload.get("name[first]")+"_"+payload.get("name[last]");
        String cash_flow_chart = "src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg";
        String pie_chart_1 = "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg";
        String pie_chart_2 = "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg";
        String life_insurance_chart = "src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg";
        String health_insurance_chart = "src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg";

        try {
            File outputFile = pdfGeneratorUtil.createPdf(payload.get("name[first]")+ "_" + payload.get("name[last]") + "_report",this.data, "Page1_Cover","Page2","Page2.1_Index.html","Page3_PI","Page4_CashFlow", "Page5_CashFlowGraph",
                    /*"Page7_Investment"*//*, "Page8_Assumptions",*/"Page9_FinancialGoals","Page10_AssetAllocationChart","your_networth", "Page11_ActionPlan",
                    "Page12_ActionPlanChart", "Page14_Disclaimer");

            File file = pdfGeneratorUtil.createPdf(payload.get("name[first]")+ "_" + payload.get("name[last]") + "_customer_all_data", koshantra_data,
                    "clients_all_data");

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
        ZoneId zoneId = ZoneId.of(ZONE);
        LocalDate now = LocalDate.now(zoneId);
        now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String dateOfpurchase = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println(sdf2.format(sdf.parse(date_of_purchase)));
            dateOfpurchase = sdf2.format(sdf.parse(date_of_purchase));
        }catch (Exception e){

        }

        LocalDate started = LocalDate.parse(dateOfpurchase);

        if(freq.equals(DAILY)){
            amount = Duration.between(started.atStartOfDay(), now.atStartOfDay()).toDays() * amount;
        }else if(freq.equals(MONTHLY)){
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
