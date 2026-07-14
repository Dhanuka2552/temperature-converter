# 🌡️ Temp Converter

Hey! So this is a little side project I built to mess around with Spring Boot + MongoDB on the backend, paired with a super simple Node/Express + HTML frontend. It's basically a temperature converter — Celsius, Fahrenheit, Kelvin — but I threw in some extra stuff like conversion history, usage stats, and a "hey that's dangerously hot" warning, all locked behind an API key.

Nothing fancy, just wanted a real full-stack project to practice with. Feel free to poke around.

## What it does

- Converts between **Celsius, Fahrenheit, and Kelvin** (won't let you go below absolute zero, obviously)
- Saves every conversion to **MongoDB** so you've got a history — you can filter it by unit too
- Has a "clear history" button/endpoint if you want to wipe the slate
- Tracks basic **usage stats** — how many conversions total, and which conversion pair you use most
- Flags anything above 38°C as a **safety warning** (dangerously hot)
- Every backend route is protected with a simple **API key** (`X-API-KEY` header)
- Comes with a basic **web UI** so you're not stuck curling everything
- **Dockerized**, so you can spin the whole thing up with one command if you don't want to install stuff locally

## Built with

| Layer      | Stuff I used |
|------------|--------------|
| Backend    | Java 21, Spring Boot 3.5, Spring Data MongoDB, Lombok |
| Database   | MongoDB 7 |
| Frontend   | Node.js, Express, plain HTML/CSS/JS |
| Build      | Maven |
| Deployment | Docker & Docker Compose |

## How it's organized

```
temp-converter/
├── src/main/java/com/example/tempconverter/
│   ├── controller/      # REST endpoints
│   ├── service/         # conversion logic lives here
│   ├── model/           # Mongo documents (TemperatureLog, ApiKey)
│   ├── repository/      # Spring Data Mongo repos
│   ├── exception/       # custom exceptions
│   └── config/          # API key setup
├── UI/                  # the frontend (server.js, index.html, style.css, script.js)
├── docker-compose.yml   # spins up mongo + backend + ui together
└── Dockerfile           # multi-stage build for the backend
```

## Wanna run it?

### Easiest way — Docker Compose

Just do:

```bash
docker compose up --build
```

That gets you Mongo, the backend, and the UI all running together:

- Backend → `http://localhost:8081`
- UI → `http://localhost:3000`
- MongoDB → `localhost:27017`

### Or run it locally

You'll need Java 21, Maven, Node.js, and a MongoDB instance running somewhere.

**Backend:**
```bash
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd UI
npm install
node server.js
```

Backend defaults to port `8081`, UI defaults to `3000`.

### A couple of env vars you can set

| Variable | Default | What it's for |
|----------|---------|----------------|
| `SPRING_DATA_MONGODB_URI` | `mongodb://localhost:27017/temp_db` | Mongo connection string |
| `SPRING_API_KEY` | `SUPER-SECRET-DEV-KEY-2552` | the key you need to send in `X-API-KEY` |

> ⚠️ Heads up — that default API key is just for messing around locally. Please don't leave it like that if you ever deploy this somewhere public 😅

## API cheat sheet

Every single endpoint below needs an `X-API-KEY` header, no exceptions.

| Method | Endpoint | What it does |
|--------|----------|----------------|
| `POST`   | `/api/temperatures/convert?value={value}&from={unit}&to={unit}` | Converts a temp and logs it to history |
| `GET`    | `/api/temperatures/history` | Grabs your whole conversion history |
| `DELETE` | `/api/temperatures/history` | Wipes the history clean |
| `GET`    | `/api/temperatures/history/filter?unit={unit}` | History filtered by input unit |
| `GET`    | `/api/temperatures/warning-check?value={value}&unit={unit}` | Tells you if a temp is dangerously hot |
| `GET`    | `/api/temperatures/stats` | Total conversions + your most-used conversion pair |

For `unit` / `from` / `to`, you can use `C`/`CELSIUS`, `F`/`FAHRENHEIT`, or `K`/`KELVIN` — case doesn't matter.

**Quick example:**
```bash
curl -X POST "http://localhost:8081/api/temperatures/convert?value=100&from=C&to=F" \
  -H "X-API-KEY: SUPER-SECRET-DEV-KEY-2552"
```

## License

Just a personal learning project — loosely adapted from a Spring Boot + MongoDB setup I was studying. Do whatever you want with it.