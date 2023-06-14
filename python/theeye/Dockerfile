# syntax=docker/dockerfile:1

FROM python:3
ENV PYTHONUNBUFFERED=1
WORKDIR /code
EXPOSE 8000
COPY requirements.txt /code/
COPY . /code/
RUN pip install -r requirements.txt
