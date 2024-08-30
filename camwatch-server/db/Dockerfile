FROM postgres:latest

# Install pgAgent
RUN apt-get update && \
    apt-get install -y pgagent && \
    rm -rf /var/lib/apt/lists/*

# Copy entrypoint script
COPY entrypoint.sh tmp/entrypoint.sh
RUN chmod +x tmp/entrypoint.sh

# Use the default entrypoint from the postgres image
ENTRYPOINT ["tmp/entrypoint.sh"]

# Default command to start PostgreSQL
CMD ["postgres"]

