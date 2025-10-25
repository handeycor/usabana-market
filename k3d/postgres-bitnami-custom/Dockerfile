# Dockerfile
FROM docker.io/bitnami/postgresql:latest

# Copiar init.sql al directorio que el entrypoint de Bitnami revisa
COPY init.sql /docker-entrypoint-initdb.d/init.sql