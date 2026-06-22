FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --upgrade pip && \
    pip install \
    --default-timeout=100 \
    --retries=10 \
    --no-cache-dir \
    -r requirements.txt

COPY . .

EXPOSE 5001

CMD ["python", "app.py"]