FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY server.js index.html style.css script.js ./
USER node
EXPOSE 3000
CMD ["node", "server.js"]
