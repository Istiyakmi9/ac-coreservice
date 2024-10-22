package com.bot.coreservice.services;

import com.bot.coreservice.Repository.AccountSequenceRepository;
import com.bot.coreservice.Repository.InvestmentRepository;
import com.bot.coreservice.Repository.UserFileRepository;
import com.bot.coreservice.Repository.UserRepository;
import com.bot.coreservice.contracts.ICDProductInvestmentService;
import com.bot.coreservice.contracts.IInvestmentService;
import com.bot.coreservice.contracts.IUserService;
import com.bot.coreservice.db.LowLevelExecution;
import com.bot.coreservice.entity.CDProductInvestment;
import com.bot.coreservice.entity.InvestmentDetail;
import com.bot.coreservice.entity.User;
import com.bot.coreservice.entity.UserFile;
import com.bot.coreservice.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    FileManager fileManager;
    @Autowired
    UserFileRepository userFileRepository;
    @Autowired
    ICDProductInvestmentService ICDProductInvestmentService;
    @Autowired
    IInvestmentService iInvestmentService;
    @Autowired
    AccountSequenceRepository accountSequenceRepository;
    @Autowired
    InvestmentRepository investmentRepository;

    public String addUserExcelService(MultipartFile userExcel) throws Exception {
        try {
            List<User> users = readAndMapUserExcel(userExcel);
            if (!users.isEmpty()) {
                userRepository.saveAll(users);
            }

            return "user inserted successfully";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<User> filterUserService(FilterModel filterModel) {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_searchString", filterModel.getSearchString(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_pageIndex", filterModel.getPageIndex(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_pageSize", filterModel.getPageSize(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_sortBy", filterModel.getSortBy(), Types.VARCHAR));

        var dataSet = lowLevelExecution.executeProcedure("sp_user_by_filter", dbParameters);
        return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<User>>() {
        });
    }

    public UserDetail getUserByIdService(long userId) throws Exception {
        if (userId == 0)
            throw new Exception("Invalid user Id");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_userId", userId, Types.BIGINT));

        var dataSet = lowLevelExecution.executeProcedure("sp_user_get_by_id", dbParameters);
        var result = objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<UserDetail>>() {
        });

        if (result == null || result.isEmpty())
            return null;
        else
            return result.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDetail addNewUserService(String userData, MultipartFile profileImage) throws Exception {
        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        User user = objectMapper.readValue(userData, User.class);
        validateUser(user);

        user.setAccountId("");
        user.setCreatedBy(1L);
        user.setUpdatedBy(1L);
        user.setCreatedOn(currentDate);
        user.setUpdatedOn(currentDate);

        var newUser = userRepository.save(user);

        saveUserProfile(profileImage, newUser);

        var userDetail = objectMapper.readValue(userData, UserDetail.class);
        if (user.getProductType() == 4) {
            var investmentDetail = objectMapper.convertValue(userDetail.getInvestmentDetail(), InvestmentDetail.class);
            investmentDetail.setUserId(newUser.getUserId());
            investmentDetail.setAccountId(getAccountNumber(user.getProductType()));
            investmentDetail = iInvestmentService.addInvestmentService(investmentDetail);
            userDetail.setInvestmentDetail(investmentDetail);
        } else {
            var inventoryDetail = objectMapper.convertValue(userDetail.getCdProductInvestment(), CDProductInvestment.class);
            inventoryDetail.setUserId(newUser.getUserId());
            inventoryDetail = ICDProductInvestmentService.addCDProductInvestmentService(inventoryDetail);
            userDetail.setCdProductInvestment(inventoryDetail);
        }

        userDetail.setAccountId(newUser.getAccountId());
        return userDetail;
    }

    private void validateUser(User user) throws Exception {
        if (user.getFirstName() == null || user.getFirstName().isEmpty())
            throw new Exception("Invalid first name");

        if (user.getLastName() == null || user.getLastName().isEmpty())
            throw new Exception("Invalid last name");

        if (user.getMobileNumber() == null || user.getMobileNumber().isEmpty() || user.getMobileNumber().length() != 10)
            throw new Exception("Invalid mobile number");

        if (user.getAadharNumber() == null || user.getAadharNumber().isEmpty() || user.getAadharNumber().length() != 12)
            throw new Exception("Invalid aadhar number");

        if (user.getAddress() == null || user.getAddress().isEmpty())
            throw new Exception("Invalid address");

        if (user.getReferenceBy() == null || user.getReferenceBy().isEmpty())
            throw new Exception("Invalid reference");

        if (user.getProductType() == 0)
            throw new Exception("Invalid product selected");
    }

    private String getAccountNumber(int productType) throws Exception {
        var lastAccountSequence = accountSequenceRepository.findById(1).orElseThrow(
                () -> new Exception("Account sequence detail not found")
        );
        var nextAccountNumber = lastAccountSequence.getLastSequenceNumber() + 1;
        var accountNumber = String.format("%05d", nextAccountNumber);

        lastAccountSequence.setLastSequenceNumber(nextAccountNumber);
        accountSequenceRepository.save(lastAccountSequence);

        if (productType == ApplicationConstant.Investment) {
            return "INV" + accountNumber;
        } else {
            return "PRO" + accountNumber;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDetail updateUserService(String userData, MultipartFile profileImage) throws Exception {
        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        User user = objectMapper.readValue(userData, User.class);
        if (user.getUserId() == 0 || user.getUserId() == null)
            throw new Exception("Invalid user");

        validateUser(user);
        var existingUserData = userRepository.findById(user.getUserId());

        var existingUser = getUpdateUserDetail(existingUserData, user, currentDate);

        userRepository.save(existingUser);

        saveUserProfile(profileImage, existingUser);

        var userDetail = objectMapper.readValue(userData, UserDetail.class);
        if (user.getProductType() == ApplicationConstant.Investment) {
            var investmentDetail = objectMapper.convertValue(userDetail.getInvestmentDetail(), InvestmentDetail.class);
            investmentDetail.setUserId(existingUser.getUserId());
            investmentDetail.setAccountId(getAccountNumber(ApplicationConstant.Investment));
            investmentDetail = iInvestmentService.addInvestmentService(investmentDetail);

            userDetail.setInvestmentDetail(investmentDetail);
        } else {
            var inventoryDetail = objectMapper.convertValue(userDetail.getCdProductInvestment(), CDProductInvestment.class);
            inventoryDetail.setUserId(existingUser.getUserId());
            inventoryDetail = ICDProductInvestmentService.addCDProductInvestmentService(inventoryDetail);

            userDetail.setCdProductInvestment(inventoryDetail);
        }

        userDetail.setAccountId(existingUser.getAccountId());
        return userDetail;
    }

    private void saveUserProfile(MultipartFile profileImage, User user) throws Exception {
        if (profileImage != null) {
            UserFile userFile = new UserFile();

            userFile.setFilePath(UploadNewFile(profileImage, user.getUserId(), "profile_", "profile"));
            userFile.setUserId(user.getUserId());

            userFileRepository.save(userFile);
        }
    }

    @NotNull
    private static User getUpdateUserDetail(Optional<User> existingUserData, User user, Timestamp currentDate) throws Exception {
        if (existingUserData.isEmpty())
            throw new Exception("User detail not found");

        var existingUser = existingUserData.get();

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setMobileNumber(user.getMobileNumber());
        existingUser.setAadharNumber(user.getAadharNumber());
        existingUser.setEmailId(user.getEmailId());
        existingUser.setDob(user.getDob());
        existingUser.setAddress(user.getAddress());
        existingUser.setUpdatedBy(1L);
        existingUser.setReferenceBy(user.getReferenceBy());
        existingUser.setUpdatedOn(currentDate);
        return existingUser;
    }

    private String UploadNewFile(MultipartFile newFile, long userId, String folderName, String fileType) throws Exception {
        if (newFile == null) return "";
        String filepath = "";
        try {
            filepath = fileManager.uploadFile(newFile, userId, folderName + new Date().getTime(), fileType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return filepath;
    }

    private List<User> readAndMapUserExcel(MultipartFile userExcel) throws Exception {
        List<User> users = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(userExcel.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> columnMap = new HashMap<>();

            extractedHeader(sheet, columnMap);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row.getCell(columnMap.get("firstname")).getStringCellValue().isEmpty()
                        || row.getCell(columnMap.get("firstname")).getStringCellValue() == null) {
                    break;
                }
                users.add(mappedUserData(columnMap, row));
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return users;
    }

    private User mappedUserData(Map<String, Integer> columnMap, Row row) {
        User user = new User();
        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        if (columnMap.containsKey("firstname")) {
            user.setFirstName(row.getCell(columnMap.get("firstname")).getStringCellValue());
        }

        if (columnMap.containsKey("lastname")) {
            user.setLastName(row.getCell(columnMap.get("lastname")).getStringCellValue());
        }

        if (columnMap.containsKey("address")) {
            user.setAddress(row.getCell(columnMap.get("address")).getStringCellValue());
        }

        if (columnMap.containsKey("emailid")) {
            user.setEmailId(row.getCell(columnMap.get("emailid")).getStringCellValue());
        }

        if (columnMap.containsKey("referenceby")) {
            Cell cell = row.getCell(columnMap.get("referenceby"));
            if (cell.getCellType() == CellType.NUMERIC) {
                String referenceby = String.valueOf(cell.getNumericCellValue());

                referenceby = String.format("%.0f", cell.getNumericCellValue());
                user.setReferenceBy(referenceby);
            } else {
                user.setReferenceBy(cell.getStringCellValue());
            }
        }

        if (columnMap.containsKey("mobilenumber")) {
            Cell cell = row.getCell(columnMap.get("mobilenumber"));
            if (cell.getCellType() == CellType.NUMERIC) {
                String mobileNumber = String.valueOf(cell.getNumericCellValue());

                mobileNumber = String.format("%.0f", cell.getNumericCellValue());
                user.setMobileNumber(mobileNumber);
            } else {
                user.setMobileNumber(cell.getStringCellValue());
            }
        }

        if (columnMap.containsKey("aadharnumber")) {
            Cell cell = row.getCell(columnMap.get("aadharnumber"));
            if (cell.getCellType() == CellType.NUMERIC) {
                String aadharNumber = String.valueOf(cell.getNumericCellValue());

                aadharNumber = String.format("%.0f", cell.getNumericCellValue());
                user.setAadharNumber(aadharNumber);
            } else {
                user.setAadharNumber(cell.getStringCellValue());
            }
        }

        user.setCreatedBy(1L);
        user.setUpdatedBy(1L);
        user.setCreatedOn(currentDate);
        user.setUpdatedOn(currentDate);

        return user;
    }

    private void extractedHeader(Sheet sheet, Map<String, Integer> columnMap) {
        Row headerRow = sheet.getRow(0);
        for (int cellIndex = 0; cellIndex < headerRow.getPhysicalNumberOfCells(); cellIndex++) {
            String header = headerRow.getCell(cellIndex).getStringCellValue().toLowerCase();
            columnMap.put(header, cellIndex);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String addUserAndInvestmentExcelService(MultipartFile userExcel) throws Exception {
        try {
            List<UserDetail> userDetails = readAndMapUserAndInvestmentExcel(userExcel);
            if (!userDetails.isEmpty()) {
                userDetails.forEach(x -> {
                    User user = objectMapper.convertValue(x, User.class);
                    try {
                        user = addUserDetail(user);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    InvestmentDetail investmentDetail = objectMapper.convertValue(x.getInvestmentDetail(), InvestmentDetail.class);
                    investmentDetail.setUserId(user.getUserId());

                    try {
                        addInvestmentDetail(investmentDetail);
                    } catch (Exception e) {
                    }
                });
            }

            return "user inserted successfully";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private User addUserDetail(User user) throws Exception {
        var existingUser = userRepository.getUserByEmailOrMobile(user.getMobileNumber(), user.getAadharNumber());

        if (existingUser == null) {
            user.setAccountId("");
            user.setLastName("");
            user = userRepository.save(user);
            return user;
        } else {
            return existingUser;
        }
    }

    private void addInvestmentDetail(InvestmentDetail investmentDetail) throws Exception {
        var existingInvestment = investmentRepository.getInvestmentByUserId(investmentDetail.getUserId());

        if (existingInvestment == null || existingInvestment.isEmpty()) {
            updateInvestmentDetail(investmentDetail);
            iInvestmentService.addInvestmentService(investmentDetail);
        } else {
            if (investmentDetail.getMonth() == 0)
                throw new Exception("Please select month");

            if (investmentDetail.getYear() == 0)
                throw new Exception("Please select year");

            var existingInvestmentDetail = existingInvestment.size() == 1 ? existingInvestment.get(0)
                    : findCurrentInvestment(existingInvestment, investmentDetail.getAccountId());
            try {
                var paymentDetails = objectMapper.readValue(existingInvestmentDetail.getPaymentDetail(), new TypeReference<List<PaymentDetail>>() {
                });
                var currentPayment = findCurrentPayment(paymentDetails, investmentDetail);
                currentPayment.setPaid(true);

                existingInvestmentDetail.setPaymentDetail(objectMapper.writeValueAsString(paymentDetails));
                existingInvestmentDetail.setPaidInstallment(existingInvestmentDetail.getPaidInstallment() + 1);
                existingInvestmentDetail.setLastPaymentAmount(currentPayment.getAmount());

                investmentRepository.save(existingInvestmentDetail);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    private InvestmentDetail findCurrentInvestment(List<InvestmentDetail> investmentDetails, String accountId) throws Exception {
        if (accountId == null || accountId.isEmpty())
            throw new Exception("Please provide account number");

        return investmentDetails.stream().filter(x -> x.getAccountId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new Exception("Investment detail not found"));
    }

    private PaymentDetail findCurrentPayment(List<PaymentDetail> paymentDetails, InvestmentDetail investmentDetail) throws Exception {
        return paymentDetails.stream().filter(x -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(x.getPaymentDate());
            return (calendar.get(Calendar.MONTH) + 1 == investmentDetail.getMonth() && calendar.get(Calendar.YEAR) == investmentDetail.getYear());
        }).findFirst().orElseThrow(() -> new Exception("Payment not found for the specified month and year"));
    }

    private void updateInvestmentDetail(InvestmentDetail investmentDetail) throws Exception {
        investmentDetail.setLastPaymentDate(getNewDate(investmentDetail.getIstPaymentDate(), investmentDetail.getPeriod() - 1));
        investmentDetail.setPrincipalAmount(investmentDetail.getInvestmentAmount() * 0.05);
        investmentDetail.setProfitAmount(investmentDetail.getTotalProfitAmount() - investmentDetail.getPrincipalAmount());
        investmentDetail.setInvestmentDate(getNewDate(investmentDetail.getIstPaymentDate(), -1 * investmentDetail.getPaidInstallment()));
        investmentDetail.setAccountId(getAccountNumber(ApplicationConstant.Investment));
    }

    private Date getNewDate(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.MONTH, month);

        return calendar.getTime();
    }

    private List<UserDetail> readAndMapUserAndInvestmentExcel(MultipartFile userExcel) throws Exception {
        List<UserDetail> userDetails = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(userExcel.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> columnMap = new HashMap<>();

            extractedHeader(sheet, columnMap);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row.getCell(columnMap.get("name")).getStringCellValue().isEmpty()
                        || row.getCell(columnMap.get("name")).getStringCellValue() == null) {
                    break;
                }
                userDetails.add(mappedUserAndInvestmentData(columnMap, row));
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return userDetails;
    }

    private UserDetail mappedUserAndInvestmentData(Map<String, Integer> columnMap, Row row) {
        UserDetail userDetail = new UserDetail();
        userDetail.setInvestmentDetail(new InvestmentDetail());
        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        if (columnMap.containsKey("name")) {
            userDetail.setFirstName(row.getCell(columnMap.get("name")).getStringCellValue());
        }

        if (columnMap.containsKey("address")) {
            userDetail.setAddress(row.getCell(columnMap.get("address")).getStringCellValue());
        }

        if (columnMap.containsKey("reference by")) {
            Cell cell = row.getCell(columnMap.get("reference by"));
            if (cell.getCellType() == CellType.NUMERIC) {
                String referenceby = String.valueOf(cell.getNumericCellValue());

                referenceby = String.format("%.0f", cell.getNumericCellValue());
                userDetail.setReferenceBy(referenceby);
            } else {
                userDetail.setReferenceBy(cell.getStringCellValue());
            }
        }

        if (columnMap.containsKey("mobile number")) {
            Cell cell = row.getCell(columnMap.get("mobile number"));
            if (cell.getCellType() == CellType.NUMERIC) {
                String mobileNumber = String.valueOf(cell.getNumericCellValue());

                mobileNumber = String.format("%.0f", cell.getNumericCellValue());
                userDetail.setMobileNumber(mobileNumber);
            } else {
                userDetail.setMobileNumber(cell.getStringCellValue());
            }
        }

        if (columnMap.containsKey("aadhar number")) {
            Cell cell = row.getCell(columnMap.get("aadhar number"));
            if (cell.getCellType() == CellType.NUMERIC) {
                String aadharNumber = String.valueOf(cell.getNumericCellValue());

                aadharNumber = String.format("%.0f", cell.getNumericCellValue());
                userDetail.setAadharNumber(aadharNumber);
            } else {
                userDetail.setAadharNumber(cell.getStringCellValue());
            }
        }

        if (columnMap.containsKey("monthly payment")) {
            userDetail.getInvestmentDetail().setTotalProfitAmount(row.getCell(columnMap.get("monthly payment")).getNumericCellValue());
        }

        if (columnMap.containsKey("period")) {
            userDetail.getInvestmentDetail().setPeriod((int) row.getCell(columnMap.get("period")).getNumericCellValue());
        }

        if (columnMap.containsKey("paid installment")) {
            userDetail.getInvestmentDetail().setPaidInstallment((int) row.getCell(columnMap.get("paid installment")).getNumericCellValue());
        }

        if (columnMap.containsKey("first payment")) {
            userDetail.getInvestmentDetail().setIstPaymentDate(row.getCell(columnMap.get("first payment")).getDateCellValue());
        }

        if (columnMap.containsKey("investment amount")) {
            userDetail.getInvestmentDetail().setInvestmentAmount(row.getCell(columnMap.get("investment amount")).getNumericCellValue());
        }

        if (columnMap.containsKey("month")) {
            userDetail.getInvestmentDetail().setMonth((int) row.getCell(columnMap.get("month")).getNumericCellValue());
        }

        if (columnMap.containsKey("year")) {
            userDetail.getInvestmentDetail().setYear((int) row.getCell(columnMap.get("year")).getNumericCellValue());
        }

        userDetail.setCreatedBy(1L);
        userDetail.setUpdatedBy(1L);
        userDetail.setCreatedOn(currentDate);
        userDetail.setUpdatedOn(currentDate);

        return userDetail;
    }

    public byte[] downloadUserExcelService(FilterModel filterModel) throws Exception {
        var users = filterUserService(filterModel);
        if (users == null || users.isEmpty()) {
            throw new Exception("Record not found");
        }
        return getUserExcelBytes(users);
    }

    public byte[] getUserExcelBytes(List<User> users) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Mobile Number");
        headerRow.createCell(2).setCellValue("Aadhar Number");
        headerRow.createCell(3).setCellValue("Reference By");

        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getFirstName() + " " + user.getLastName());
            row.createCell(1).setCellValue(user.getMobileNumber());
            row.createCell(2).setCellValue(user.getAadharNumber());
            row.createCell(3).setCellValue(user.getReferenceBy());

        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            workbook.close();
            return byteArrayOutputStream.toByteArray();
        }
    }

}