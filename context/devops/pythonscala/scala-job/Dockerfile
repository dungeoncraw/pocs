FROM eclipse-temurin:21-jdk-jammy AS builder

RUN apt-get update \
    && apt-get install --yes --no-install-recommends ca-certificates curl gzip \
    && rm -rf /var/lib/apt/lists/*

RUN set -eux; \
    case "$(dpkg --print-architecture)" in \
      amd64) SCALA_CLI_ARCH="x86_64" ;; \
      arm64) SCALA_CLI_ARCH="aarch64" ;; \
      *) echo "Architecture not supported by Scala CLI" >&2; exit 1 ;; \
    esac; \
    curl -fL \
      "https://github.com/VirtusLab/scala-cli/releases/latest/download/scala-cli-${SCALA_CLI_ARCH}-pc-linux.gz" \
      | gzip -d > /usr/local/bin/scala-cli; \
    chmod 0755 /usr/local/bin/scala-cli; \
    scala-cli version

WORKDIR /src
COPY src/Main.scala ./Main.scala

RUN java -version \
    && mkdir -p /out \
    && scala-cli --power package Main.scala \
       --assembly \
       --server=false \
       --jvm system \
       --force \
       --main-class localjob.Main \
       -o /out/app.jar \
    && test -s /out/app.jar

FROM eclipse-temurin:21-jre-jammy

ARG SUPERCRONIC_VERSION=v0.2.48

ENV HOME=/tmp \
    TZ=America/Sao_Paulo

RUN apt-get update \
    && apt-get install --yes --no-install-recommends ca-certificates curl tzdata \
    && rm -rf /var/lib/apt/lists/*

RUN set -eux; \
    case "$(dpkg --print-architecture)" in \
      amd64) \
        SUPERCRONIC_BINARY="supercronic-linux-amd64"; \
        SUPERCRONIC_SHA1="016b7c9aebfc8d9fd9526e8ba33b191fc524485f"; \
        ;; \
      arm64) \
        SUPERCRONIC_BINARY="supercronic-linux-arm64"; \
        SUPERCRONIC_SHA1="2ab9b3bdcf290f60b59700aad876b6e68f3a6b06"; \
        ;; \
      *) \
        echo "Architecture not supported by Supercronic" >&2; \
        exit 1; \
        ;; \
    esac; \
    curl -fsSL \
      "https://github.com/aptible/supercronic/releases/download/${SUPERCRONIC_VERSION}/${SUPERCRONIC_BINARY}" \
      -o /usr/local/bin/supercronic; \
    echo "${SUPERCRONIC_SHA1}  /usr/local/bin/supercronic" | sha1sum -c -; \
    chmod 0755 /usr/local/bin/supercronic

RUN groupadd --system --gid 10001 scala-job \
    && useradd \
        --system \
        --uid 10001 \
        --gid scala-job \
        --home-dir /nonexistent \
        --shell /usr/sbin/nologin \
        scala-job \
    && mkdir -p /opt/scala-job /etc/supercronic /data \
    && chown -R scala-job:scala-job /opt/scala-job /etc/supercronic /data

COPY --from=builder --chown=10001:10001 /out/app.jar /opt/scala-job/app.jar
COPY --chown=10001:10001 docker/run-job.sh /opt/scala-job/run-job.sh
COPY --chown=10001:10001 docker/entrypoint.sh /opt/scala-job/entrypoint.sh

RUN chmod 0755 /opt/scala-job/run-job.sh /opt/scala-job/entrypoint.sh

USER 10001:10001

EXPOSE 9000

ENTRYPOINT ["/opt/scala-job/entrypoint.sh"]
