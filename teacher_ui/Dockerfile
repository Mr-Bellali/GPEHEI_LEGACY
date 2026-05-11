# https://medium.com/@itsuki.enjoy/dockerize-a-next-js-app-4b03021e084d
FROM node:18

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 3000
CMD npm run dev