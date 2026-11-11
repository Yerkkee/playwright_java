package com.yerkkee.listeners;

import com.microsoft.playwright.Page;
import com.yerkkee.base.PlaywrightManager;
import com.yerkkee.config.TestConfig;
import com.yerkkee.utils.AllureEnvironmentWriter;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import lombok.extern.slf4j.Slf4j;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Logs the test lifecycle and attaches debugging artifacts to Allure when a test fails:
 * full-page screenshot, Playwright trace (.zip) and video (.webm).
 * Passed tests keep nothing - trace and video are discarded in BaseTest#closeBrowserSession.
 *
 * <p>TestNG calls these hooks in the thread that ran the test, so the ThreadLocal page
 * from {@link PlaywrightManager} belongs to exactly this test, even in parallel runs.
 * They also run before {@code @AfterMethod}, while the page is still open.
 */
@Slf4j
public class AllureArtifactsListener implements ITestListener, ISuiteListener {

    private static final Path TRACE_DIR = Paths.get("target", "traces");

    @Override
    public void onStart(ISuite suite) {
        AllureEnvironmentWriter.write();
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info(">>> START  {}", testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("<<< PASSED {} ({} ms)", testName(result), duration(result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("<<< SKIPPED {}", testName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("<<< FAILED {} ({} ms): {}", testName(result), duration(result),
                result.getThrowable() == null ? "" : result.getThrowable().toString());

        // Each step is isolated: a crashed page must not prevent the trace/video from being saved
        if (TestConfig.screenshotOnFailure()) {
            safely("screenshot", this::attachScreenshot);
        }
        if (TestConfig.traceOnFailure()) {
            safely("trace", () -> attachTrace(result));
        }
        if (TestConfig.videoOnFailure()) {
            safely("video", this::attachVideo);
        }
    }

    private void attachScreenshot() {
        PlaywrightManager.currentPage().ifPresent(page -> {
            byte[] png = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.attachment("Screenshot on failure", "image/png",
                    new ByteArrayInputStream(png), AttachmentOptions.withFileExtension("png"));
        });
    }

    private void attachTrace(ITestResult result) throws IOException {
        Files.createDirectories(TRACE_DIR);
        Path target = TRACE_DIR.resolve(fileSafeName(result) + "_" + System.currentTimeMillis() + ".zip");
        PlaywrightManager.saveTrace(target).ifPresent(trace -> {
            attachFile("Playwright trace (open at trace.playwright.dev)", "application/zip", trace, "zip");
            log.info("Trace saved: {}", trace.toAbsolutePath());
        });
    }

    /** Video is written only when the context closes, so the listener closes it here. */
    private void attachVideo() {
        PlaywrightManager.closeSession(true)
                .ifPresent(video -> attachFile("Video", "video/webm", video, "webm"));
    }

    private static void attachFile(String name, String mimeType, Path file, String extension) {
        try (InputStream content = Files.newInputStream(file)) {
            Allure.attachment(name, mimeType, content, AttachmentOptions.withFileExtension(extension));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot attach " + file, e);
        }
    }

    private static void safely(String artifact, ThrowingRunnable action) {
        try {
            action.run();
        } catch (Exception e) {
            log.warn("Could not attach {}: {}", artifact, e.getMessage());
        }
    }

    private static String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
    }

    private static String fileSafeName(ITestResult result) {
        return testName(result).replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private static long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
