package com.yerkkee.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.Video;
import com.yerkkee.config.TestConfig;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Owns all Playwright objects, one set per test thread.
 *
 * <p>Why ThreadLocal: Playwright objects are not thread-safe. With TestNG {@code parallel="methods"}
 * each worker thread needs its own Playwright, Browser, BrowserContext and Page.
 *
 * <p>Lifecycle:
 * <ul>
 *   <li>Playwright + Browser - created lazily once per thread and reused (launching a browser is slow);</li>
 *   <li>BrowserContext + Page - created for every test method, so cookies/storage never leak between tests;</li>
 *   <li>everything is closed at the end of the suite via {@link #closeAll()}.</li>
 * </ul>
 */
@Slf4j
public final class PlaywrightManager {

    private static final Path VIDEO_DIR = Paths.get("target", "videos");

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> TRACING = ThreadLocal.withInitial(() -> false);

    /** Every Playwright created by any thread, so the suite can close them all at the end. */
    private static final Queue<Playwright> ALL_PLAYWRIGHTS = new ConcurrentLinkedQueue<>();

    private PlaywrightManager() {
        throw new UnsupportedOperationException("Utility class");
    }

    /** Opens a fresh context and page for the current test. */
    public static void startSession() {
        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setViewportSize(TestConfig.viewportWidth(), TestConfig.viewportHeight());
        if (TestConfig.videoOnFailure()) {
            // Video can only be enabled when the context is created; it is deleted later for passed tests
            options.setRecordVideoDir(VIDEO_DIR);
        }

        BrowserContext context = browser().newContext(options);
        context.setDefaultTimeout(TestConfig.defaultTimeout());
        context.setDefaultNavigationTimeout(TestConfig.navigationTimeout());

        if (TestConfig.traceOnFailure()) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
            TRACING.set(true);
        }

        CONTEXT.set(context);
        PAGE.set(context.newPage());
        log.debug("Browser context created");
    }

    /** Page of the current test. Fails fast if called outside a test session. */
    public static Page page() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("No active Playwright page in thread '"
                    + Thread.currentThread().getName() + "'. Was startSession() called?");
        }
        return page;
    }

    /** Page of the current test, if any (listeners use it - a failure may happen before the page exists). */
    public static Optional<Page> currentPage() {
        return Optional.ofNullable(PAGE.get());
    }

    /**
     * Stops tracing and writes the trace archive.
     *
     * @return path to the .zip, or empty if tracing is not active
     */
    public static Optional<Path> saveTrace(Path target) {
        BrowserContext context = CONTEXT.get();
        if (context == null || !TRACING.get()) {
            return Optional.empty();
        }
        context.tracing().stop(new Tracing.StopOptions().setPath(target));
        TRACING.set(false);
        return Optional.of(target);
    }

    /**
     * Closes the current context. Safe to call twice: the listener closes it on failure
     * (to grab the video), then {@code @AfterMethod} calls it again as a no-op.
     *
     * @param keepVideo true - keep the video file and return its path; false - delete it
     * @return path to the finished video when {@code keepVideo} is true and video is recorded
     */
    public static Optional<Path> closeSession(boolean keepVideo) {
        BrowserContext context = CONTEXT.get();
        if (context == null) {
            return Optional.empty();
        }
        Page page = PAGE.get();
        Video video = page != null ? page.video() : null;
        try {
            if (TRACING.get()) {
                context.tracing().stop(); // no path -> trace is discarded
            }
            context.close(); // the video file is finalized only after the context is closed
        } finally {
            CONTEXT.remove();
            PAGE.remove();
            TRACING.remove();
        }

        if (video == null) {
            return Optional.empty();
        }
        if (keepVideo) {
            return Optional.of(video.path());
        }
        video.delete();
        return Optional.empty();
    }

    /** Closes every browser and Playwright instance created during the run. */
    public static void closeAll() {
        Playwright playwright;
        while ((playwright = ALL_PLAYWRIGHTS.poll()) != null) {
            try {
                playwright.close(); // also closes its browsers
            } catch (RuntimeException e) {
                log.warn("Failed to close Playwright: {}", e.getMessage());
            }
        }
        BROWSER.remove();
        PLAYWRIGHT.remove();
        log.info("All browsers closed");
    }

    private static Browser browser() {
        Browser browser = BROWSER.get();
        if (browser != null && browser.isConnected()) {
            return browser;
        }

        Playwright playwright = PLAYWRIGHT.get();
        if (playwright == null) {
            playwright = Playwright.create();
            // SauceDemo marks elements with data-test, so getByTestId() must look at that attribute
            playwright.selectors().setTestIdAttribute("data-test");
            PLAYWRIGHT.set(playwright);
            ALL_PLAYWRIGHTS.add(playwright);
        }

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(TestConfig.headless())
                .setSlowMo(TestConfig.slowMo());

        browser = switch (TestConfig.browser()) {
            case CHROMIUM -> playwright.chromium().launch(options);
            case FIREFOX -> playwright.firefox().launch(options);
            case WEBKIT -> playwright.webkit().launch(options);
        };
        BROWSER.set(browser);
        log.info("Browser launched: {} {} (headless={})",
                TestConfig.browser(), browser.version(), TestConfig.headless());
        return browser;
    }
}
