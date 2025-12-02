FROM ubuntu:latest
LABEL authors="guswn"

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y \
    procps \
    curl \
    vim \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*


ENTRYPOINT ["top", "-b"]