package com.jiverjinxDocs.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.io.File;                                    // ✅ ADD
import java.nio.file.Files;                             // ✅ ADD
import java.nio.file.Paths;                             // ✅ ADD
import java.nio.file.StandardCopyOption;                // ✅ ADD
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentManager {
    private static ExtentReports extent;
    private static String timestamp;
    private static String htmlReportPath;
    private static String pdfReportPath;

    // ✅ ADD THESE 3 lines — fixed path Jenkins always knows
    private static final String FIXED_REPORT_DIR =
        System.getProperty("user.dir") + "/test-output/LatestReport/";
    private static final String FIXED_HTML = FIXED_REPORT_DIR + "ExtentReport.html";
    private static final String FIXED_PDF  = FIXED_REPORT_DIR + "ExtentReport.pdf";

    public static ExtentReports getInstance() {
        if (extent == null) {
            timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-M-yyyy_HH-mm-ss"));

            String baseDir = System.getProperty("user.dir")
                           + "/test-output/Run_" + timestamp + "/";
            new File(baseDir).mkdirs();
            new File(FIXED_REPORT_DIR).mkdirs(); // ✅ ADD THIS

            htmlReportPath = baseDir + "ExtentReport_" + timestamp + ".html";
            pdfReportPath  = baseDir + "ExtentReport_" + timestamp + ".pdf";

            ExtentSparkReporter spark = new ExtentSparkReporter(htmlReportPath);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("JiverJinx Docs — Link Redirect Audit");
            spark.config().setReportName("Redirect Test Report");
            spark.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Project",     "JiverJinx Docs Automation");
            extent.setSystemInfo("Tester",      System.getProperty("user.name"));
            // ✅ CHANGE THIS LINE — reads environment from Jenkins parameter
            extent.setSystemInfo("Environment", System.getProperty("environment", "Production"));
            extent.setSystemInfo("Base URL",    System.getProperty("docs.url", "https://docs.jiverjinx.com"));
            extent.setSystemInfo("Browser",     "Chrome");
        }
        return extent;
    }

    public static void generatePdfReport() {
        if (htmlReportPath == null || pdfReportPath == null) return;
        try {
            com.itextpdf.html2pdf.HtmlConverter.convertToPdf(
                new java.io.FileInputStream(htmlReportPath),
                new java.io.FileOutputStream(pdfReportPath)
            );
            System.out.println("📄 PDF Report generated at: " + pdfReportPath);

            // ✅ ADD THESE — copy to fixed location so Jenkins always finds it
            Files.copy(Paths.get(htmlReportPath),
                       Paths.get(FIXED_HTML),
                       StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Paths.get(pdfReportPath),
                       Paths.get(FIXED_PDF),
                       StandardCopyOption.REPLACE_EXISTING);
            System.out.println("📋 Report copied to: " + FIXED_HTML);

        } catch (Exception e) {
            System.err.println("⚠️  PDF generation failed: " + e.getMessage());
        }
    }

    public static String getHtmlReportPath() { return htmlReportPath; }
    public static String getPdfReportPath()  { return pdfReportPath;  }
}