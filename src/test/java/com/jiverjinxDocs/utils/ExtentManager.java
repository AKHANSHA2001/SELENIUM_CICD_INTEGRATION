package com.jiverjinxDocs.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentManager {

    private static ExtentReports extent;
    private static String timestamp;
    private static String htmlReportPath;
    private static String pdfReportPath;

    public static ExtentReports getInstance() {
        if (extent == null) {

            // ── Timestamp for this run ────────────────────────────────────────
            timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-M-yyyy_HH-mm-ss"));

            // ── Create run folder inside test-output ──────────────────────────
            String baseDir = System.getProperty("user.dir") + "/test-output/Run_" + timestamp + "/";
            new java.io.File(baseDir).mkdirs();

            // ── File paths ────────────────────────────────────────────────────
            htmlReportPath = baseDir + "ExtentReport_" + timestamp + ".html";
            pdfReportPath  = baseDir + "ExtentReport_" + timestamp + ".pdf";

            // ── HTML report (ExtentSparkReporter) ─────────────────────────────
            ExtentSparkReporter spark = new ExtentSparkReporter(htmlReportPath);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("JiverJinx Docs — Link Redirect Audit");
            spark.config().setReportName("Redirect Test Report");
            spark.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Project",     "JiverJinx Docs Automation");
            extent.setSystemInfo("Tester",      System.getProperty("user.name"));
            extent.setSystemInfo("Environment", "Production");
            extent.setSystemInfo("Base URL",    "https://docs.jiverjinx.com");
            extent.setSystemInfo("Browser",     "Chrome");
        }
        return extent;
    }

    // ── Convert HTML report → PDF after flush ─────────────────────────────────
    public static void generatePdfReport() {
        if (htmlReportPath == null || pdfReportPath == null) return;
        try {
            com.itextpdf.html2pdf.HtmlConverter.convertToPdf(
                new java.io.FileInputStream(htmlReportPath),
                new java.io.FileOutputStream(pdfReportPath)
            );
            System.out.println("📄 PDF Report generated at: " + pdfReportPath);
        } catch (Exception e) {
            System.err.println("⚠️  PDF generation failed: " + e.getMessage());
        }
    }

    public static String getHtmlReportPath() { return htmlReportPath; }
    public static String getPdfReportPath()  { return pdfReportPath;  }
}
