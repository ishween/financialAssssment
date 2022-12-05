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

@Component
public class PdfService {

    @Autowired private PdfGeneratorUtil pdfGeneratorUtil;

    private Map<String, Object> data;

    public PdfService() {
        data = new HashMap<>();
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
//        ArrayList<String> dependents_relationship = (ArrayList<String>) payload.get("dependents_relation");
//        ArrayList<String> dependents_dob = (ArrayList<String>) payload.get("dependents_dob");
//        ArrayList<String> dependents_name = (ArrayList<String>) payload.get("dependents_name");

        JSONArray dependents = new JSONArray(payload.get("dependents"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> dependents_name = new ArrayList<>();
        ArrayList<String> dependents_dob = new ArrayList<>();
        ArrayList<String> dependents_relationship = new ArrayList<>();
        System.out.println("Dependents FETCH");
        for(int i=0; i<dependents.length(); i++){
            System.out.println(dependents.get(i));
            JSONObject object = new JSONObject(dependents.get(i).toString());
            System.out.println("Dependents: i: " + i + " Name: " + object.getString("Name") + " Date of Birth : " + object.getString("Date of Birth")
                    + " Relationship: " + object.getString("Relationship"));
            dependents_name.add(object.getString("Name"));

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println(sdf2.format(sdf.parse(object.getString("Date of Birth"))));
                dependents_dob.add(sdf2.format(sdf.parse(object.getString("Date of Birth"))));
            } catch (Exception ex){

            }
//            dependents_dob.add(object.getString("Date of Birth"));
            dependents_relationship.add(object.getString("Relationship"));
        }

        for(int i = 0; i<dependents_name.size(); i++){
            dependentsData.add(Map.of("Name", dependents_name.get(i),
                    "DOB", dependents_dob.get(i),
                    "Relation", dependents_relationship.get(i)));
        }
        koshantra_data.put("dependentsColumns", dependentsColumns);
        koshantra_data.put("dependentsData", dependentsData);
        koshantra_data.put("number_of_dependents", dependents_dob.size());


        List<String> stocksColumns = Arrays.asList("Name", "Amount", "Market Value", "Date of Purchase", "Frequency");
        List<String> stocksColumnsAllData = Arrays.asList("Name", "Amount", "Original Amount", "Market Value", "Date of Purchase", "Frequency");
        List<Map<String,Object>> stocksData = new ArrayList<>();

//        ArrayList<String> stock_amount = (ArrayList<String>) payload.get("stocks_amount");
//        ArrayList<String> stocks_market_value = (ArrayList<String>) payload.get("stocks_market_value");
//        ArrayList<String> stocks_date_of_purchase = (ArrayList<String>) payload.get("stocks_date_of_purchase");
//        ArrayList<String> stocks_frequency = (ArrayList<String>) payload.get("stocks_frequency");

        JSONArray stocks = new JSONArray(payload.get("stocks"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> stocksAmount = new ArrayList<>();
        ArrayList<String> stocksMktValue = new ArrayList<>();
        ArrayList<String> stocksDOI = new ArrayList<>();
        ArrayList<String> stocksFreq = new ArrayList<>();

        System.out.println("STOCKS FETCH");
        for(int i=0; i<stocks.length(); i++){
            System.out.println(stocks.get(i));
            JSONObject object = new JSONObject(stocks.get(i).toString());
            System.out.println("Stock: i: " + i + " Amount: " + object.getString("Amount") + " Mkt value: " +
                    object.getString("Market Value") + " DOI: " + object.getString("Date of Investment") + " Freq: " + object.getString("Frequency"));
            stocksAmount.add(object.getString("Amount"));
            stocksMktValue.add(object.getString("Market Value"));
            stocksDOI.add(object.getString("Date of Investment"));
            stocksFreq.add(object.getString("Frequency"));
        }


        Long stock_full_amount = 0L, mfs_full_amount = 0L, ppfs_full_amount = 0L;
        for(int i=0;i<stocksAmount.size(); i++){
            long amount = getAmountFrom(stocksFreq.get(i), stocksAmount.get(i), stocksDOI.get(i));
            stock_full_amount += amount;
            stocksData.add(Map.of(
                    "Amount", amount,
                    "Original Amount", stocksAmount.get(i),
                    "Market Value", stocksMktValue.get(i),
                    "Date of Purchase", stocksDOI.get(i),
                    "Frequency", stocksColumns.get(i)));
        }
        koshantra_data.put("stocksColumns", stocksColumns);
        koshantra_data.put("stocksData", stocksData);
        koshantra_data.put("stocksColumnsAllData", stocksColumnsAllData);

        List<String> mfsColumns = Arrays.asList("Market Value", "Amount", "Type", "Date of Purchase", "Frequency");
        List<String> mfsColumnsAllData = Arrays.asList("Market Value", "Amount", "Original Amount", "Type", "Date of Purchase", "Frequency");
        List<Map<String,Object>> mfsData = new ArrayList<>();
//        ArrayList<String> mfs_mkt_value = (ArrayList<String>) payload.get("mf_mkt_value");
//        ArrayList<String> mf_amount = (ArrayList<String>) payload.get("mf_amount");
//        ArrayList<String> mfs_type = (ArrayList<String>) payload.get("mf_type");
//        ArrayList<String> mfs_date_of_purchase = (ArrayList<String>) payload.get("mf_date_of_purchase");
//        ArrayList<String> mfs_frequency = (ArrayList<String>) payload.get("mf_frequency");

        JSONArray mfs = new JSONArray(payload.get("mutualfunds"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> mfsAmount = new ArrayList<>();
        ArrayList<String> mfsMktValue = new ArrayList<>();
        ArrayList<String> mfsType = new ArrayList<>();
        ArrayList<String> mfsDOI = new ArrayList<>();
        ArrayList<String> mfsFreq = new ArrayList<>();

        System.out.println("MF FETCH");
        for(int i=0; i<mfs.length(); i++){
            System.out.println(mfs.get(i));
            JSONObject object = new JSONObject(mfs.get(i).toString());
            System.out.println("MF: i: " + i + " Amount: " + object.getString("Amount") + " DOI: " + object.getString("Date of Investment")
                    + " Freq: " + object.getString("Frequency") + " Mrkt Value: " + object.getString("Market Value") + " Type: " + object.getString("Type"));
            mfsAmount.add(object.getString("Amount"));
            mfsDOI.add(object.getString("Date of Investment"));
            mfsFreq.add(object.getString("Frequency"));
            mfsMktValue.add(object.getString("Market Value"));
            mfsType.add(object.getString("Type"));
        }

        for(int i=0;i<mfsFreq.size(); i++){
            long amount = getAmountFrom(mfsFreq.get(i), mfsAmount.get(i), mfsDOI.get(i));
            mfs_full_amount += amount;
            mfsData.add(Map.of("Type", mfsType.get(i),
                    "Amount", amount,
                    "Original Amount", mfsAmount.get(i),
                    "Market Value", mfsMktValue.get(i),
                    "Date of Purchase", mfsDOI.get(i),
                    "Frequency", mfsFreq.get(i)));
        }
        koshantra_data.put("mfsColumns", mfsColumns);
        koshantra_data.put("mfsData", mfsData);
        koshantra_data.put("mfsColumnsAllData", mfsColumnsAllData);

        List<String> lisColumns = Arrays.asList("Start Date", "Premium Paying Term", "Type", "Sum Assured", "Name", "Policy Term");
        List<Map<String,Object>> lisData = new ArrayList<>();
//        ArrayList<String> lis_start_date = (ArrayList<String>) payload.get("li_start_date");
//        ArrayList<String> li_premium_paying_term = (ArrayList<String>) payload.get("li_premium_paying_term");
//        ArrayList<String> li_type = (ArrayList<String>) payload.get("li_type");
//        ArrayList<String> li_sum_assured = (ArrayList<String>) payload.get("li_sum_assured");
//        ArrayList<String> li_name = (ArrayList<String>) payload.get("li_name");
//        ArrayList<String> li_policy_term = (ArrayList<String>) payload.get("li_policy_term");

        JSONArray lifeinsurance = new JSONArray(payload.get("lifeinsurance"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> lisSumInsured = new ArrayList<>();
        ArrayList<String> liInsuranceName = new ArrayList<>();
        ArrayList<String> lisType = new ArrayList<>();
        ArrayList<String> liPolicyTerm = new ArrayList<>();
        ArrayList<String> liPremiumAmount = new ArrayList<>();
        ArrayList<String> liPremiumPayingTerm = new ArrayList<>();
        ArrayList<String> liStartDate = new ArrayList<>();

//         *typea20*=[[{"Type":"Term","Insurance Name":"li","Policy Term":"12","Premium Amount":"12","Premium Paying Term":"12","Sum Insured":"12","Start Date":"12-04-2022"}]],

        System.out.println("LI FETCH");
        for(int i=0; i<lifeinsurance.length(); i++){
            System.out.println(lifeinsurance.get(i));
            JSONObject object = new JSONObject(lifeinsurance.get(i).toString());
            System.out.println("LI: i: " + i + " Sum Insured: " + object.getString("Sum Insured") + " Insurance Name: " + object.getString("Insurance Name")
                    + " Policy Term: " + object.getString("Policy Term") + " Premium Amount: " + object.getString("Premium Amount")
                    + " Type: " + object.getString("Type") + " Premium Paying Term: " + object.getString("Premium Paying Term")
                    + " Start Date: " + object.getString("Start Date"));
            lisSumInsured.add(object.getString("Sum Insured"));
            liInsuranceName.add(object.getString("Insurance Name"));
            liPolicyTerm.add(object.getString("Policy Term"));
            liPremiumAmount.add(object.getString("Premium Amount"));
            lisType.add(object.getString("Type"));
            liPremiumPayingTerm.add(object.getString("Premium Paying Term"));
            liStartDate.add(object.getString("Start Date"));
        }

        JSONArray healthinsurance = new JSONArray(payload.get("healthinsurance"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> hisSumInsured = new ArrayList<>();
        ArrayList<String> hisInsuranceName = new ArrayList<>();
        ArrayList<String> hisFrequencyType = new ArrayList<>();
        ArrayList<String> hisPremiumAmount = new ArrayList<>();
        ArrayList<String> hiStartDate = new ArrayList<>();

//         *typea20*=[[{"Type":"Term","Insurance Name":"li","Policy Term":"12","Premium Amount":"12","Premium Paying Term":"12","Sum Insured":"12","Start Date":"12-04-2022"}]],

        System.out.println("HI FETCH");
        for(int i=0; i<healthinsurance.length(); i++){
            System.out.println(healthinsurance.get(i));
            JSONObject object = new JSONObject(healthinsurance.get(i).toString());
            System.out.println("HI: i: " + i + " Sum Insured: " + object.getString("Sum Insured") + " Company Name : " + object.getString("Company Name")
                    + " Premium Amount: " + object.getString("Premium Amount")
                    + " Frequency Type: " + object.getString("Frequency Type") + " Start Date: " + object.getString("Start Date"));
            hisSumInsured.add(object.getString("Sum Insured"));
            hisInsuranceName.add(object.getString("Company Name"));
            hisPremiumAmount.add(object.getString("Premium Amount"));
            hisFrequencyType.add(object.getString("Frequency Type"));
            hiStartDate.add(object.getString("Start Date"));
        }


        for(int i=0;i<liInsuranceName.size(); i++){
            lisData.add(Map.of("Name", liInsuranceName.get(i),
                    "Start Date", liStartDate.get(i),
                    "Premium Paying Term", liPremiumPayingTerm.get(i),
                    "Sum Assured", lisSumInsured.get(i),
                    "Type", lisType.get(i),
                    "Policy Term", liPolicyTerm.get(i),
                    "Premium Amount", liPremiumAmount.get(i)));
        }
        koshantra_data.put("lisColumns", lisColumns);
        koshantra_data.put("lisData", lisData);

        List<String> hisColumns = Arrays.asList("Name", "Premium Amount", "Type", "Sum Insured", "Start Date");
        List<Map<String,Object>> hisData = new ArrayList<>();
//        ArrayList<String> hi_premium_amount = (ArrayList<String>) payload.get("hi_premium_amount");
//        ArrayList<String> hi_name = (ArrayList<String>) payload.get("hi_name");
//        ArrayList<String> hi_type = (ArrayList<String>) payload.get("hi_type");
//        ArrayList<String> hi_start_date = (ArrayList<String>) payload.get("hi_start_date");
//        ArrayList<String> hi_sum_insured = (ArrayList<String>) payload.get("hi_sum_insured");
        for(int i=0;i<hiStartDate.size(); i++){
            hisData.add(Map.of("Name", hisInsuranceName.get(i),
                    "Premium Amount", hisPremiumAmount.get(i),
                    "Frequency Type", hisFrequencyType.get(i),
                    "Sum Insured", hisSumInsured.get(i),
                    "Start Date", hiStartDate.get(i)));
        }
        koshantra_data.put("hisColumns", hisColumns);
        koshantra_data.put("hisData", hisData);

        List<String> bondsColumns = Arrays.asList("Name", "Invested", "Maturity Date", "Date of Investment");
        List<Map<String,Object>> bondsData = new ArrayList<>();
//        ArrayList<String> bonds_name = (ArrayList<String>) payload.get("bonds_name");
//        ArrayList<String> bond_invested = (ArrayList<String>) payload.get("bonds_invested");
//        ArrayList<String> bonds_date_of_investment = (ArrayList<String>) payload.get("bonds_date_of_investment");
//        ArrayList<String> bonds_maturity_date = (ArrayList<String>) payload.get("bonds_maturity_date");

        JSONArray bonds = new JSONArray(payload.get("bonds"));
        ArrayList<String> bond_invested = new ArrayList<>();
        ArrayList<String> bonds_name = new ArrayList<>();
        ArrayList<String> bonds_date_of_investment = new ArrayList<>();
        ArrayList<String> bonds_maturity_date = new ArrayList<>();

        System.out.println("bonds FETCH");
        for(int i=0; i<bonds.length(); i++){
            System.out.println(bonds.get(i));
            JSONObject object = new JSONObject(bonds.get(i).toString());
            System.out.println("bonds: i: " + i + " Name: " + object.getString("Name") + " Amount: " +
                    object.getString("Amount") + " DOI: " + object.getString("Date of Investment") + " Maturity Date: " + object.getString("Maturity Date"));
            bond_invested.add(object.getString("Amount"));
            bonds_name.add(object.getString("Name"));
            bonds_date_of_investment.add(object.getString("Date of Investment"));
            bonds_maturity_date.add(object.getString("Maturity Date"));
        }

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
//        ArrayList<String> ppf_invested = (ArrayList<String>) payload.get("ppf_invested");
//        ArrayList<String> ppf_date_of_investment = (ArrayList<String>) payload.get("ppf_date_of_investment");
//        ArrayList<String> ppf_frequency = (ArrayList<String>) payload.get("ppf_frequency");

        JSONArray ppfs = new JSONArray(payload.get("ppf"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> ppfsInvested = new ArrayList<>();
        ArrayList<String> ppfsDOI = new ArrayList<>();
        ArrayList<String> ppfsFreq = new ArrayList<>();

        System.out.println("PPF FETCH");
        for(int i=0; i<ppfs.length(); i++){
            System.out.println(ppfs.get(i));
            JSONObject object = new JSONObject(ppfs.get(i).toString());
            System.out.println("PPF: i: " + i + " Amount: " + object.getString("Amount") + " DOI: " + object.getString("Date of Investment")
                    + " Freq: " + object.getString("Frequency"));
            ppfsInvested.add(object.getString("Amount"));
            ppfsDOI.add(object.getString("Date of Investment"));
            ppfsFreq.add(object.getString("Frequency"));
        }

        for(int i=0;i<ppfsInvested.size(); i++){
            long amount = getAmountFrom(ppfsFreq.get(i), ppfsInvested.get(i), ppfsDOI.get(i));
            ppfs_full_amount += amount;
            ppfsData.add(Map.of("Invested", amount,
                    "Original Amount", ppfsInvested.get(i),
                    "Date of Investment", ppfsDOI.get(i),
                    "Frequency", ppfsFreq.get(i)));
        }
        koshantra_data.put("ppfsColumns", ppfsColumns);
        koshantra_data.put("ppfsData", ppfsData);
        koshantra_data.put("ppfsColumnsAllData", ppfsColumnsAllData);

        List<String> fdsColumns = Arrays.asList("Name", "Amount", "Start Date", "Maturity Date");
        List<Map<String,Object>> fdsData = new ArrayList<>();
//        ArrayList<String> fd_start_date = (ArrayList<String>) payload.get("fd_start_date");
//        ArrayList<String> fd_name = (ArrayList<String>) payload.get("fd_name");
//        ArrayList<String> fd_maturity_date = (ArrayList<String>) payload.get("fd_maturity_date");
//        ArrayList<String> fd_amount = (ArrayList<String>) payload.get("fd_amount");

        JSONArray fds = new JSONArray(payload.get("fixeddeposits"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> fdsName = new ArrayList<>();
        ArrayList<String> fdsAmountInvested = new ArrayList<>();
        ArrayList<String> fdsInterestRate = new ArrayList<>();
        ArrayList<String> fdsStartDate = new ArrayList<>();
        ArrayList<String> fdsMaturityDate = new ArrayList<>();

        System.out.println("fds FETCH");
        for(int i=0; i<fds.length(); i++){
            System.out.println(fds.get(i));
            JSONObject object = new JSONObject(fds.get(i).toString());
            System.out.println("fds: i: " + i + " Bank/NBFC Name: " + object.getString("Bank/NBFC Name") + " Amount Invested: " +
                    object.getString("Amount Invested") + " Interest Rate: " + object.getString("Interest Rate")
                    + " Start Date: " + object.getString("Start Date") + " Maturity Date: " + object.getString("Maturity Date"));
            fdsName.add(object.getString("Bank/NBFC Name"));
            fdsAmountInvested.add(object.getString("Amount Invested"));
            fdsStartDate.add(object.getString("Start Date"));
            fdsInterestRate.add(object.getString("Interest Rate"));
            fdsMaturityDate.add(object.getString("Maturity Date"));

        }

        for(int i=0;i<fdsName.size(); i++){
            fdsData.add(Map.of("Name", fdsName.get(i),
                    "Amount", fdsAmountInvested.get(i),
                    "Start Date", fdsStartDate.get(i),
                    "Maturity Date", fdsMaturityDate.get(i),
                    "Interest Rate", fdsInterestRate.get(i)));
        }
        koshantra_data.put("fdsColumns", fdsColumns);
        koshantra_data.put("fdsData", fdsData);

        List<String> loanoremisColumns = Arrays.asList("Start Date", "Installment Amount", "Installment Left", "Type");
        List<Map<String,Object>> loanoremisData = new ArrayList<>();
//        ArrayList<String> loan_emi_start_date = (ArrayList<String>) payload.get("loan_emi_start_date");
//        ArrayList<String> loan_emi_installment_amount = (ArrayList<String>) payload.get("loan_emi_installment_amount");
//        ArrayList<String> loan_emi_installement_left = (ArrayList<String>) payload.get("loan_emi_installement_left");
//        ArrayList<String> loan_emi_type = (ArrayList<String>) payload.get("loan_emi_type");
        JSONArray loanemi = new JSONArray(payload.get("loanemi"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> loanAmount = new ArrayList<>();
        ArrayList<String> loanNoInstallments = new ArrayList<>();
        ArrayList<String> loanAmountInstallment = new ArrayList<>();
        ArrayList<String> loanType = new ArrayList<>();
        ArrayList<String> loanStartDate = new ArrayList<>();

//         *typea20*=[[{"Type":"Term","Insurance Name":"li","Policy Term":"12","Premium Amount":"12","Premium Paying Term":"12","Sum Insured":"12","Start Date":"12-04-2022"}]],

        System.out.println("loanemi FETCH");
        for(int i=0; i<loanemi.length(); i++){
            System.out.println(loanemi.get(i));
            JSONObject object = new JSONObject(loanemi.get(i).toString());
            System.out.println("Loan: i: " + i + " Loan Amount: " + object.getString("Loan Amount") + " No of Installments Left : " + object.getString("No of Installments Left")
                    + " Amount of Installment:: " + object.getString("Amount of Installment")
                    + " Type: " + object.getString("Type") + " Start Date: " + object.getString("Start Date"));
            loanAmount.add(object.getString("Loan Amount"));
            loanNoInstallments.add(object.getString("No of Installments Left"));
            loanAmountInstallment.add(object.getString("Amount of Installment"));
            loanType.add(object.getString("Type"));
            loanStartDate.add(object.getString("Start Date"));
        }
        for(int i=0;i<loanAmountInstallment.size(); i++){
            loanoremisData.add(Map.of("Start Date", loanStartDate.get(i),
                    "Installment Amount", loanAmountInstallment.get(i),
                    "Installment Left", loanNoInstallments.get(i),
                    "Type", loanType.get(i)));
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
//            String[] life_goals_list = lifeGoals.split(",");
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
//        Map<String,Object> data = new HashMap<>();

        data.put("first_name", payload.get("name[first]"));
        data.put("last_name", payload.get("name[last]"));


        JSONArray dependents = new JSONArray(payload.get("dependents"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> dependentsName = new ArrayList<>();
        ArrayList<String> dependentsDOB = new ArrayList<>();
        ArrayList<String> dependentsRelation = new ArrayList<>();
        System.out.println("Dependents FETCH");
        for(int i=0; i<dependents.length(); i++){
            System.out.println(dependents.get(i));
            JSONObject object = new JSONObject(dependents.get(i).toString());
            System.out.println("Dependents: i: " + i + " Name: " + object.getString("Name") + " Date of Birth : " + object.getString("Date of Birth")
                    + " Relationship: " + object.getString("Relationship"));
            dependentsName.add(object.getString("Name"));
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println(sdf2.format(sdf.parse(object.getString("Date of Birth"))));
                dependentsDOB.add(sdf2.format(sdf.parse(object.getString("Date of Birth"))));
            }catch (Exception ex){

            }
            dependentsRelation.add(object.getString("Relationship"));
        }

        //PI
        data.put("logo", "src/main/resources/templates/images/Wealth_Creation_Logo.png");
        data.put("client_name", payload.get("name[first]")+" "+payload.get("name[last]"));

        String fileName = payload.get("name[first]")+"_"+payload.get("name[last]");
        data.put("ID",fileName);

        DecimalFormat df = new DecimalFormat("0.00"); /*df.format(futureCost)*/


        String dob = payload.get("dateof[year]") + "-" + payload.get("dateof[month]") + "-" + payload.get("dateof[day]");
//        String dateOfpurchase = "";
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//            System.out.println(sdf2.format(sdf.parse(date_of_purchase)));
//            dateOfpurchase = sdf2.format(sdf.parse(date_of_purchase));
//        }catch (Exception e){
//
//        }
        getPersonalInformation(payload.get("name[first]")+" "+payload.get("name[last]"), dob, payload.get("profession"),
                payload.get("phonenumber[full]"), dependentsName, dependentsRelation, dependentsDOB);

//        List<String> personalInfoColumns = Arrays.asList("Relationship", "Name", "DOB", "Occupation", "Mobile No.");
//        List<Map<String,Object>> personalInfoData = new ArrayList<>();
//        personalInfoData.add(Map.of("Relationship", "Self", "Name", payload.get("first_name")+" "+payload.get("last_name"), "DOB", payload.get("dob"), "Occupation", payload.get("profession"), "Mobile No.", payload.get("mobile_no")));
//        ArrayList<String> dependents_relationship = (ArrayList<String>) payload.get("dependents_relation");
//        ArrayList<String> dependents_dob = (ArrayList<String>) payload.get("dependents_dob");
//        ArrayList<String> dependents_name = (ArrayList<String>) payload.get("dependents_name");
//
//        for(int i = 0; i<dependents_name.size(); i++){
//            personalInfoData.add(Map.of("Relationship", dependents_relationship.get(i),
//                    "Name", dependents_name.get(i),
//                    "DOB", dependents_dob.get(i)));
//        }
//
//        data.put("personalInfoColumns", personalInfoColumns);
//        data.put("personalInfoData", personalInfoData);

        getIncomeInfo(payload.get("annualincome"), (String) payload.get("profession"), df);
//        DecimalFormat df = new DecimalFormat("0.00"); /*df.format(futureCost)*/

        //Page4
//        List<String> incomeColumns = Arrays.asList("Income", "Monthly(Rs.)", "Annually(Rs.)");
//        List<Map<String,Object>> incomeData = new ArrayList<>();
//        String annual_income = (String) payload.get("income");
//        String annual_expense = (String) payload.get("expense");
//        if(payload.get("profession").equals("Self Employed")){
//            incomeData.add(Map.of("Income", "Business Income", "Monthly(Rs.)", df.format(Long.parseLong(annual_income)/12.0), "Annually(Rs.)", annual_income));
//            incomeData.add(Map.of("Income", "Income Salary", "Monthly(Rs.)", "0", "Annually(Rs.)", "0"));
//        }else{
//            incomeData.add(Map.of("Income", "Business Income", "Monthly(Rs.)", "0", "Annually(Rs.)", "0"));
//            incomeData.add(Map.of("Income", "Income Salary", "Monthly(Rs.)", df.format(Long.parseLong(annual_income)/12.0), "Annually(Rs.)", annual_income));
//        }
//        data.put("incomeColumns", incomeColumns);
//        data.put("incomeData", incomeData);

        getCashFlow((String) payload.get("annualincome"), (String) payload.get("annualexpense"), fileName);

//        List<Map<String, Object>> incomeTotal = new ArrayList<>();
//        incomeTotal.add(Map.of("Income", "Total", "Monthly(Rs.)", df.format(Long.parseLong(annual_income)/12.0), "Annually(Rs.)", annual_income));
//
//        data.put("incomeTotal", incomeTotal);
//
//        //Page5
//        try {
//            List<String> xAxis = Arrays.asList("Income", "Expenses");
//            List<String> values = Arrays.asList(annual_income, annual_expense);
//            createBarChart(xAxis, values, "", "INR (₹)", "","src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg");
//            data.put("cash_flow_chart", "src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg");
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        getFinancialGoals((String) payload.get("lifegoals"), dob, payload.get("name[first]")+" "+payload.get("name[last]"),
                (String) payload.get("annualexpense"), dependentsName, dependentsRelation, dependentsDOB);

        //Page9
//        List<String> financialGoalsColumns = Arrays.asList("Goal Name", "Years to Goal", "Present Value", "Growth Rate(%)", "Future Cost(Rs.)");
//        List<Map<String,Object>> financialGoalsData = new ArrayList<>();
//        String life_goals = (String) payload.get("life_goals");
//        if(life_goals != null) {
//            String[] life_goals_list = life_goals.split(",");
//            for (String life_goal : life_goals_list) {
//                if (life_goal.equals("Child Education")) {
//                    //child dependents
//                    for (int i = 0; i < dependents_relationship.size(); i++) {
//                        if (dependents_relationship.get(i).equals("Child")) {
//                            LocalDate curDate = LocalDate.now();
//                            int age = Period.between(LocalDate.parse(dependents_dob.get(i)), curDate).getYears();
//                            if (age < 18) {
//                                double rate = 0.08;
//                                int present_value = 700000;
//                                double futureCost = (present_value) * Math.pow(1 + rate, (18-age));
//
//                                financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Graduation",
//                                        "Years to Goal", (18 - age),
//                                        "Present Value", present_value,
//                                        "Growth Rate(%)", (rate*100),
//                                        "Future Cost(Rs.)", Math.round(futureCost)));
//                            }
//                            if (age < 21) {
//                                double rate = 0.08;
//                                int present_value = 2000000;
//                                double futureCost = (present_value) * Math.pow(1 + rate, (21 - age));
//                                financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Higher Studies",
//                                        "Years to Goal", (21 - age),
//                                        "Present Value", present_value,
//                                        "Growth Rate(%)", (rate*100),
//                                        "Future Cost(Rs.)", Math.round(futureCost)));
//                            }
//                        }
//                    }
//                }
//                if (life_goal.equals("Self Marriage")) {
//                    LocalDate curDate = LocalDate.now();
//                    int age = Period.between(LocalDate.parse((String) payload.get("dob")), curDate).getYears(), present_value = 2000000;
//                    double rate = 0.06;
//                    double futureCost = (present_value) * Math.pow(1 + rate, (25 - age));
//                    financialGoalsData.add(Map.of("Goal Name", payload.get("first_name") + " " + payload.get("last_name") + "-Marriage",
//                            "Years to Goal", (25 - age),
//                            "Present Value", present_value,
//                            "Growth Rate(%)", (rate*100),
//                            "Future Cost(Rs.)", Math.round(futureCost)));
//                }
//                if (life_goal.equals("Retirement")) {
//                    LocalDate curDate = LocalDate.now();
//                    int age = Period.between(LocalDate.parse((String) payload.get("dob")), curDate).getYears();
//                    double rate = 0.06;
//                    double futureCost = (Long.parseLong(annual_expense)) * Math.pow(1 + rate, (58 - age));
//                    financialGoalsData.add(Map.of("Goal Name", payload.get("first_name") + " " + payload.get("last_name") + "-Retirement",
//                            "Years to Goal", (58 - age),
//                            "Present Value", annual_expense,
//                            "Growth Rate(%)", (rate*100),
//                            "Future Cost(Rs.)", Math.round(futureCost)));
//                }
//                if (life_goal.equals("Child Marriage")) {
//                    for (int i = 0; i < dependents_relationship.size(); i++) {
//                        if (dependents_relationship.get(i).equals("Child")) {
//                            LocalDate curDate = LocalDate.now();
//                            int age = Period.between(LocalDate.parse(dependents_dob.get(i)), curDate).getYears();
//                            double rate = 0.06;
//                            int present_value = 2000000;
//                            double futureCost = (present_value) * Math.pow(1 + rate, (25 - age));
//                            financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Marriage",
//                                    "Years to Goal", (25 - age),
//                                    "Present Value", present_value,
//                                    "Growth Rate(%)", (rate*100),
//                                    "Future Cost(Rs.)", Math.round(futureCost)));
//                        }
//                    }
//                }
//            }
//        }else{
//            System.out.println("LIFE GOALS IS COMING AS NULL");
//        }
//        data.put("financialGoalsColumns", financialGoalsColumns);
//        data.put("financialGoalsData", financialGoalsData);

        JSONArray bonds = new JSONArray(payload.get("bonds"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> bondsAmount = new ArrayList<>();
        ArrayList<String> bondsName = new ArrayList<>();
        ArrayList<String> bondsDOI = new ArrayList<>();
        ArrayList<String> bondsMaturityDate = new ArrayList<>();

        System.out.println("bonds FETCH");
        for(int i=0; i<bonds.length(); i++){
            System.out.println(bonds.get(i));
            JSONObject object = new JSONObject(bonds.get(i).toString());
            System.out.println("bonds: i: " + i + " Name: " + object.getString("Name") + " Amount: " +
                    object.getString("Amount") + " DOI: " + object.getString("Date of Investment") + " Maturity Date: " + object.getString("Maturity Date"));
            bondsAmount.add(object.getString("Amount"));
            bondsName.add(object.getString("Name"));
            bondsDOI.add(object.getString("Date of Investment"));
            bondsMaturityDate.add(object.getString("Maturity Date"));
        }

        Long bondsFullAmount = getTotalBondsAmount(bondsAmount);

        JSONArray fds = new JSONArray(payload.get("fixeddeposits"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> fdsName = new ArrayList<>();
        ArrayList<String> fdsAmountInvested = new ArrayList<>();
        ArrayList<String> fdsInterestRate = new ArrayList<>();
        ArrayList<String> fdsStartDate = new ArrayList<>();
        ArrayList<String> fdsMaturityDate = new ArrayList<>();

        System.out.println("fds FETCH");
        for(int i=0; i<fds.length(); i++){
            System.out.println(fds.get(i));
            JSONObject object = new JSONObject(fds.get(i).toString());
            System.out.println("fds: i: " + i + " Bank/NBFC Name: " + object.getString("Bank/NBFC Name") + " Amount Invested: " +
                    object.getString("Amount Invested") + " Interest Rate: " + object.getString("Interest Rate")
                    + " Start Date: " + object.getString("Start Date") + " Maturity Date: " + object.getString("Maturity Date"));
            fdsName.add(object.getString("Bank/NBFC Name"));
            fdsAmountInvested.add(object.getString("Amount Invested"));
            fdsStartDate.add(object.getString("Start Date"));
            fdsInterestRate.add(object.getString("Interest Rate"));
            fdsMaturityDate.add(object.getString("Maturity Date"));

        }
        Long fdFullAmount = getTotalFDsAmount(fdsAmountInvested);



//        ArrayList<String> bonds_invested = (ArrayList<String>) payload.get("bonds_invested");
//        ArrayList<String> fds_amount = (ArrayList<String>) payload.get("fd_amount");

//        Long total_bonds_amount = bonds_invested == null || bonds_invested.isEmpty()? 0L : bonds_invested.stream().map(Long::parseLong).reduce(0L, Long::sum);
//        Long total_fds_amount = fds_amount == null || fds_amount.isEmpty()? 0L : fds_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);

//        String str = payload.get("stocks");
        JSONArray stocks = new JSONArray(payload.get("stocks"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> stocksAmount = new ArrayList<>();
        ArrayList<String> stocksMktValue = new ArrayList<>();
        ArrayList<String> stocksDOI = new ArrayList<>();
        ArrayList<String> stocksFreq = new ArrayList<>();

        System.out.println("STOCKS FETCH");
        for(int i=0; i<stocks.length(); i++){
            System.out.println(stocks.get(i));
            JSONObject object = new JSONObject(stocks.get(i).toString());
            System.out.println("Stock: i: " + i + " Amount: " + object.getString("Amount") + " Mkt value: " +
                    object.getString("Market Value") + " DOI: " + object.getString("Date of Investment") + " Freq: " + object.getString("Frequency"));
            stocksAmount.add(object.getString("Amount"));
            stocksMktValue.add(object.getString("Market Value"));
            stocksDOI.add(object.getString("Date of Investment"));
            stocksFreq.add(object.getString("Frequency"));
        }
//        System.out.println(array);
//        System.out.println(array.get(i));
//        Long stockFullAmount = getStocksInfo((ArrayList<String>) payload.get("stocks_amount"), (ArrayList<String>) payload.get("stocks_market_value"),
//                (ArrayList<String>) payload.get("stocks_date_of_purchase"), (ArrayList<String>) payload.get("stocks_frequency"));

        Long stockFullAmount = getStocksInfo(stocksAmount, stocksMktValue,stocksDOI, stocksFreq);


//        ArrayList<String> stock_amount = (ArrayList<String>) payload.get("stocks_amount");
//        ArrayList<String> stocks_market_value = (ArrayList<String>) payload.get("stocks_market_value");
//        ArrayList<String> stocks_date_of_purchase = (ArrayList<String>) payload.get("stocks_date_of_purchase");
//        ArrayList<String> stocks_frequency = (ArrayList<String>) payload.get("stocks_frequency");
//        List<Map<String,Object>> stocksData = new ArrayList<>();
//
//        Long stock_full_amount = 0L, mfs_full_amount = 0L, ppfs_full_amount = 0L;
//        for(int i=0;i<stock_amount.size(); i++){
//            long amount = getAmountFrom(stocks_frequency.get(i), stock_amount.get(i), stocks_date_of_purchase.get(i));
//            stock_full_amount += amount;
//            stocksData.add(Map.of(
//                    "Amount", amount,
//                    "Original Amount", stock_amount.get(i),
//                    "Market Value", stocks_market_value.get(i),
//                    "Date of Purchase", stocks_date_of_purchase.get(i),
//                    "Frequency", stocks_frequency.get(i)));
//        }

        JSONArray ppfs = new JSONArray(payload.get("ppf"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> ppfsInvested = new ArrayList<>();
        ArrayList<String> ppfsDOI = new ArrayList<>();
        ArrayList<String> ppfsFreq = new ArrayList<>();

        System.out.println("PPF FETCH");
        for(int i=0; i<ppfs.length(); i++){
            System.out.println(ppfs.get(i));
            JSONObject object = new JSONObject(ppfs.get(i).toString());
            System.out.println("PPF: i: " + i + " Amount: " + object.getString("Amount") + " DOI: " + object.getString("Date of Investment")
                    + " Freq: " + object.getString("Frequency"));
            ppfsInvested.add(object.getString("Amount"));
            ppfsDOI.add(object.getString("Date of Investment"));
            ppfsFreq.add(object.getString("Frequency"));
        }
        Long ppfFullAmount = getPPFsInfo(ppfsInvested, ppfsDOI, ppfsFreq);


//        List<Map<String,Object>> ppfsData = new ArrayList<>();
//        ArrayList<String> ppf_invested = (ArrayList<String>) payload.get("ppf_invested");
//        ArrayList<String> ppf_date_of_investment = (ArrayList<String>) payload.get("ppf_date_of_investment");
//        ArrayList<String> ppf_frequency = (ArrayList<String>) payload.get("ppf_frequency");
//        for(int i=0;i<ppf_invested.size(); i++){
//            long amount = getAmountFrom(ppf_frequency.get(i), ppf_invested.get(i), ppf_date_of_investment.get(i));
//            ppfs_full_amount += amount;
//            ppfsData.add(Map.of("Invested", amount,
//                    "Original Amount", ppf_invested.get(i),
//                    "Date of Investment", ppf_date_of_investment.get(i),
//                    "Frequency", ppf_frequency.get(i)));
//        }


        JSONArray mfs = new JSONArray(payload.get("mutualfunds"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> mfsAmount = new ArrayList<>();
        ArrayList<String> mfsMktValue = new ArrayList<>();
        ArrayList<String> mfsType = new ArrayList<>();
        ArrayList<String> mfsDOI = new ArrayList<>();
        ArrayList<String> mfsFreq = new ArrayList<>();

        System.out.println("MF FETCH");
        for(int i=0; i<mfs.length(); i++){
            System.out.println(mfs.get(i));
            JSONObject object = new JSONObject(mfs.get(i).toString());
            System.out.println("MF: i: " + i + " Amount: " + object.getString("Amount") + " DOI: " + object.getString("Date of Investment")
                    + " Freq: " + object.getString("Frequency") + " Mrkt Value: " + object.getString("Market Value") + " Type: " + object.getString("Type"));
            mfsAmount.add(object.getString("Amount"));
            mfsDOI.add(object.getString("Date of Investment"));
            mfsFreq.add(object.getString("Frequency"));
            mfsMktValue.add(object.getString("Market Value"));
            mfsType.add(object.getString("Type"));
        }

        Long mfFullAmount = getMFsInfo(mfsAmount, mfsMktValue,mfsType, mfsDOI, mfsFreq);


//        List<Map<String,Object>> mfsData = new ArrayList<>();
//        ArrayList<String> mfs_mkt_value = (ArrayList<String>) payload.get("mf_mkt_value");
//        ArrayList<String> mf_amount = (ArrayList<String>) payload.get("mf_amount");
//        ArrayList<String> mfs_type = (ArrayList<String>) payload.get("mf_type");
//        ArrayList<String> mfs_date_of_purchase = (ArrayList<String>) payload.get("mf_date_of_purchase");
//        ArrayList<String> mfs_frequency = (ArrayList<String>) payload.get("mf_frequency");
//        for(int i=0;i<mfs_frequency.size(); i++){
//            long amount = getAmountFrom(mfs_frequency.get(i), mf_amount.get(i), mfs_date_of_purchase.get(i));
//            mfs_full_amount += amount;
//            mfsData.add(Map.of("Type", mfs_type.get(i),
//                    "Amount", amount,
//                    "Original Amount", mf_amount.get(i),
//                    "Market Value", mfs_mkt_value.get(i),
//                    "Date of Purchase", mfs_date_of_purchase.get(i),
//                    "Frequency", mfs_frequency.get(i)));
//        }


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

        //Page10
//        try {
//            List<String> items = Arrays.asList("Equity ("+equity_percentage+"%)", "Debt ("+debt_percentage+"%)");
//            List<Float> itemValues = Arrays.asList(equity_percentage, debt_percentage);
//            createPieChart(items, itemValues, "Current Asset Allocation", "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg");
//            data.put("pie_chart_1", "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg");
//
//            items = Arrays.asList("Equity ("+ideal_equity_percentage+"%)", "Debt ("+ideal_debt_percentage+"%)");
//            itemValues = Arrays.asList(ideal_equity_percentage, ideal_debt_percentage);
//            createPieChart(items, itemValues, "Recommended Asset Allocation", "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg");
//            data.put("pie_chart_2", "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg");
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        JSONArray lifeinsurance = new JSONArray(payload.get("lifeinsurance"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> lisSumInsured = new ArrayList<>();
        ArrayList<String> liInsuranceName = new ArrayList<>();
        ArrayList<String> lisType = new ArrayList<>();
        ArrayList<String> liPolicyTerm = new ArrayList<>();
        ArrayList<String> liPremiumAmount = new ArrayList<>();
        ArrayList<String> liPremiumPayingTerm = new ArrayList<>();
        ArrayList<String> liStartDate = new ArrayList<>();

//         *typea20*=[[{"Type":"Term","Insurance Name":"li","Policy Term":"12","Premium Amount":"12","Premium Paying Term":"12","Sum Insured":"12","Start Date":"12-04-2022"}]],

        System.out.println("LI FETCH");
        for(int i=0; i<lifeinsurance.length(); i++){
            System.out.println(lifeinsurance.get(i));
            JSONObject object = new JSONObject(lifeinsurance.get(i).toString());
            System.out.println("LI: i: " + i + " Sum Insured: " + object.getString("Sum Insured") + " Insurance Name: " + object.getString("Insurance Name")
                    + " Policy Term: " + object.getString("Policy Term") + " Premium Amount: " + object.getString("Premium Amount")
                    + " Type: " + object.getString("Type") + " Premium Paying Term: " + object.getString("Premium Paying Term")
                    + " Start Date: " + object.getString("Start Date"));
            lisSumInsured.add(object.getString("Sum Insured"));
            liInsuranceName.add(object.getString("Insurance Name"));
            liPolicyTerm.add(object.getString("Policy Term"));
            liPremiumAmount.add(object.getString("Premium Amount"));
            lisType.add(object.getString("Type"));
            liPremiumPayingTerm.add(object.getString("Premium Paying Term"));
            liStartDate.add(object.getString("Start Date"));
        }

        JSONArray healthinsurance = new JSONArray(payload.get("healthinsurance"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> hisSumInsured = new ArrayList<>();
        ArrayList<String> hisInsuranceName = new ArrayList<>();
        ArrayList<String> hisFrequencyType = new ArrayList<>();
        ArrayList<String> hisPremiumAmount = new ArrayList<>();
        ArrayList<String> hiStartDate = new ArrayList<>();

//         *typea20*=[[{"Type":"Term","Insurance Name":"li","Policy Term":"12","Premium Amount":"12","Premium Paying Term":"12","Sum Insured":"12","Start Date":"12-04-2022"}]],

        System.out.println("HI FETCH");
        for(int i=0; i<healthinsurance.length(); i++){
            System.out.println(healthinsurance.get(i));
            JSONObject object = new JSONObject(healthinsurance.get(i).toString());
            System.out.println("HI: i: " + i + " Sum Insured: " + object.getString("Sum Insured") + " Company Name : " + object.getString("Company Name")
                    + " Premium Amount: " + object.getString("Premium Amount")
                    + " Frequency Type: " + object.getString("Frequency Type") + " Start Date: " + object.getString("Start Date"));
            hisSumInsured.add(object.getString("Sum Insured"));
            hisInsuranceName.add(object.getString("Company Name"));
            hisPremiumAmount.add(object.getString("Premium Amount"));
            hisFrequencyType.add(object.getString("Frequency Type"));
            hiStartDate.add(object.getString("Start Date"));
        }


        Long lis_amount = (lisSumInsured).stream().map(Long::parseLong).reduce(0L, Long::sum);
        Long his_amount = (hisSumInsured).stream().map(Long::parseLong).reduce(0L, Long::sum);

        Long total_li_required = 20 * (Long.parseLong((String) payload.get("annualincome")));
        Long additional_li_required = (total_li_required) - lis_amount;
        Long ideal_hi_amount = 0L;
        if(age>=21 && age<30 && (!dependentsDOB.isEmpty())) ideal_hi_amount = 7_50_000L;
        else if(age>=21 && age<30 ) ideal_hi_amount = 5_00_000L;
        else if(age>=30 && age <40) ideal_hi_amount = 10_00_000L;
        else if (age>=40 && age<50) ideal_hi_amount = 15_00_000L;
        else if(age>=50) ideal_hi_amount = 20_00_000L;
        Long additional_hi_required = ideal_hi_amount - his_amount;

        getActionPlan((String) payload.get("annualexpense"), (String) payload.get("annualincome"), ideal_equity_percentage, ideal_debt_percentage,
                additional_li_required, additional_hi_required, existing_equity_amount, equity_percentage, debt_percentage, total, payload.get("name[first]")+" "+payload.get("name[last]"));


//        //Page11
//        Long emergency_fund = Long.parseLong(annual_expense)/2; //Formual 6 * monthly expense
//        long ideal_equity_amount = ((long)ideal_equity_percentage * (Long.parseLong(annual_income)/12))* 100;
//        long ideal_debt_amount = ((long)ideal_debt_percentage * (Long.parseLong(annual_income)/12))*100;
//        Long lis_amount = ((ArrayList<String>) payload.get("li_sum_assured")).stream().map(Long::parseLong).reduce(0L, Long::sum);
//        Long his_amount = ((ArrayList<String>) payload.get("hi_sum_insured")).stream().map(Long::parseLong).reduce(0L, Long::sum);
//
//        Long total_li_required = 20 * (Long.parseLong((String) payload.get("income")));
//        Long additional_li_required = (total_li_required) - lis_amount;
//        Long ideal_hi_amount = 0L;
//        if(age>=21 && age<30 && (!payload.get("number_of_dependents").toString().isEmpty() && Integer.parseInt((String) payload.get("number_of_dependents")) > 0)) ideal_hi_amount = 7_50_000L;
//        else if(age>=21 && age<30 ) ideal_hi_amount = 5_00_000L;
//        else if(age>=30 && age <40) ideal_hi_amount = 10_00_000L;
//        else if (age>=40 && age<50) ideal_hi_amount = 15_00_000L;
//        else if(age>=50) ideal_hi_amount = 20_00_000L;
//        Long additional_hi_required = ideal_hi_amount - his_amount;
//
//        data.put("emergency_fund", emergency_fund);
//        data.put("ideal_equity_amount", ideal_equity_amount);
//        data.put("ideal_debt_amount", ideal_debt_amount);
//        data.put("additional_li_required", additional_li_required);
//        data.put("additional_hi_required", additional_hi_required);
//        if(existing_equity_amount > ideal_equity_amount){
//            long switch_out_equity = (long) (((ideal_equity_percentage-equity_percentage) * (total==0?1:total)) / 100);
//            long switch_in_debt = (long) (((ideal_debt_percentage-debt_percentage) * (total==0?1:total)) / 100);
//            data.put("switch_out_in_equity", "Switch Out");
//            data.put("switch_out_in_debt", "Switch In");
//            data.put("switch_out_in_equity_amount", switch_out_equity);
//            data.put("switch_out_in_debt_amount", switch_in_debt);
//        }else{
//            long switch_in_equity = (long) (((ideal_equity_percentage-equity_percentage) * (total==0?1:total)) / 100);
//            long switch_out_debt = (long) (((ideal_debt_percentage-debt_percentage) * (total==0?1:total)) / 100);
//            data.put("switch_out_in_equity", "Switch In");
//            data.put("switch_out_in_debt", "Switch Out");
//            data.put("switch_out_in_equity_amount", switch_in_equity);
//            data.put("switch_out_in_debt_amount", switch_out_debt);
//        }
//        List<String> LifeInsuranceColumns = Arrays.asList("Person To Be Assured", "Addl. Coverage Required");
//        List<String> HealthInsuranceColumns = Arrays.asList("Person To Be Insured", "Addl. Coverage Required");
//
//        List<Map<String,Object>> LifeInsuranceData = new ArrayList<>();
//        List<Map<String,Object>> HealthInsuranceData = new ArrayList<>();
//
//        LifeInsuranceData.add(Map.of("Person To Be Assured", payload.get("first_name")+" "+payload.get("last_name"),
//                "Addl. Coverage Required", additional_li_required+""));
//        HealthInsuranceData.add(Map.of("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
//                "Addl. Coverage Required", additional_hi_required+""));
//
//        data.put("LifeInsuranceColumns", LifeInsuranceColumns);
//        data.put("HealthInsuranceColumns", HealthInsuranceColumns);
//        data.put("LifeInsuranceData", LifeInsuranceData);
//        data.put("HealthInsuranceData", HealthInsuranceData);


        getInsuranceChart(total_li_required, lis_amount, additional_li_required, ideal_hi_amount, his_amount, additional_hi_required, fileName);

        //Page12
//        try {
//            List<String> xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
//            List<String> values = Arrays.asList(total_li_required+"", lis_amount+"", additional_li_required+"");
//            createBarChart(xAxis, values, "Life Insurance Client", "INR (₹)", "","src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg");
//            data.put("life_insurance_chart", "src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg");
//            xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
//            values = Arrays.asList(ideal_hi_amount+"", his_amount+"", additional_hi_required+"");
//            createBarChart(xAxis, values, "Health Insurance Client", "INR (₹)", "","src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg");
//            data.put("health_insurance_chart", "src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg");
//            //TODO: add chart name as health-ID(from DB)
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        JSONArray loanemi = new JSONArray(payload.get("loanemi"));
//[{"Date of Investment":"12-04-2022","Amount":"111","Frequency":"Lumpsum","Market Value":"111"},{"Date of Investment":"12-04-2022","Amount":"222","Frequency":"Lumpsum","Market Value":"222"}]

        ArrayList<String> loanAmount = new ArrayList<>();
        ArrayList<String> loanNoInstallments = new ArrayList<>();
        ArrayList<String> loanAmountInstallment = new ArrayList<>();
        ArrayList<String> loanType = new ArrayList<>();
        ArrayList<String> loanStartDate = new ArrayList<>();

//         *typea20*=[[{"Type":"Term","Insurance Name":"li","Policy Term":"12","Premium Amount":"12","Premium Paying Term":"12","Sum Insured":"12","Start Date":"12-04-2022"}]],

        System.out.println("loanemi FETCH");
        for(int i=0; i<loanemi.length(); i++){
            System.out.println(loanemi.get(i));
            JSONObject object = new JSONObject(loanemi.get(i).toString());
            System.out.println("Loan: i: " + i + " Loan Amount: " + object.getString("Loan Amount") + " No of Installments Left : " + object.getString("No of Installments Left")
                    + " Amount of Installment:: " + object.getString("Amount of Installment")
                    + " Type: " + object.getString("Type") + " Start Date: " + object.getString("Start Date"));
            loanAmount.add(object.getString("Loan Amount"));
            loanNoInstallments.add(object.getString("No of Installments Left"));
            loanAmountInstallment.add(object.getString("Amount of Installment"));
            loanType.add(object.getString("Type"));
            loanStartDate.add(object.getString("Start Date"));
        }
        getNetworth(loanType, loanAmountInstallment,
                existing_equity_amount, equity_percentage, existing_debt_amount, debt_percentage, total);

        //YOUR NETWORTH
//        Long home_loan = 0L, personal_loan = 0L;
//        ArrayList<String> loans_type = (ArrayList<String>) payload.get("loan_emi_type");
//        ArrayList<String> loans_amount = (ArrayList<String>) payload.get("loan_emi_installment_amount");
//        for(int i=0; i<loans_type.size(); i++){
//            String loan_type = loans_type.get(i);
//            if(loan_type.equals("Home Loan")){
//                home_loan += Long.parseLong(loans_amount.get(i));
//            }else if(loan_type.equals("Personal Loan")){
//                personal_loan += Long.parseLong(loans_amount.get(i));
//            }
//        }
//
//
//        List<String> assetsColumns = Arrays.asList("Financial Assets", "Amount", "Percentage");
//        List<String> liabilitiesColumns = Arrays.asList("Liability Type", "Amount");
//
//        List<Map<String,Object>> assetsData = new ArrayList<>();
//        assetsData.add(Map.of("Financial Assets", "Equity", "Amount", existing_equity_amount+"", "Percentage", equity_percentage+"%"));
//        assetsData.add(Map.of("Financial Assets", "Debt/Fixed Income Asset", "Amount", existing_debt_amount+"", "Percentage", debt_percentage+"%"));
//        assetsData.add(Map.of("Financial Assets", "Total", "Amount", total+"", "Percentage", "100%"));
//
//        List<Map<String,Object>> liabilitiesData = new ArrayList<>();
//        liabilitiesData.add(Map.of("Liability Type", "Home Loan", "Amount", home_loan+"" ));
//        liabilitiesData.add(Map.of("Liability Type", "Personal Loan", "Amount", personal_loan+""));
//        liabilitiesData.add(Map.of("Liability Type", "Total", "Amount", home_loan+personal_loan+""));
//
//        data.put("assetsColumns", assetsColumns);
//        data.put("liabilitiesColumns", liabilitiesColumns);
//        data.put("assetsData", assetsData);
//        data.put("liabilitiesData", liabilitiesData);
//        return data;
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

//
//        koshantra_data.put("first_name", payload.get("first_name"));
//        koshantra_data.put("last_name", payload.get("last_name"));
//        koshantra_data.put("dob", payload.get("dob"));
//        koshantra_data.put("profession", payload.get("profession"));
//        koshantra_data.put("income", payload.get("income"));
//        koshantra_data.put("expense", payload.get("expense"));
//        koshantra_data.put("mobile_no", payload.get("mobile_no"));
//        koshantra_data.put("gender", payload.get("gender"));
//        koshantra_data.put("email", payload.get("email"));
//        koshantra_data.put("address", payload.get("address"));
//        koshantra_data.put("risk_profile", payload.get("risk_profile"));
//        koshantra_data.put("life_goals", payload.get("life_goals"));

//        koshantra_data.put("number_of_dependents", payload.get("number_of_dependents"));
//        List<String> dependentsColumns = Arrays.asList("Name", "DOB", "Relation");
//        List<Map<String,Object>> dependentsData = new ArrayList<>();
//        koshantra_data.put("dependentsColumns", dependentsColumns);
//        koshantra_data.put("dependentsData", dependentsData);

//        List<String> stocksColumns = Arrays.asList("Name", "Amount", "Market Value", "Date of Purchase", "Frequency");
//        List<String> stocksColumnsAllData = Arrays.asList("Name", "Amount", "Original Amount", "Market Value", "Date of Purchase", "Frequency");
//        List<Map<String,Object>> stocksData = new ArrayList<>();
//
//        ArrayList<String> stock_amount = (ArrayList<String>) payload.get("stocks_amount");
//        ArrayList<String> stocks_market_value = (ArrayList<String>) payload.get("stocks_market_value");
//        ArrayList<String> stocks_date_of_purchase = (ArrayList<String>) payload.get("stocks_date_of_purchase");
//        ArrayList<String> stocks_frequency = (ArrayList<String>) payload.get("stocks_frequency");
//        Long stock_full_amount = 0L, mfs_full_amount = 0L, ppfs_full_amount = 0L;
//        for(int i=0;i<stock_amount.size(); i++){
//            long amount = getAmountFrom(stocks_frequency.get(i), stock_amount.get(i), stocks_date_of_purchase.get(i));
//            stock_full_amount += amount;
//            stocksData.add(Map.of(
//                    "Amount", amount,
//                    "Original Amount", stock_amount.get(i),
//                    "Market Value", stocks_market_value.get(i),
//                    "Date of Purchase", stocks_date_of_purchase.get(i),
//                    "Frequency", stocks_frequency.get(i)));
//        }
//        koshantra_data.put("stocksColumns", stocksColumns);
//        koshantra_data.put("stocksData", stocksData);
//        koshantra_data.put("stocksColumnsAllData", stocksColumnsAllData);
//
//        List<String> mfsColumns = Arrays.asList("Market Value", "Amount", "Type", "Date of Purchase", "Frequency");
//        List<String> mfsColumnsAllData = Arrays.asList("Market Value", "Amount", "Original Amount", "Type", "Date of Purchase", "Frequency");
//        List<Map<String,Object>> mfsData = new ArrayList<>();
//        ArrayList<String> mfs_mkt_value = (ArrayList<String>) payload.get("mf_mkt_value");
//        ArrayList<String> mf_amount = (ArrayList<String>) payload.get("mf_amount");
//        ArrayList<String> mfs_type = (ArrayList<String>) payload.get("mf_type");
//        ArrayList<String> mfs_date_of_purchase = (ArrayList<String>) payload.get("mf_date_of_purchase");
//        ArrayList<String> mfs_frequency = (ArrayList<String>) payload.get("mf_frequency");
//        for(int i=0;i<mfs_frequency.size(); i++){
//            long amount = getAmountFrom(mfs_frequency.get(i), mf_amount.get(i), mfs_date_of_purchase.get(i));
//            mfs_full_amount += amount;
//            mfsData.add(Map.of("Type", mfs_type.get(i),
//                    "Amount", amount,
//                    "Original Amount", mf_amount.get(i),
//                    "Market Value", mfs_mkt_value.get(i),
//                    "Date of Purchase", mfs_date_of_purchase.get(i),
//                    "Frequency", mfs_frequency.get(i)));
//        }
//        koshantra_data.put("mfsColumns", mfsColumns);
//        koshantra_data.put("mfsData", mfsData);
//        koshantra_data.put("mfsColumnsAllData", mfsColumnsAllData);
//
//        List<String> lisColumns = Arrays.asList("Start Date", "Premium Paying Term", "Type", "Sum Assured", "Name", "Policy Term");
//        List<Map<String,Object>> lisData = new ArrayList<>();
//        ArrayList<String> lis_start_date = (ArrayList<String>) payload.get("li_start_date");
//        ArrayList<String> li_premium_paying_term = (ArrayList<String>) payload.get("li_premium_paying_term");
//        ArrayList<String> li_type = (ArrayList<String>) payload.get("li_type");
//        ArrayList<String> li_sum_assured = (ArrayList<String>) payload.get("li_sum_assured");
//        ArrayList<String> li_name = (ArrayList<String>) payload.get("li_name");
//        ArrayList<String> li_policy_term = (ArrayList<String>) payload.get("li_policy_term");
//        for(int i=0;i<li_name.size(); i++){
//            lisData.add(Map.of("Name", li_name.get(i),
//                    "Start Date", lis_start_date.get(i),
//                    "Premium Paying Term", li_premium_paying_term.get(i),
//                    "Sum Assured", li_sum_assured.get(i),
//                    "Type", li_type.get(i),
//                    "Policy Term", li_policy_term.get(i)));
//        }
//        koshantra_data.put("lisColumns", lisColumns);
//        koshantra_data.put("lisData", lisData);
//
//        List<String> hisColumns = Arrays.asList("Name", "Premium Amount", "Type", "Sum Insured", "Start Date");
//        List<Map<String,Object>> hisData = new ArrayList<>();
//        ArrayList<String> hi_premium_amount = (ArrayList<String>) payload.get("hi_premium_amount");
//        ArrayList<String> hi_name = (ArrayList<String>) payload.get("hi_name");
//        ArrayList<String> hi_type = (ArrayList<String>) payload.get("hi_type");
//        ArrayList<String> hi_start_date = (ArrayList<String>) payload.get("hi_start_date");
//        ArrayList<String> hi_sum_insured = (ArrayList<String>) payload.get("hi_sum_insured");
//        for(int i=0;i<hi_start_date.size(); i++){
//            hisData.add(Map.of("Name", hi_name.get(i),
//                    "Premium Amount", hi_premium_amount.get(i),
//                    "Type", hi_type.get(i),
//                    "Sum Insured", hi_sum_insured.get(i),
//                    "Start Date", hi_start_date.get(i)));
//        }
//        koshantra_data.put("hisColumns", hisColumns);
//        koshantra_data.put("hisData", hisData);
//
//        List<String> bondsColumns = Arrays.asList("Name", "Invested", "Maturity Date", "Date of Investment");
//        List<Map<String,Object>> bondsData = new ArrayList<>();
//        ArrayList<String> bonds_name = (ArrayList<String>) payload.get("bonds_name");
//        ArrayList<String> bond_invested = (ArrayList<String>) payload.get("bonds_invested");
//        ArrayList<String> bonds_date_of_investment = (ArrayList<String>) payload.get("bonds_date_of_investment");
//        ArrayList<String> bonds_maturity_date = (ArrayList<String>) payload.get("bonds_maturity_date");
//        for(int i=0;i<bonds_name.size(); i++){
//            bondsData.add(Map.of("Name", bonds_name.get(i),
//                    "Invested", bond_invested.get(i),
//                    "Maturity Date", bonds_maturity_date.get(i),
//                    "Date of Investment", bonds_date_of_investment.get(i)));
//        }
//        koshantra_data.put("bondsColumns", bondsColumns);
//        koshantra_data.put("bondsData", bondsData);
//
//        List<String> ppfsColumns = Arrays.asList("Invested", "Date of Investment", "Frequency");
//        List<String> ppfsColumnsAllData = Arrays.asList("Invested", "Original Amount", "Date of Investment", "Frequency");
//        List<Map<String,Object>> ppfsData = new ArrayList<>();
//        ArrayList<String> ppf_invested = (ArrayList<String>) payload.get("ppf_invested");
//        ArrayList<String> ppf_date_of_investment = (ArrayList<String>) payload.get("ppf_date_of_investment");
//        ArrayList<String> ppf_frequency = (ArrayList<String>) payload.get("ppf_frequency");
//        for(int i=0;i<ppf_invested.size(); i++){
//            long amount = getAmountFrom(ppf_frequency.get(i), ppf_invested.get(i), ppf_date_of_investment.get(i));
//            ppfs_full_amount += amount;
//            ppfsData.add(Map.of("Invested", amount,
//                    "Original Amount", ppf_invested.get(i),
//                    "Date of Investment", ppf_date_of_investment.get(i),
//                    "Frequency", ppf_frequency.get(i)));
//        }
//        koshantra_data.put("ppfsColumns", ppfsColumns);
//        koshantra_data.put("ppfsData", ppfsData);
//        koshantra_data.put("ppfsColumnsAllData", ppfsColumnsAllData);
//
//        List<String> fdsColumns = Arrays.asList("Name", "Amount", "Start Date", "Maturity Date");
//        List<Map<String,Object>> fdsData = new ArrayList<>();
//        ArrayList<String> fd_start_date = (ArrayList<String>) payload.get("fd_start_date");
//        ArrayList<String> fd_name = (ArrayList<String>) payload.get("fd_name");
//        ArrayList<String> fd_maturity_date = (ArrayList<String>) payload.get("fd_maturity_date");
//        ArrayList<String> fd_amount = (ArrayList<String>) payload.get("fd_amount");
//        for(int i=0;i<fd_name.size(); i++){
//            fdsData.add(Map.of("Name", fd_name.get(i),
//                    "Amount", fd_amount.get(i),
//                    "Start Date", fd_start_date.get(i),
//                    "Maturity Date", fd_maturity_date.get(i)));
//        }
//        koshantra_data.put("fdsColumns", fdsColumns);
//        koshantra_data.put("fdsData", fdsData);
//
//        List<String> loanoremisColumns = Arrays.asList("Start Date", "Installment Amount", "Installment Left", "Type");
//        List<Map<String,Object>> loanoremisData = new ArrayList<>();
//        ArrayList<String> loan_emi_start_date = (ArrayList<String>) payload.get("loan_emi_start_date");
//        ArrayList<String> loan_emi_installment_amount = (ArrayList<String>) payload.get("loan_emi_installment_amount");
//        ArrayList<String> loan_emi_installement_left = (ArrayList<String>) payload.get("loan_emi_installement_left");
//        ArrayList<String> loan_emi_type = (ArrayList<String>) payload.get("loan_emi_type");
//        for(int i=0;i<loan_emi_installment_amount.size(); i++){
//            loanoremisData.add(Map.of("Start Date", loan_emi_start_date.get(i),
//                    "Installment Amount", loan_emi_installment_amount.get(i),
//                    "Installment Left", loan_emi_installement_left.get(i),
//                    "Type", loan_emi_type.get(i)));
//        }
//        koshantra_data.put("loanoremisColumns", loanoremisColumns);
//        koshantra_data.put("loanoremisData", loanoremisData);


        //Page 1
//        data.put("logo", "src/main/resources/templates/images/koshantra_logo.PNG");
//        data.put("logo", "src/main/resources/templates/images/Wealth_Creation_Logo.png");
//        data.put("client_name", payload.get("first_name")+" "+payload.get("last_name"));
//        String fileName = payload.get("first_name")+" "+payload.get("last_name") + "_" + UUID.randomUUID().toString();
//        String fileName = payload.get("first_name")+"_"+payload.get("last_name");
//        String cash_flow_chart = "src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg";
//        String pie_chart_1 = "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg";
//        String pie_chart_2 = "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg";
//        String life_insurance_chart = "src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg";
//        String health_insurance_chart = "src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg";
//        File cashFlowChartFile, pieChart1File, pieChart2File, lifeInsuranceFile, healthInsuranceFile;
//        data.put("ID",fileName);

        //Page3
//        List<String> personalInfoColumns = Arrays.asList("Relationship", "Name", "DOB", "Occupation", "Mobile No.");
//        List<Map<String,Object>> personalInfoData = new ArrayList<>();
////        personalInfoData.add(Map.of("Relationship", "Client"));
//        personalInfoData.add(Map.of("Relationship", "Self", "Name", payload.get("first_name")+" "+payload.get("last_name"), "DOB", payload.get("dob"), "Occupation", payload.get("profession"), "Mobile No.", payload.get("mobile_no")));
////        personalInfoData.add(Map.of("Relationship", "Partner"));
//        ArrayList<String> dependents_relationship = (ArrayList<String>) payload.get("dependents_relation");
//        ArrayList<String> dependents_dob = (ArrayList<String>) payload.get("dependents_dob");
//        ArrayList<String> dependents_name = (ArrayList<String>) payload.get("dependents_name");
//
//        for(int i = 0; i<dependents_name.size(); i++){
//            personalInfoData.add(Map.of("Relationship", dependents_relationship.get(i),
//                    "Name", dependents_name.get(i),
//                    "DOB", dependents_dob.get(i)));
//
//            dependentsData.add(Map.of("Name", dependents_name.get(i),
//                    "DOB", dependents_dob.get(i),
//                    "Relation", dependents_relationship.get(i)));
//        }
//
////        personalInfoData.add(Map.of("Relationship", "Spouse", "Name", "Ishween", "DOB", "01/06/1999", "Occupation", "Salaried", "Mobile No.", "9540305678"));
//////        personalInfoData.add(Map.of("Relationship", "Dependents"));
////        personalInfoData.add(Map.of("Relationship", "Son", "Name", "Ishween", "DOB", "01/06/1999", "Occupation", "Salaried", "Mobile No.", "9540305678"));
////        personalInfoData.add(Map.of("Relationship", "Daughter", "Name", "Ishween", "DOB", "01/06/1999", "Occupation", "Salaried", "Mobile No.", "9540305678"));
//        data.put("personalInfoColumns", personalInfoColumns);
//        data.put("personalInfoData", personalInfoData);

//        DecimalFormat df = new DecimalFormat("0.00"); /*df.format(futureCost)*/
//
//        //Page4
//        List<String> incomeColumns = Arrays.asList("Income", "Monthly(Rs.)", "Annually(Rs.)");
//        List<Map<String,Object>> incomeData = new ArrayList<>();
//        String annual_income = (String) payload.get("income");
//        String annual_expense = (String) payload.get("expense");
//        if(payload.get("profession").equals("Self Employed")){
//            incomeData.add(Map.of("Income", "Business Income", "Monthly(Rs.)", df.format(Long.parseLong(annual_income)/12.0), "Annually(Rs.)", annual_income));
//            incomeData.add(Map.of("Income", "Income Salary", "Monthly(Rs.)", "0", "Annually(Rs.)", "0"));
//        }else{
//            incomeData.add(Map.of("Income", "Business Income", "Monthly(Rs.)", "0", "Annually(Rs.)", "0"));
//            incomeData.add(Map.of("Income", "Income Salary", "Monthly(Rs.)", df.format(Long.parseLong(annual_income)/12.0), "Annually(Rs.)", annual_income));
//        }
////        incomeData.add(Map.of("Income", "Business Income", "Monthly ₹", "0", "Annually(₹)", "0"));
////        incomeData.add(Map.of("Income", "Income Salary", "Monthly ₹", "100", "Annually(₹)", "1200"));
////        incomeData.add(Map.of("Income", "Rental Income", "Monthly ₹", "300", "Annually(₹)", "3600"));
//        data.put("incomeColumns", incomeColumns);
//        data.put("incomeData", incomeData);
//        List<Map<String, Object>> incomeTotal = new ArrayList<>();
//        incomeTotal.add(Map.of("Income", "Total", "Monthly(Rs.)", df.format(Long.parseLong(annual_income)/12.0), "Annually(Rs.)", annual_income));
//        data.put("incomeTotal", incomeTotal);
//
//        //Page5
//        try {
//            List<String> xAxis = Arrays.asList("Income", "Expenses");
//            List<String> values = Arrays.asList(annual_income, annual_expense);
//            cashFlowChartFile = createBarChart(xAxis, values, "", "INR (₹)", "","src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg");
//            data.put("cash_flow_chart", "src/main/resources/templates/images/cash_flow_chart"+fileName+".jpeg");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        //Page 8
////        List<String> assumptionColumns = Arrays.asList("Description", "Percentage");
////        List<Map<String,Object>> assumptionData = new ArrayList<>();
////        assumptionData.add(Map.of("Description", "Liquid Mutual Fund", "Percentage", "6"));
////        assumptionData.add(Map.of("Description", "Debt Mutual Fund", "Percentage", "7"));
////        assumptionData.add(Map.of("Description", "Equity Mutual Fund", "Percentage", "12"));
////        data.put("assumptionColumns", assumptionColumns);
////        data.put("assumptionData", assumptionData);
//
//        //Page9
//        List<String> financialGoalsColumns = Arrays.asList("Goal Name", "Years to Goal", "Present Value", "Growth Rate(%)", "Future Cost(Rs.)");
//        List<Map<String,Object>> financialGoalsData = new ArrayList<>();
//        String life_goals = (String) payload.get("life_goals");
////        DecimalFormat df = new DecimalFormat("0.00"); /*df.format(futureCost)*/
//        if(life_goals != null) {
//            String[] life_goals_list = life_goals.split(",");
////        ArrayList<String> life_goals = (ArrayList<String>) payload.get("life_goals");
//            for (String life_goal : life_goals_list) {
//                if (life_goal.equals("Child Education")) {
//                    //child dependents
//                    for (int i = 0; i < dependents_relationship.size(); i++) {
//                        if (dependents_relationship.get(i).equals("Child")) {
//                            LocalDate curDate = LocalDate.now();
//                            int age = Period.between(LocalDate.parse(dependents_dob.get(i)), curDate).getYears();
//                            if (age < 18) {
//                                double rate = 0.08;
//                                int present_value = 700000;
//                                double futureCost = (present_value) * Math.pow(1 + rate, (18-age));
//
//                                financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Graduation",
//                                        "Years to Goal", (18 - age),
//                                        "Present Value", present_value,
//                                        "Growth Rate(%)", (rate*100),
//                                        "Future Cost(Rs.)", Math.round(futureCost)));
//                            }
//                            if (age < 21) {
//                                double rate = 0.08;
//                                int present_value = 2000000;
//                                double futureCost = (present_value) * Math.pow(1 + rate, (21 - age));
//                                financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Higher Studies",
//                                        "Years to Goal", (21 - age),
//                                        "Present Value", present_value,
//                                        "Growth Rate(%)", (rate*100),
//                                        "Future Cost(Rs.)", Math.round(futureCost)));
//                            }
//                        }
//                    }
//                }
//                if (life_goal.equals("Self Marriage")) {
//                    LocalDate curDate = LocalDate.now();
//                    int age = Period.between(LocalDate.parse((String) payload.get("dob")), curDate).getYears(), present_value = 2000000;
//                    double rate = 0.06;
//                    double futureCost = (present_value) * Math.pow(1 + rate, (25 - age));
//                    financialGoalsData.add(Map.of("Goal Name", payload.get("first_name") + " " + payload.get("last_name") + "-Marriage",
//                            "Years to Goal", (25 - age),
//                            "Present Value", present_value,
//                            "Growth Rate(%)", (rate*100),
//                            "Future Cost(Rs.)", Math.round(futureCost)));
//                }
//                if (life_goal.equals("Retirement")) {
//                    LocalDate curDate = LocalDate.now();
//                    int age = Period.between(LocalDate.parse((String) payload.get("dob")), curDate).getYears();
//                    double rate = 0.06;
//                    double futureCost = (Long.parseLong(annual_expense)) * Math.pow(1 + rate, (58 - age));
//                    financialGoalsData.add(Map.of("Goal Name", payload.get("first_name") + " " + payload.get("last_name") + "-Retirement",
//                            "Years to Goal", (58 - age),
//                            "Present Value", annual_expense,
//                            "Growth Rate(%)", (rate*100),
//                            "Future Cost(Rs.)", Math.round(futureCost)));
//                }
//                if (life_goal.equals("Child Marriage")) {
//                    for (int i = 0; i < dependents_relationship.size(); i++) {
//                        if (dependents_relationship.get(i).equals("Child")) {
//                            LocalDate curDate = LocalDate.now();
//                            int age = Period.between(LocalDate.parse(dependents_dob.get(i)), curDate).getYears();
//                            double rate = 0.06;
//                            int present_value = 2000000;
//                            double futureCost = (present_value) * Math.pow(1 + rate, (25 - age));
//                            financialGoalsData.add(Map.of("Goal Name", dependents_name.get(i) + "-Marriage",
//                                    "Years to Goal", (25 - age),
//                                    "Present Value", present_value,
//                                    "Growth Rate(%)", (rate*100),
//                                    "Future Cost(Rs.)", Math.round(futureCost)));
//                        }
//                    }
//                }
//            }
//        }else{
//            System.out.println("LIFE GOALS IS COMING AS NULL");
//        }
////        financialGoalsData.add(Map.of("Goal Name", "X-Graduation", "Years to Goal", "12", "Present Cost of Goal", "300000", "Growth Rate(%)", "8", "Future Cost", "583000"));
//        data.put("financialGoalsColumns", financialGoalsColumns);
//        data.put("financialGoalsData", financialGoalsData);
//
//        ArrayList<String> stocks_amount = (ArrayList<String>) payload.get("stocks_amount");
//        ArrayList<String> mfs_amount = (ArrayList<String>) payload.get("mf_amount");
//        ArrayList<String> bonds_invested = (ArrayList<String>) payload.get("bonds_invested");
//        ArrayList<String> ppfs_invested = (ArrayList<String>) payload.get("ppf_invested");
//        ArrayList<String> fds_amount = (ArrayList<String>) payload.get("fd_amount");
//
////        List<Long> longList = Lists.transform(mfs_amount, new Function<String, Long>() {
////            public Long apply(String s) {
////                return Long.valueOf(s);
////            }
////        });
//
////        Long total_stocks_amount = stocks_amount == null || stocks_amount.isEmpty()? 0L : stocks_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);
////        Long total_mfs_amount = mfs_amount == null || mfs_amount.isEmpty()? 0L : mfs_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);
//        Long total_bonds_amount = bonds_invested == null || bonds_invested.isEmpty()? 0L : bonds_invested.stream().map(Long::parseLong).reduce(0L, Long::sum);
////        Long total_ppfs_amount = ppfs_invested == null || ppfs_invested.isEmpty()? 0L : ppfs_invested.stream().map(Long::parseLong).reduce(0L, Long::sum);
//        Long total_fds_amount = fds_amount == null || fds_amount.isEmpty()? 0L : fds_amount.stream().map(Long::parseLong).reduce(0L, Long::sum);
//
//        Long existing_equity_amount = stock_full_amount + mfs_full_amount;
//        Long existing_debt_amount = total_bonds_amount + ppfs_full_amount + total_fds_amount;
//
//        Long total = existing_equity_amount + existing_debt_amount;
//
//        float equity_percentage = 0F, debt_percentage = 0F;
//        try {
//            equity_percentage = total.equals(0L) ? 0F : Float.valueOf(df.format((existing_equity_amount / (total*1.00)) * (100.00)));//add point upto 2 decimal
////            debt_percentage = total.equals(0L) ? 0 : (int) ((existing_debt_amount / (total*1.0)) * 100);
//            debt_percentage = 100 - equity_percentage;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        int age = Period.between(LocalDate.parse((String) payload.get("dob")), LocalDate.now()).getYears();
//        float ideal_equity_percentage = Float.valueOf(df.format(100-age));
//        float ideal_debt_percentage = age;
//
//        //Page10
//        try {
//            List<String> items = Arrays.asList("Equity ("+equity_percentage+"%)", "Debt ("+debt_percentage+"%)");
//            List<Float> itemValues = Arrays.asList(equity_percentage, debt_percentage);
//            pieChart1File = createPieChart(items, itemValues, "Current Asset Allocation", "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg");
//            data.put("pie_chart_1", "src/main/resources/templates/images/pie_chart_1"+fileName+".jpeg");
//
//            items = Arrays.asList("Equity ("+ideal_equity_percentage+"%)", "Debt ("+ideal_debt_percentage+"%)");
//            itemValues = Arrays.asList(ideal_equity_percentage, ideal_debt_percentage);
//            pieChart2File = createPieChart(items, itemValues, "Recommended Asset Allocation", "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg");
//            data.put("pie_chart_2", "src/main/resources/templates/images/pie_chart_2"+fileName+".jpeg");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        //Page11
////        Long emergency_fund = Long.parseLong(annual_income)/4; //3 MONTH SALARY
//        Long emergency_fund = Long.parseLong(annual_expense)/2; //Formual 6 * monthly expense
//        long ideal_equity_amount = ((long)ideal_equity_percentage * (Long.parseLong(annual_income)/12))* 100;
//        long ideal_debt_amount = ((long)ideal_debt_percentage * (Long.parseLong(annual_income)/12))*100;
//        Long lis_amount = ((ArrayList<String>) payload.get("li_sum_assured")).stream().map(Long::parseLong).reduce(0L, Long::sum);
//        Long his_amount = ((ArrayList<String>) payload.get("hi_sum_insured")).stream().map(Long::parseLong).reduce(0L, Long::sum);
//
//        Long total_li_required = 20 * (Long.parseLong(annual_income));
//        Long additional_li_required = (total_li_required) - lis_amount;
//        Long ideal_hi_amount = 0L;
//        if(age>=21 && age<30 && (!payload.get("number_of_dependents").toString().isEmpty() && Integer.parseInt((String) payload.get("number_of_dependents")) > 0)) ideal_hi_amount = 7_50_000L;
//        else if(age>=21 && age<30 ) ideal_hi_amount = 5_00_000L;
//        else if(age>=30 && age <40) ideal_hi_amount = 10_00_000L;
//        else if (age>=40 && age<50) ideal_hi_amount = 15_00_000L;
//        else if(age>=50) ideal_hi_amount = 20_00_000L;
//        Long additional_hi_required = ideal_hi_amount - his_amount;
//
//        data.put("emergency_fund", emergency_fund);
//        data.put("ideal_equity_amount", ideal_equity_amount);
//        data.put("ideal_debt_amount", ideal_debt_amount);
//        data.put("additional_li_required", additional_li_required);
//        data.put("additional_hi_required", additional_hi_required);
//        if(existing_equity_amount > ideal_equity_amount){
//            long switch_out_equity = (long) (((ideal_equity_percentage-equity_percentage) * (total==0?1:total)) / 100);
//            long switch_in_debt = (long) (((ideal_debt_percentage-debt_percentage) * (total==0?1:total)) / 100);
//            data.put("switch_out_in_equity", "Switch Out");
//            data.put("switch_out_in_debt", "Switch In");
//            data.put("switch_out_in_equity_amount", switch_out_equity);
//            data.put("switch_out_in_debt_amount", switch_in_debt);
//        }else{
//            long switch_in_equity = (long) (((ideal_equity_percentage-equity_percentage) * (total==0?1:total)) / 100);
//            long switch_out_debt = (long) (((ideal_debt_percentage-debt_percentage) * (total==0?1:total)) / 100);
//            data.put("switch_out_in_equity", "Switch In");
//            data.put("switch_out_in_debt", "Switch Out");
//            data.put("switch_out_in_equity_amount", switch_in_equity);
//            data.put("switch_out_in_debt_amount", switch_out_debt);
//        }
//        List<String> LifeInsuranceColumns = Arrays.asList("Person To Be Assured", "Addl. Coverage Required");
//        List<String> HealthInsuranceColumns = Arrays.asList("Person To Be Insured", "Addl. Coverage Required");
//
//        List<Map<String,Object>> LifeInsuranceData = new ArrayList<>();
//        List<Map<String,Object>> HealthInsuranceData = new ArrayList<>();
//
////        List<String> LifeInsuranceData = Arrays.asList("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
////                "Addl. Coverage Required", additional_li_required+"");
////        List<String> HealthInsuranceData = Arrays.asList("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
////                "Addl. Coverage Required", additional_hi_required+"");
//        LifeInsuranceData.add(Map.of("Person To Be Assured", payload.get("first_name")+" "+payload.get("last_name"),
//                "Addl. Coverage Required", additional_li_required+""));
//        HealthInsuranceData.add(Map.of("Person To Be Insured", payload.get("first_name")+" "+payload.get("last_name"),
//                "Addl. Coverage Required", additional_hi_required+""));
//
//        data.put("LifeInsuranceColumns", LifeInsuranceColumns);
//        data.put("HealthInsuranceColumns", HealthInsuranceColumns);
//        data.put("LifeInsuranceData", LifeInsuranceData);
//        data.put("HealthInsuranceData", HealthInsuranceData);
//
//        //Page12
//        try {
//            List<String> xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
//            List<String> values = Arrays.asList(total_li_required+"", lis_amount+"", additional_li_required+"");
//            lifeInsuranceFile = createBarChart(xAxis, values, "Life Insurance Client", "INR (₹)", "","src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg");
//            data.put("life_insurance_chart", "src/main/resources/templates/images/life_insurance_chart"+fileName+".jpeg");
//            xAxis = Arrays.asList("Total Required", "Current Available", "Additional Required");
//            values = Arrays.asList(ideal_hi_amount+"", his_amount+"", additional_hi_required+"");
//            healthInsuranceFile = createBarChart(xAxis, values, "Health Insurance Client", "INR (₹)", "","src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg");
//            data.put("health_insurance_chart", "src/main/resources/templates/images/health_insurance_chart"+fileName+".jpeg");
//            //TODO: add chart name as health-ID(from DB)
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        //YOUR NETWORTH
//        Long home_loan = 0L, personal_loan = 0L;
//        ArrayList<String> loans_type = (ArrayList<String>) payload.get("loan_emi_type");
//        ArrayList<String> loans_amount = (ArrayList<String>) payload.get("loan_emi_installment_amount");
//        for(int i=0; i<loans_type.size(); i++){
//            String loan_type = loans_type.get(i);
//            if(loan_type.equals("Home Loan")){
//                home_loan += Long.parseLong(loans_amount.get(i));
//            }else if(loan_type.equals("Personal Loan")){
//                personal_loan += Long.parseLong(loans_amount.get(i));
//            }
//        }
//
////        List<String> netWorthColumns = Arrays.asList("Assets", "Liabilities");
////        List<String> assetsColumnsRepeat = Arrays.asList("Assets");
////        List<String> liabilitiesColumnsRepeat = Arrays.asList("Liabilities");
////
////        List<Map<String,Object>> assetsColumns = new ArrayList<>();
////        List<Map<String,Object>> liabilitiesColumns = new ArrayList<>();
////        assetsColumns.add(Map.of("Assets", "Financial Assets"));
////        assetsColumns.add(Map.of("Assets", "Amount"));
////        assetsColumns.add(Map.of("Assets", "Percentage"));
////
////        liabilitiesColumns.add(Map.of("Liabilities", "Liability Type"));
////        liabilitiesColumns.add(Map.of("Liabilities", "Amount"));
//
//
//        List<String> assetsColumns = Arrays.asList("Financial Assets", "Amount", "Percentage");
//        List<String> liabilitiesColumns = Arrays.asList("Liability Type", "Amount");
//
//
////        assetsColumns.add(Map.of("Financial Assets", "Equity", "Amount", existing_equity_amount+"", "Percentage", equity_percentage+""));
////        liabilitiesColumns.add(Map.of("Financial Assets", "Equity", "Amount", existing_equity_amount+"", "Percentage", equity_percentage+""));
//
////        List<String> assetsColumns = Arrays.asList("Financial Assets", "Amount", "Percentage");
////        List<String> liabilitiesColumns = Arrays.asList("Liability Type", "Amount");
//
////        List<String> assetsData = Arrays.asList("Financial Assets", "Equity", "Amount", existing_equity_amount+"", "Percentage", equity_percentage+"");
//        List<Map<String,Object>> assetsData = new ArrayList<>();
//        assetsData.add(Map.of("Financial Assets", "Equity", "Amount", existing_equity_amount+"", "Percentage", equity_percentage+"%"));
//        assetsData.add(Map.of("Financial Assets", "Debt/Fixed Income Asset", "Amount", existing_debt_amount+"", "Percentage", debt_percentage+"%"));
//        assetsData.add(Map.of("Financial Assets", "Total", "Amount", total+"", "Percentage", "100%"));
//
//        List<Map<String,Object>> liabilitiesData = new ArrayList<>();
//        liabilitiesData.add(Map.of("Liability Type", "Home Loan", "Amount", home_loan+"" ));
//        liabilitiesData.add(Map.of("Liability Type", "Personal Loan", "Amount", personal_loan+""));
//        liabilitiesData.add(Map.of("Liability Type", "Total", "Amount", home_loan+personal_loan+""));
//
//
////        data.put("netWorthColumns", netWorthColumns);
////        data.put("assetsColumnsRepeat", assetsColumnsRepeat);
////        data.put("liabilitiesColumnsRepeat", liabilitiesColumnsRepeat);
//        data.put("assetsColumns", assetsColumns);
//        data.put("liabilitiesColumns", liabilitiesColumns);
//        data.put("assetsData", assetsData);
//        data.put("liabilitiesData", liabilitiesData);
//
//        data.put("first_name", payload.get("first_name"));
//        data.put("last_name", payload.get("last_name"));
//        //Page13

        //Page14

        try {
            File outputFile = pdfGeneratorUtil.createPdf(payload.get("name[first]")+ "_" + payload.get("name[last]") + "_report",this.data, "Page1_Cover","Page2","Page2.1_Index.html","Page3_PI","Page4_CashFlow", "Page5_CashFlowGraph",
                    /*"Page7_Investment"*//*, "Page8_Assumptions",*/"Page9_FinancialGoals","Page10_AssetAllocationChart","your_networth", "Page11_ActionPlan",
                    "Page12_ActionPlanChart", "Page14_Disclaimer");

            File file = pdfGeneratorUtil.createPdf(payload.get("name[first]")+ "_" + payload.get("name[last]") + "_customer_all_data", koshantra_data,
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
        String dateOfpurchase = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println(sdf2.format(sdf.parse(date_of_purchase)));
            dateOfpurchase = sdf2.format(sdf.parse(date_of_purchase));
        }catch (Exception e){

        }

        LocalDate started = LocalDate.parse(dateOfpurchase);

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
