package org.graylog2.alarmcallbacks.jabber.testcontainers;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.joschi.jadconfig.util.Size;
import okhttp3.HttpUrl;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.Wait;

import java.time.Duration;
import java.util.function.Consumer;

public class GraylogContainer extends GenericContainer<GraylogContainer> {
    private static final int GRAYLOG_HTTP_PORT = 9000;

    public GraylogContainer() {
        super("graylog-plugin-jabber:latest");
    }

    @Override
    protected void configure() {
        this.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("Graylog")))
                .withCreateContainerCmdModifier((Consumer<CreateContainerCmd>) cmd -> cmd.withMemory(Size.megabytes(1024L).toBytes()))
                .withEnv("GRAYLOG_SERVER_JAVA_OPTS", "-server -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=2")
                .withEnv("GRAYLOG_PASSWORD_SECRET", "supersecretpasswordpepper")
                .withEnv("GRAYLOG_ROOT_USERNAME", "admin")
                .withEnv("GRAYLOG_ROOT_PASSWORD_SHA2", "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918")
                .withEnv("GRAYLOG_MESSAGE_JOURNAL_ENABLED", "false")
                .withEnv("GRAYLOG_DEFAULT_MESSAGE_OUTPUT_CLASS", "org.graylog2.outputs.DiscardMessageOutput")
                // Try to reduce Graylog's resource consumption as much as possible
                .withEnv("GRAYLOG_WEB_ENABLE", "false")
                .withEnv("GRAYLOG_DISABLE_SIGAR", "true")
                .withEnv("GRAYLOG_INPUTBUFFER_PROCESSORS", "1")
                .withEnv("GRAYLOG_PROCESSBUFFER_PROCESSORS", "1")
                .withEnv("GRAYLOG_OUTPUTBUFFER_PROCESSORS", "1")
                .withEnv("GRAYLOG_RING_SIZE", "2")
                .withEnv("GRAYLOG_INPUTBUFFER_RING_SIZE", "2")
                .withEnv("GRAYLOG_CONTENT_PACKS_LOADER_ENABLED", "false")
                .withEnv("GRAYLOG_PROXIED_REQUESTS_THREAD_POOL_SIZE", "2")
                .withExposedPorts(GRAYLOG_HTTP_PORT)
                .withNetworkAliases("graylog")
                .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(5L)));
    }

    public HttpUrl getGraylogUrl() {
        return new HttpUrl.Builder()
                .scheme("http")
                .username("admin")
                .password("admin")
                .host(this.getContainerIpAddress())
                .port(this.getMappedPort(GRAYLOG_HTTP_PORT))
                .encodedPath("/api/")
                .build();
    }
}
