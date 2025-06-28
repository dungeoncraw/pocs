FROM node:alpine
WORKDIR /app
COPY package.json .
RUN npm install
COPY src .
EXPOSE 80

CMD ["node", "server.js"]